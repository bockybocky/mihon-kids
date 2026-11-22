package keiyoushi.kidsfilter

import java.util.concurrent.ConcurrentHashMap

/**
 * Three layers, in order: site mark, keyword, remote blacklist.
 * Blacklist is fetched at most once per process. A failed fetch becomes an empty list.
 */
object KidsFilter {
    val KEYWORDS = arrayOf(
        "工口",
        "色情",
        "成人",
        "限制级",
        "限制級",
        "R18",
        "R-18",
        "18禁",
        "十八禁",
        "紳士",
        "绅士",
        "H漫",
        "里番",
        "裏番",
        "エロ",
        "hentai",
    )

    const val BLACKLIST_URL = "https://raw.githubusercontent.com/bockybocky/mihon-kids/main/blacklist.json"
    const val FILTERED_PREFIX = "【已過濾】"

    private val blocked = ConcurrentHashMap.newKeySet<String>()
    private val lock = Any()

    @Volatile
    private var fetcher: (() -> String)? = null

    @Volatile
    private var blacklistLoaded = false

    private val blacklist = HashMap<String, List<String>>()

    fun note(source: String) {
        blocked.add("noted|$source")
    }

    fun installFetcher(fetch: () -> String) {
        fetcher = fetch
    }

    fun reset() {
        synchronized(lock) {
            fetcher = null
            blacklistLoaded = false
            blacklist.clear()
            blocked.clear()
        }
    }

    fun ensureBlacklist() {
        if (blacklistLoaded) return
        synchronized(lock) {
            if (blacklistLoaded) return
            val raw = try {
                fetcher?.invoke()
            } catch (_: Exception) {
                null
            }
            blacklist.clear()
            blacklist.putAll(loadBlacklist(raw))
            blacklistLoaded = true
        }
    }

