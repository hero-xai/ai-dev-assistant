# AI 企业级研发助手平台

暂定名：`ai-dev-assistant`

## GitHub 开源信息

- Repository: https://github.com/hero-xai/ai-dev-assistant
- License: MIT
- Releases: 使用 `v0.1.0`、`v0.2.0`、`v0.3.0` 记录每个阶段能力
- Packages: 已配置 GitHub Actions，可将 Maven 构建产物发布到 GitHub Packages

## 阶段留痕

| Version | Commit | 内容 |
| --- | --- | --- |
| v0.1.0 | 600837a | 初始化 Spring Boot 项目、MySQL 表结构、项目管理、Git Commit 读取 |
| v0.2.0 | 03e8488 | 新增 Git Diff 生成接口，输出变更文件、增删行和 diffContent |
| v0.3.0 | d1def44 | 新增 Review Task 持久化，将 commit diff 快照保存到数据库 |
## V1 目标

面向企业软件研发场景，围绕本地 Git 项目的 Commit 变更完成：

- Commit 读取
- Changed Files 分析
- Git Diff 生成
- Diff 预处理
- AI Code Review
- 结构化 Review 报告
- 开发变更说明生成

## 当前进度

Step 3：Project 项目管理。

当前能力：

- Spring Boot 应用可启动
- 统一 API 响应
- 全局异常处理
- 健康检查接口
- MySQL 初始化表结构设计
- 项目管理接口
- Git Commit 读取接口
- Git Diff 生成接口
- Review Task 评审任务接口

## 本地环境

- Java 21：`D:\APP\dev\jdk21\jdk-21.0.12.1+1`
- Maven：`D:\APP\dev\apache-maven-3.8.8`
- Maven 本地仓库：`D:\APP\dev\maven_repo`
- MySQL：`D:\APP\dev\mysql-8.4`
- MySQL 数据目录：`D:\APP\dev\mysql-data`
- MySQL 应用账号：`ai_dev_user`
- DBeaver：`D:\APP\dev\dbeaver-ce`

## 构建

```bat
scripts\build-backend.cmd
```

## 启动

```bat
scripts\run-backend.cmd
```

## 验证

```http
GET http://localhost:8080/api/health
```

预期返回：

```json
{
  "code": "0",
  "message": "success",
  "data": {
    "status": "UP",
    "service": "ai-dev-assistant"
  }
}
```

## 数据库初始化

初始化 SQL：

```text
src/main/resources/db/schema.sql
```

本地启动 MySQL：

```bat
scripts\start-mysql.cmd
```

初始化数据库和表：

```bat
scripts\init-database.cmd
```

进入 MySQL 命令行：

```bat
scripts\mysql-cli.cmd
```

也可以使用 DBeaver 可视化连接：

```text
Host: 127.0.0.1
Port: 3306
Database: ai_dev_assistant
User: ai_dev_user
Password: see scripts\local-env.cmd on your local machine
```

V1 当前设计了 5 张表：

- `dev_project`
- `review_task`
- `review_changed_file`
- `review_issue`
- `generated_document`

## 项目管理接口

```http
POST /api/projects
GET /api/projects
GET /api/projects/{id}
PUT /api/projects
DELETE /api/projects/{id}
```

接口测试样例：

```text
http/api-test.http
```

## Git Commit 接口

```http
GET /api/projects/{projectId}/git/commits?limit=10
GET /api/projects/{projectId}/git/commits/{commitId}
GET /api/projects/{projectId}/git/commits/{commitId}/diff
```



## Review Task 评审任务接口

```http
POST /api/review-tasks
GET /api/review-tasks?projectId=1
GET /api/review-tasks/{id}
```

创建评审任务时，系统会读取指定项目和 commit 的 Git Diff，并把变更文件快照保存到 `review_changed_file`，为后续 AI Code Review 提供稳定输入。

