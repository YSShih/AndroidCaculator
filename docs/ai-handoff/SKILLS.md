# SKILLS — 調用紀錄

> 每次調用 skill 在此追加一列，方便交接。

| 日期 (UTC) | Skill ID | 用途 | 結果 |
|---|---|---|---|
| 2026-10-08 | （無） | 本次初始化未調用任何 skill，僅用 git + 文件工具 | README 與 `docs/ai-handoff/` 建立，首次 commit |
| 2026-10-08 | `ai-precision-development` | KMM 新專案範圍控制（MULTI-AGENT 模式宣告、CHANGE BUDGET、上鎖） | Phase 1 骨架＋共享引擎，預算內 |
| 2026-10-08 | subagent 并行 x3（engine / skeleton / android-ui，唯讀設計、零寫檔） | 引擎 API、Gradle 骨架、Compose layout 設計收斂 | 設計差異已調和（`%`＝UI 單目百分比＋表示式二元求餘，package 統一 `com.calc.calculator`） |

## 可用 Skill 來源

本環境可用 skill（按需調用，用了就登記）：

- `ai-precision-development`：非-trivial 程式變更的範圍控制
- `animate` / `animate-expo` / `animation-vocabulary` / `apple-design` / `emil-design-eng`：動畫與設計
- `find-animation-opportunities` / `improve-animations`：動畫審計
- `ask-sonner`：React toast
- `graphify`：codebase 知識圖譜
- `opencode`：OpenCode 本體問題排查
- `report`：回報 opencode issue
- `spec-kit`：spec-driven 開發
- `write-swift`：Swift 開發
