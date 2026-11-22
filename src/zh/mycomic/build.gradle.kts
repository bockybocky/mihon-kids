import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "MyComic Kids"
    pkgName = "zh.mycomic.kids"
    versionCode = 2
    contentWarning = ContentWarning.MIXED
    libVersion = "1.6"

    source {
        lang = "zh"
        name = "MyComic（兒童）"
        baseUrl = "https://mycomic.com"
    }

    deeplink {
        path("/comics/..*")
        path("/cn/comics/..*")
        path("/chapters/..*")
        path("/cn/chapters/..*")
    }
}
