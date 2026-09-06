# Step 2 数据库设计

## 设计目标

V1 的数据库只保存系统自身产生和需要回看的业务数据，不复制整个 Git 仓库。

Git 仓库中的 Commit 历史、Tree、Blob、原始文件内容，都可以通过 JGit 从本地仓库实时读取。数据库重点保存一次 Review 任务的快照和 AI 输出结果，保证历史报告可以复盘。

## 需要持久化的数据

### dev_project

保存用户接入的本地 Git 项目。

需要持久化的原因：

- 系统需要知道有哪些项目可以被分析。
- 项目名、本地路径、默认分支是业务配置，不应该每次重新输入。
- 后续 ReviewTask 需要关联到具体项目。

### review_task

保存一次 AI Code Review 任务。

需要持久化的原因：

- 一次 Review 是系统的核心业务行为。
- Review 执行后需要历史回看。
- 需要保存当时的 commit 快照，避免 Git 仓库后续变化影响历史报告。

### review_changed_file

保存一次 Review 中涉及的变更文件快照。

需要持久化的原因：

- Review 报告要展示影响文件。
- 文件变更类型、增删行数、预处理后的 diff 状态都属于本次任务结果。
- 后续可以基于文件维度统计风险。

### review_issue

保存 AI 输出的结构化问题明细。

需要持久化的原因：

- 企业 AI 应用不能只保存一大段字符串。
- 结构化问题可以筛选、统计、排序、展示。
- 每个问题都要关联文件、严重级别、类别、建议。

### generated_document

保存 AI 生成的开发变更说明。

需要持久化的原因：

- 文档是 V1 的交付物之一。
- 需要支持历史查看和复制。
- 后续可以扩展为导出 Markdown、Word 或 PDF。

## 暂不持久化的数据

### GitCommit 历史列表

暂不单独建 `git_commit` 表。Commit 历史可以从本地 Git 仓库读取。

只有在执行 ReviewTask 时，才把 commitId、parentCommitId、message、author、commitTime 保存为快照。

### 原始完整 Diff

V1 不单独保存所有原始 Diff。原因是原始 Diff 可能很大，也可能包含敏感代码。

V1 可以保存预处理后的 diff 片段，便于复盘 AI 为什么得出某个结果。

### Git Tree / Blob

这些是 Git 底层对象，由 JGit 读取即可，不进入业务数据库。

## 表关系

```text
dev_project 1 ── N review_task
review_task 1 ── N review_changed_file
review_task 1 ── N review_issue
review_task 1 ── N generated_document
```

## 状态字段约定

`review_task.status`：

- PENDING：已创建，未开始
- RUNNING：执行中
- SUCCESS：执行成功
- FAILED：执行失败

`review_task.risk_level`：

- PASS：未发现明显问题
- LOW：低风险
- MEDIUM：中风险
- HIGH：高风险

`review_changed_file.change_type`：

- ADD
- MODIFY
- DELETE
- RENAME
- COPY

`review_changed_file.diff_status`：

- INCLUDED：已纳入 AI Review
- SKIPPED_BINARY：跳过二进制文件
- SKIPPED_LARGE：跳过超大 Diff
- SKIPPED_GENERATED：跳过生成文件
- SKIPPED_LOCK_FILE：跳过 lock 文件
- SKIPPED_UNSUPPORTED_TYPE：跳过不支持的文件类型

`review_issue.severity`：

- LOW
- MEDIUM
- HIGH

## 为什么这里不应该用 AI

数据库设计、字段约束、索引设计是确定性工程设计，应该由开发者根据业务查询场景设计。

AI 可以辅助解释设计思路，但不能让 AI 随意决定真实表结构。否则后续代码、查询、迁移都会不稳定。

## 后续扩展方向

- 增加用户表和权限控制。
- 对 Review 原始 Prompt 和模型响应做安全审计存储。
- 增加文档导出任务表。
- 将 Diff 快照拆到独立大字段表或对象存储。
- 增加多模型调用记录表。
