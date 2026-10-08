# AndroidCaculator

Android 計算機專案。

## AI 協作

- 工具：OpenCode
- 模型：Muse Spark 1.3 Free
  - `providerID/modelID`：`opencode/muse-spark-1.3-contributor-free`
  - Provider：`opencode`
- 協作方式：以 OpenCode agent 執行 git 初始化、文件建立、程式實作與驗證。

## AI 交接紀錄

交接文件放在 `docs/ai-handoff/`：

- `SKILLS.md`：本次專案調用過的 skill 清單。
- `EXECUTION_LOG.md`：執行紀錄（做了什麼、指令、結果），方便下一個 AI 接手。

> 維護規則：每次用 skill 或做關鍵變更，先更新 `docs/ai-handoff/` 再 commit。
