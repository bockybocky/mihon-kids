import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Kuaikanmanhua Kids"
    pkgName = "zh.kuaikanmanhua.kids"
    versionCode = 1
    contentWarning = ContentWarning.SAFE
    libVersion = "1.6"

    source {
        name = "快看漫画（兒童）"
        lang = "zh-Hans"
        baseUrl = "https://www.kuaikanmanhua.com"
    }

    deeplink {
        host("kuaikanmanhua.com")
        host("*.kuaikanmanhua.com")
        path("/mobile/..*")
        path("/web/topic/..*")
    }
}
