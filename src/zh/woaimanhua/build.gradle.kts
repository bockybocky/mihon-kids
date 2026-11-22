import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Woaimanhua Kids"
    pkgName = "zh.woaimanhua.kids"
    versionCode = 1
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "我爱漫画（兒童）"
        lang = "zh"
        baseUrl = "https://www.woimh.com"
    }

    deeplink {
        host("www.woimh.com")
        host("woimh.com")
        path("/book/..*")
        path("/chapter/..*")
    }
}
