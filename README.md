# mihon-kids：給小孩用的 Mihon 漫畫來源（過濾十八禁）

> 同一份程式也放在 [duokan-pro2-mod/mihon-kids](https://github.com/bockybocky/duokan-pro2-mod/tree/main/mihon-kids)（多看電紙書改機教學）。

把 [keiyoushi/extensions-source](https://github.com/keiyoushi/extensions-source) 幾個中文來源改成「兒童版」：被判定為十八禁的書，在列表裡直接藏起來；就算用網址硬開，標題會變成「【已過濾】」，章節是 0。一般書照常看。

## 已完成

| 來源 | 套件名 | 判斷方式 | 上機實測 |
|---|---|---|---|
| 動漫屋（兒童） | zh.dm5.kids | 站方「限制级」分類＋關鍵字＋黑名單 | ✅ 成人書搜不到、網址直開 0 章；一般書正常 |
| 嗶哩漫畫（兒童） | zh.bilimanga.kids | 關鍵字＋黑名單（站方無成人標記） | ✅ 成人書 0 章、葬送的芙莉蓮 147 章 |
| 拷貝漫畫（兒童） | zh.kaobeimanhua.kids | 站方 restrict 欄＋關鍵字＋黑名單 | ✅ 搜「工口」原版整排、兒童版 0 本；一般書有章節 |
| MyComic（兒童） | zh.mycomic.kids | 關鍵字＋黑名單 | ✅ 搜「工口」原版整排、兒童版 0 本；一般書有章節 |
| MangaDex Kids | all.mangadex.kids | 所有請求寫死只要 safe 分級，設定頁分級選項移除 | ✅ 搜「工口」原版 2 本、兒童版 0 本；一般書有章節 |
| 包子漫画（兒童） | zh.baozimanhua.kids | 關鍵字＋黑名單 | ✅ 搜「工口」原版整排、兒童版只剩一般書 |
| GoDa漫画（兒童） | zh.baozimhorg.kids | 關鍵字＋黑名單 | ⚠️ 書名含關鍵字的擋掉；書名不含關鍵字但內容偏成人的會漏 |
| 如漫画（兒童） | zh.rumanhua.kids | 關鍵字＋黑名單 | ✅ 搜「工口」原版 5 本、兒童版 0 本 |
| 我爱漫画（兒童） | zh.woaimanhua.kids | 關鍵字＋黑名單 | ⚠️ 書名含關鍵字的擋掉；此站成人書多數書名不含關鍵字，會漏，建議不給小孩用 |
| 漫画柜（兒童） | zh.manhuagui.kids | R18 開關永遠關＋關鍵字＋黑名單 | ✅ 搜「工口」原版整排、兒童版只剩一般書 |
| 快看漫画（兒童） | zh.kuaikanmanhua.kids | 關鍵字＋黑名單 | ✅ 兒童版與原版一般書結果相同 |
| 腾讯动漫（兒童） | zh.tencentcomics.kids | 關鍵字＋黑名單 | ✅ 兒童版與原版結果相同（站方本身無成人分類） |
| 再漫画（兒童） | zh.zaimanhua.kids | 關鍵字＋黑名單 | ✅ 一般書有章節 |
| CCC追漫台（兒童） | zh.creativecomic.kids | 關鍵字＋黑名單 | ✅ 一般書有章節 |
| 瓜子漫画（兒童） | zh.guazimanhua.kids | 關鍵字＋黑名單 | ✅ 搜一般書結果與原版相同 |
| 無限動漫（兒童） | zh.comicabc.kids | 關鍵字＋黑名單 | ⚠️ 未能實測（原版當天也連不上站） |
| Dongman Manhua（兒童） | zh.dongmanmanhua.kids | 關鍵字＋黑名單 | ⚠️ 未能實測（原版當天也逾時） |

其他中文來源製作中。

## 三層判斷（KidsFilter.kt）

1. 站方標記（動漫屋的 `/manhua-18-x/` 分類、拷貝的 `restrict` 欄）。注意：動漫屋「是否已年滿18歲」彈窗每本書都有，不能當判準。
2. 關鍵字：標題、別名、簡介、分類含「工口、色情、成人、限制級、R18、18禁、紳士、里番…」就擋。
3. 遠端黑名單：https://github.com/bockybocky/mihon-kids 的 `blacklist.json`，漏網的書加一條即可，不用重新編譯。

## 怎麼用

1. 把這些檔案蓋到 keiyoushi/extensions-source 的同路徑。
2. 編譯：`./gradlew :src:zh:dm5:assembleDebug`（需 Android SDK，compileSdk 37）。
3. `adb install` 產出的 apk，在 Mihon「擴充套件」頁按「信任」。
4. 把原版來源移除，否則小孩點原版還是看得到。

## 限制（老實說）

- 關鍵字擋法會漏，也會誤擋（例如書名正好有「成人」二字）。
- 這只擋漫畫程式裡的內容。小孩要是能自己裝程式、開瀏覽器，一分鐘就能繞過，要搭配系統層的限制。

授權：Apache-2.0（沿用上游）。
