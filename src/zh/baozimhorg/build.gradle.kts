import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "GoDa Kids"
    pkgName = "zh.baozimhorg.kids"
    versionCode = 35
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"
    theme = "goda"

    source {
        name = "GoDa漫画（兒童）"
        lang = "zh"
        baseUrl {
            mirrors(
                "https://baozimh.org",
                "https://godamh.com",
                "https://m.baozimh.one",
                "https://bzmh.org",
                "https://g-mh.org",
                "https://m.g-mh.org",
            )
        }
    }
}
