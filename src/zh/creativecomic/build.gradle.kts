import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Creative Comic Collection Kids"
    pkgName = "zh.creativecomic.kids"
    versionCode = 4
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "CCC追漫台（兒童）"
        lang = "zh-Hant"
        baseUrl = "https://www.creative-comic.tw"
    }
}
