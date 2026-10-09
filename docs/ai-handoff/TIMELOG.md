# TIMELOG — 計時紀錄（使用者本地時間）

> 規則：所有動作都要有一列，含開始/結束與結果。時間由執行者以 `Get-Date` 或使用者回報為準。

| 日期 | 時間 | 動作 | 結果 |
|---|---|---|---|
| 2026-10-08 | 22:34 | 計時開始（使用者宣告） | KMM 計算機任務啟動 |
| 2026-10-08 | 22:34 | 派工 3 個並行 subagent（引擎設計 / 骨架 / Android UI） | 全部完成，已收斂 |
| 2026-10-08 | 22:34 | 環境盤點：Java 20、Studio 在 `C:\Program Files\Android\Android Studio`、SDK 在 `%LOCALAPPDATA%\Android\Sdk`（`adb.exe`、`emulator.exe` 存在）、`ANDROID_HOME` 未設、無 `gradle` CLI | 以 `local.properties` 指 SDK，Gradle 由 Studio/wrapper 提供 |
| 2026-10-08 | 22:4x | Phase 1：根 Gradle 設定（`settings`/`build`/`libs.versions`/`wrapper`/`gradle.properties`）+ `shared` 模組（`CalculatorState`/`Calculator`/`Tokenizer`/`Evaluator`/`Format`＋13 個 `commonTest`）+ `.gitignore` | 待 commit，待 Studio sync 驗證 |
| 2026-10-08 | 22:4x | Phase 2（待）：`androidApp` Compose UI＋`iosApp` SwiftUI 源碼 | 未開始 |
| 2026-10-08 | 22:36-22:37 | Phase 1 commit `d1c544b`＋Phase 2 commit `52d3c9d`＋push | `master -> master`，乾淨；發現 AVD `Pixel_3a_API_35`、Studio JBR 17 可用 |
| 2026-10-08 | 22:38 | 演算法驗證：Python 等價移植 15/15（優先順序、括號、求餘、`0.1+0.2→0.3`、除零/格式/括號/溢位） | 邏輯 PASS；Kotlin 語法待 Gradle 實編 |
| 2026-10-08 | 22:38 | 新增最小 `gradlew`/`gradlew.bat`（commit `a4ab10e` 已 push），啟動背景實編（Gradle 8.7＋JBR 17＋`assembleDebug`） | 背景執行中，待通知 |
| 2026-10-08 | 22:4x | 背景任務完成：Gradle 8.7 已裝好（`%LOCALAPPDATA%\Gradle\gradle-8.7`），Studio JBR 實為 Java 21（AGP 8.5.2 可用） | 續跑官方 `gradle wrapper`＋`:androidApp:assembleDebug`，背景執行中 |
| 2026-10-08 | 22:5x | 實編 #1 失敗：`:androidApp:compileDebugKotlin` JVM-target 不一致（Kotlin 21 vs Java 17）；另 SDK 自動裝好 Build-Tools 34＋Platform 34 | 修 `androidApp` 加 `kotlinOptions.jvmTarget="17"`，收官方 wrapper 產物，重跑 |
| 2026-10-08 | 22:5x | 實編 #2：主程式編過，`testDebugUnitTest` 13 中 2 敗（`mismatched`／`backspace` 斷言與 `reduce` 守衛語義不合，引擎行為正確） | 修正測試期望（ stray `)` 忽略、求值後刪除重置），重跑 |
| 2026-10-08 | 22:5x | 實編 #3：BUILD SUCCESSFUL（`assembleDebug`＋13/13 測試，commit `f945c24`） | 啟動模擬器（`Pixel_3a_API_35`）裝機驗收，背景執行中 |
| 2026-10-08 | 22:46 | 模擬器驗收：安裝＋啟動成功，截圖確認 4x6 UI；adb 點按 `1+2×3=` 顯示 `7`（端到端 PASS） | 發現 `⌫` 缺字（tofu）→先改 `DEL` 又太寬被截→定案 `←`，Android＋iOS 一致 |
| 2026-10-08 | 22:47 | `←` 重編＋重裝＋截圖確認正常；清暫存截圖；README 定稿；commit＋push 收尾 | 待執行 |
| 2026-10-09 | 11:00 | macOS 接手：修 `local.properties`（Mac SDK 路徑＋env 優先順序說明，gitignore 不進版控） | 本機生效 |
| 2026-10-09 | 11:0x | 新建 `iosApp/iosApp.xcodeproj`，`xcodebuild -list` 解析成功，Xcode 可開 | 待實機＋實編確認 |

## 環境證據
- `C:\Program Files\Android\Android Studio` 存在；`%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`＝True；`emulator\emulator.exe`＝True
- `java -version`＝Java 20；`gradle`/`adb`/`emulator` 不在 PATH
- iOS 在 Windows 標 `UNVERIFIED_ON_WINDOWS`，需 macOS＋Xcode 補驗（`embedAndSignAppleFrameworkForXcode`、`xcodebuild`）
