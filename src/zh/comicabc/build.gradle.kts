import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Comicabc Kids"
    pkgName = "zh.comicabc.kids"
    versionCode = 1
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "無限動漫（兒童）"
        lang = "zh"
        baseUrl = "https://www.8comic.com"
    }
}
