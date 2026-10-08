# AndroidCaculator — KMM 跨平台計算機

Kotlin Multiplatform 計算機：共享計算引擎（純 Kotlin）＋ Android（Jetpack Compose）＋ iOS（SwiftUI）。

## 結構

```
shared/      KMP 共享模組：CalculatorState / Calculator(reduce) / Tokenizer /
             Evaluator(shunting-yard) / Format / CalculatorStore
androidApp/  Compose App（StateFlow ViewModel＋4x6 按鍵矩陣）
iosApp/      SwiftUI 源碼＋接線說明（Windows 未編譯，需 macOS 補驗）
docs/ai-handoff/  SKILLS.md / EXECUTION_LOG.md / TIMELOG.md（交接紀錄）
```

## 語義（已定案）

- UI `%` 鍵＝單目百分比（÷100）；表示式內 `%`＝二元求餘。
- 錯誤：除以零／算式不完整／括號不配對／數值溢位；錯誤後按數字自動重起，`C` 清輸入、`AC` 全清。

## 建置（Windows）

- 需求：Android Studio（自帶 JBR 17＋）、SDK（含 Platform 34）。
- `./gradlew.bat :androidApp:assembleDebug`；測試 `:shared:testDebugUnitTest`（13 個）。
- 模擬器：`Pixel_3a_API_35` 已驗證安裝＋點按 `1+2×3=`＝`7`。

## iOS（需 macOS＋Xcode）

見 `iosApp/接線說明.md`：`embedAndSignAppleFrameworkForXcode` 後用 Xcode 開專案驗證。

## AI 協作

- 工具：OpenCode
- 模型：Muse Spark 1.3 Free（`opencode/muse-spark-1.3-contributor-free`）
- 交接：`docs/ai-handoff/`；規則：關鍵變更先更新該目錄再 commit。
