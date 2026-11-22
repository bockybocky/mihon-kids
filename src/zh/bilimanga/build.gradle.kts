import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "BiliManga Kids"
    pkgName = "zh.bilimanga.kids"
    versionCode = 16
    contentWarning = ContentWarning.MIXED
    libVersion = "1.6"

    source {
        name = "嗶哩漫畫（兒童）"
        lang = "zh"
        baseUrl = "https://www.bilimanga.net"
    }

    deeplink {
        host("www.bilimanga.net")
        path("/detail/.*\\.html")
    }
}
