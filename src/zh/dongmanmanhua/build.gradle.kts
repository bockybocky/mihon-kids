import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Dongman Manhua Kids"
    pkgName = "zh.dongmanmanhua.kids"
    versionCode = 1
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "Dongman Manhua（兒童）"
        lang = "zh-Hans"
        baseUrl = "https://www.dongmanmanhua.cn"
    }
}
