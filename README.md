# 等保备案管理平台

前后端分离的等保定级备案材料管理系统，支持备案单位、测评机构提交材料，管理员审核、批注回传与重新提交流程。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.4、Spring Data JPA、MySQL、Spring Security + JWT |
| 前端 | Vue 3、Vite、Element Plus、Vue Router、Pinia、Axios |

## 项目结构

```
Check_Sec/                 # 后端 Spring Boot
  src/main/java/...
  src/main/resources/application.yml
Check_Sec-ui/              # 前端 Vue3
  src/
```

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+

## 数据库准备

1. 安装并启动 MySQL。
2. 创建数据库（也可由 JPA 自动建库，需账号有建库权限）：

```sql
CREATE DATABASE IF NOT EXISTS check_sec DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 修改 `src/main/resources/application.yml` 中的数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/check_sec?...
    username: root
    password: 你的密码
```

## 启动后端

在项目根目录 `Check_Sec` 执行：

```bash
mvn spring-boot:run
```

或在 IntelliJ IDEA 中运行 `CheckSecApplication`。

- 服务地址：`http://localhost:8080`
- 上传文件目录：`./uploads`（可在 `application.yml` 的 `app.file.upload-dir` 修改）
- 单文件大小上限：50MB（`app.file.max-size-mb`）

### 默认管理员

| 项目 | 值 |
|------|-----|
| 账号 | `admin` |
| 密码 | `admin123456` |

首次登录后系统会提示修改默认密码。

## 启动前端

```bash
cd Check_Sec-ui
npm install
npm run dev
```

- 访问地址：`http://localhost:5173`
- 开发环境已通过 Vite 代理将 `/api` 转发到 `http://localhost:8080`

生产构建：

```bash
npm run build
```

将 `dist` 部署到 Nginx 等静态服务器，并反向代理 `/api` 到后端。

## 系统角色与菜单

| 角色 | 主要功能 |
|------|----------|
| 管理员 | 用户管理、备案审核、上传批注文件、通过/退回 |
| 备案单位 | 注册登录、新增/提交备案、下载批注、修改重提 |
| 测评机构 | 注册登录、代备案单位提交、跟踪审核状态 |

## 主要 API

| 模块 | 路径前缀 |
|------|----------|
| 认证 | `POST /api/auth/register`、`/login`，`GET /api/auth/me` |
| 用户管理 | `/api/admin/users`（管理员） |
| 备案 | `/api/filings` |
| 文件 | `POST /api/files/upload`，`GET /api/files/{id}/download` |
| 统计 | `GET /api/dashboard/stats` |

请求头需携带：`Authorization: Bearer <token>`

## 备案状态说明

草稿 → 待审核 → 审核中 / 退回修改 / 已回传批注 → 已重新提交 → 审核通过

- 提交后普通用户不可直接修改（仅草稿、退回、已回传批注状态可编辑）
- 审核通过后不可再修改
- 所有关键操作写入 `filing_operation_log`

## 常见问题

1. **登录 401**：检查后端是否启动、数据库是否连接成功。
2. **文件上传失败**：确认扩展名为 doc/docx/pdf/xls/xlsx/zip/rar，且小于 50MB。
3. **Maven 未找到**：配置 `MAVEN_HOME` 或使用 IDEA 自带 Maven 运行。

## Docker 云服务器部署

详见 **[docs/DOCKER部署教程.md](docs/DOCKER部署教程.md)**。

快速启动：

```bash
cp .env.example .env
# 编辑 .env 修改 MYSQL_ROOT_PASSWORD 和 JWT_SECRET
docker compose up -d --build
```

浏览器访问 `http://服务器IP`（默认端口 80）。

## 后续扩展

- 文件存储已抽象 `FileStorageService` 接口，可替换为 OSS 实现。
- 可增加操作日志独立菜单、短信验证码等能力。