    fun loadBlacklist(json: String?): Map<String, List<String>> {
        if (json.isNullOrBlank()) return emptyMap()
        return try {
            parseBlacklist(json)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun prefixTitle(title: String): String = if (title.startsWith(FILTERED_PREFIX)) title else FILTERED_PREFIX + title

    fun containsKeyword(texts: List<String?>): Boolean {
        val blob = texts.filterNotNull().joinToString("\n")
        if (blob.isEmpty()) return false
        return KEYWORDS.any { blob.contains(it, ignoreCase = true) }
    }

    fun blacklistHit(source: String, urlOrPath: String?): Boolean {
        if (urlOrPath.isNullOrEmpty()) return false
        val list = blacklist[source] ?: return false
        return list.any { fragment -> fragment.isNotEmpty() && urlOrPath.contains(fragment) }
    }

    fun remember(source: String, key: String) {
        if (key.isNotEmpty()) blocked.add("$source|$key")
    }

    fun known(source: String, key: String?): Boolean {
        if (key.isNullOrEmpty()) return false
        if (blocked.contains("$source|$key")) return true
        return blocked.any { entry ->
            if (!entry.startsWith("$source|")) return@any false
            val saved = entry.removePrefix("$source|")
            saved.length >= 3 && key.contains(saved)
        }
    }

    fun restrictHits(value: Int?, display: String?): Boolean {
        if (value == null && display == null) return false
        if (value != null && value != 0) return true
        val shown = display ?: return false
        return shown.contains("限制") || shown.contains("18") || shown.contains("成人")
    }

    fun judge(
        source: String,
        urlOrPath: String?,
        texts: List<String?>,
        siteMarked: Boolean = false,
        restrictValue: Int? = null,
        restrictDisplay: String? = null,
    ): Boolean {
        ensureBlacklist()
        val hit = siteMarked ||
            (source == "kaobeimanhua" && restrictHits(restrictValue, restrictDisplay)) ||
            containsKeyword(texts) ||
            blacklistHit(source, urlOrPath)
        if (hit && !urlOrPath.isNullOrEmpty()) remember(source, urlOrPath)
        return hit
    }

    fun refuse(source: String, urlOrPath: String?, texts: List<String?> = emptyList()) {
        ensureBlacklist()
        if (known(source, urlOrPath) || blacklistHit(source, urlOrPath) || containsKeyword(texts)) {
            throw Exception(FILTERED_PREFIX)
        }
    }

    fun dm5SiteMarked(html: String): Boolean {
        val blocks = CATEGORY_BLOCK.findAll(html).map { it.value }.toList()
        if (blocks.isEmpty()) return false
        val scope = blocks.joinToString("\n")
        if (scope.contains("/manhua-18-x/")) return true
        return CATEGORY_TEXT.containsMatchIn(scope)
    }

    fun judgeDm5(html: String, url: String? = null): Boolean {
        val title = group(DETAIL_TITLE_MOBILE, html) ?: group(DETAIL_TITLE_DESKTOP, html)
        val desc = group(DETAIL_DESC_MOBILE, html) ?: group(DETAIL_DESC_DESKTOP, html)
        val cats = CATEGORY_BLOCK.findAll(html).joinToString("\n") { it.value }
        return judge("dm5", url, listOf(title, desc, cats), siteMarked = dm5SiteMarked(html))
    }

    fun judgeBili(html: String, url: String? = null): Boolean {
        val title = group(BILI_TITLE, html)
        val alias = group(BILI_ALIAS, html)
        val summary = group(BILI_SUMMARY, html)
        val tags = BILI_TAG.findAll(html).joinToString(" ") { it.groupValues[1] }
        return judge("bilimanga", url, listOf(title, alias, summary, tags))
    }

    fun judgeMycomic(html: String, url: String? = null): Boolean {
        val title = stripTags(group(MYCOMIC_TITLE, html)) ?: stripTags(group(MYCOMIC_H1, html))
        val desc = stripTags(group(MYCOMIC_DESC, html))
        return judge("mycomic", url, listOf(title, desc))
    }

    fun judgeKaobei(json: String): Boolean {
        val name = group(JSON_NAME, json)
        val alias = group(JSON_ALIAS, json)
        val brief = group(JSON_BRIEF, json)
        val path = group(JSON_PATH, json)
        val themeBlock = group(JSON_THEME, json).orEmpty()
        val themes = JSON_STRING.findAll(themeBlock).joinToString(" ") { it.groupValues[1] }
        val restrict = group(RESTRICT_OBJECT, json)
        val value: Int?
        val display: String?
        if (restrict == null || restrict == "null") {
            value = null
            display = null
        } else {
            value = group(JSON_VALUE, restrict)?.toIntOrNull()
            display = group(JSON_DISPLAY, restrict)
        }
        return judge(
            "kaobeimanhua",
            path,
            listOf(name, alias, brief, themes, display),
            restrictValue = value,
            restrictDisplay = display,
        )
    }

    private fun group(pattern: Regex, text: String): String? = pattern.find(text)?.groupValues?.get(1)

    private fun stripTags(raw: String?): String? {
        if (raw == null) return null
        return raw.replace(TAG, "").trim().ifEmpty { null }
    }

    private fun parseBlacklist(json: String): Map<String, List<String>> {
        val out = LinkedHashMap<String, List<String>>()
        for (entry in BLACKLIST_ENTRY.findAll(json)) {
            val key = entry.groupValues[1]
            val items = JSON_STRING.findAll(entry.groupValues[2]).map { it.groupValues[1] }.toList()
            out[key] = items
        }
        return out
    }

    private val CATEGORY_BLOCK = Regex("""class="[^"]*(?:detail-main-info-class|\btip\b)[^"]*"[^>]*>[\s\S]{0,2500}?</(?:p|div)>""")
    private val CATEGORY_TEXT = Regex(""">(?:限制级|限制級)<""")
    private val DETAIL_TITLE_MOBILE = Regex("""class="detail-main-info-title"[^>]*>([^<]+)""")
    private val DETAIL_TITLE_DESKTOP = Regex("""<p class="title"[^>]*>\s*([^<]+)""")
    private val DETAIL_DESC_MOBILE = Regex("""class="detail-desc"[^>]*>([^<]*)""")
    private val DETAIL_DESC_DESKTOP = Regex("""<p class="content"[^>]*>\s*([^<]*)""")
    private val BILI_TITLE = Regex("""<h1 class="book-title"[^>]*>([^<]+)""")
    private val BILI_ALIAS = Regex("""class="backupname"[^>]*>([^<]+)""")
    private val BILI_SUMMARY = Regex("""id="bookSummary"[\s\S]*?<content>([\s\S]*?)</content>""")
    private val BILI_TAG = Regex("""class="tag-small"[^>]*>([^<]+)""")
    private val MYCOMIC_TITLE = Regex("""data-flux-heading[^>]*>([\s\S]*?)</""")
    private val MYCOMIC_H1 = Regex("""<h1[^>]*>([\s\S]*?)</h1>""")
    private val MYCOMIC_DESC = Regex("""x-show=["']show["'][^>]*>([\s\S]*?)</""")
    private val JSON_NAME = Regex(""""name"\s*:\s*"([^"]*)"""")
    private val JSON_ALIAS = Regex(""""alias"\s*:\s*"([^"]*)"""")
    private val JSON_BRIEF = Regex(""""brief"\s*:\s*"([^"]*)"""")
    private val JSON_PATH = Regex(""""path_word"\s*:\s*"([^"]*)"""")
    private val JSON_VALUE = Regex(""""value"\s*:\s*(-?\d+)""")
    private val JSON_DISPLAY = Regex(""""display"\s*:\s*"([^"]*)"""")
    private val JSON_THEME = Regex(""""theme"\s*:\s*\[([^\[\]]*)\]""")
    private val RESTRICT_OBJECT = Regex(""""restrict"\s*:\s*(\{[^}]*\}|null)""")
    private val BLACKLIST_ENTRY = Regex(""""([^"]+)"\s*:\s*\[([^\[\]]*)]""")
    private val JSON_STRING = Regex(""""([^"]*)"""")
    private val TAG = Regex("""<[^>]+>""")
}
