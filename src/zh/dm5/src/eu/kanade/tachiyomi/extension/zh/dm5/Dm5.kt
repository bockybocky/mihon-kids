package eu.kanade.tachiyomi.extension.zh.dm5

import eu.kanade.tachiyomi.multisrc.dm5.Dm5
import keiyoushi.annotation.Source
import keiyoushi.kidsfilter.KidsFilter

@Source
abstract class Dm5 : Dm5() {
    init {
        KidsFilter.note("dm5")
    }
}
