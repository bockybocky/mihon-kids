import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Tencent Comics (ac.qq.com) Kids"
    pkgName = "zh.tencentcomics.kids"
    versionCode = 1
    contentWarning = ContentWarning.MIXED
    libVersion = "1.6"

    source {
        name = "腾讯动漫（兒童）"
        lang = "zh-Hans"
        baseUrl = "https://m.ac.qq.com"
    }

    deeplink {
        host("ac.qq.com")
        host("*.ac.qq.com")
        path("/comic/index/id/..*")
        path("/Comic/comicInfo/id/..*")
    }
}
