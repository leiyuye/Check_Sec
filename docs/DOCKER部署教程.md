# 等保备案管理平台 — Docker 云服务器部署教程

本教程适用于在 **Linux 云服务器**（阿里云 / 腾讯云 / 华为云等）上使用 Docker 一键部署本项目。

部署架构：

```
浏览器 → 云服务器:80 → [Nginx 前端容器] → /api 反向代理 → [Spring Boot 后端容器] → [MySQL 容器]
                                              ↓
                                        静态页面 (Vue 构建产物)
```

---

## 一、服务器准备

### 1.1 推荐配置

| 项目 | 建议 |
|------|------|
| 系统 | Ubuntu 22.04 / CentOS 7+ / Debian 11+ |
| CPU | 2 核及以上 |
| 内存 | 4 GB 及以上（首次构建镜像较吃内存） |
| 磁盘 | 40 GB 及以上 |
| 开放端口 | **80**（HTTP），若用 HTTPS 再开 **443** |

### 1.2 安装 Docker 与 Compose

**Ubuntu / Debian：**

```bash
sudo apt update
sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo ${VERSION_CODENAME:-$VERSION_ID}) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker
```

**验证：**

```bash
docker --version
docker compose version
```

> 国内机器若拉镜像慢，可配置 Docker 镜像加速（阿里云容器镜像服务控制台有免费加速地址）。

### 1.3 云厂商安全组

在控制台 **安全组** 中放行：

- 入站：**TCP 80**（来源 0.0.0.0/0 或按需限制）
- 如需 SSH：**TCP 22**

**不要**对公网开放 3306（MySQL 仅在 Docker 内网访问）。

---

## 二、上传项目代码

### 方式 A：Git 克隆（推荐）

```bash
cd /opt
sudo git clone <你的仓库地址> Check_Sec
sudo chown -R $USER:$USER Check_Sec
cd Check_Sec
```

### 方式 B：本地上传

在本地将整个 `Check_Sec` 目录打包上传到服务器，例如：

```bash
# 本地 PowerShell（在项目上级目录）
scp -r Check_Sec root@你的服务器IP:/opt/
```

```bash
# 服务器
cd /opt/Check_Sec
```

---

## 三、配置环境变量

```bash
cd /opt/Check_Sec
cp .env.example .env
nano .env   # 或 vim .env
```

**务必修改以下两项：**

```env
MYSQL_ROOT_PASSWORD=你的强密码
JWT_SECRET=至少32位的随机字符串
HTTP_PORT=80
```

保存退出。

---

## 四、构建并启动

```bash
cd /opt/Check_Sec
docker compose up -d --build
```

首次会：

1. 拉取 MySQL、Node、Maven、Nginx 等基础镜像  
2. 编译后端 Java 项目  
3. 编译前端 Vue 项目  
4. 启动 3 个容器：`check_sec_mysql`、`check_sec_backend`、`check_sec_frontend`

**查看状态：**

```bash
docker compose ps
```

三个服务均为 `running` 即正常。MySQL 需等待 healthcheck 通过后后端才会启动（约 1～2 分钟）。

**查看日志：**

```bash
# 全部日志
docker compose logs -f

# 仅后端
docker compose logs -f backend

# 仅前端
docker compose logs -f frontend
```

---

## 五、访问系统

浏览器打开：

```text
http://你的服务器公网IP
```

默认管理员账号（系统首次启动自动创建）：

| 账号 | 密码 |
|------|------|
| admin | admin123456 |

登录后请尽快修改密码。

---

## 六、常用运维命令

```bash
cd /opt/Check_Sec

# 停止
docker compose down

# 停止并删除数据卷（会清空数据库和上传文件，慎用）
docker compose down -v

# 重新构建并启动（代码更新后）
docker compose up -d --build

# 仅重启某个服务
docker compose restart backend
docker compose restart frontend

# 进入后端容器
docker exec -it check_sec_backend sh

# 查看上传文件卷
docker volume inspect check_sec_upload_data
```

---

## 七、数据持久化说明

| 数据卷 | 内容 |
|--------|------|
| `mysql_data` | MySQL 数据库文件 |
| `upload_data` | 用户上传的备案材料、批注文件 |

升级应用时执行 `docker compose up -d --build` **不会**删除上述数据卷。

**备份 MySQL 示例：**

```bash
docker exec check_sec_mysql mysqldump -uroot -p你的密码 check_sec > backup_$(date +%F).sql
```

**恢复：**

```bash
docker exec -i check_sec_mysql mysql -uroot -p你的密码 check_sec < backup_2026-05-25.sql
```

---

## 八、配置 HTTPS（可选）

生产环境建议在宿主机再套一层 **Nginx** 或 **Caddy** 做 SSL 终结，或使用云厂商负载均衡 HTTPS。

简单做法：宿主机安装 Nginx，配置 `443` 证书，反代到 `127.0.0.1:80`：

```nginx
server {
    listen 443 ssl;
    server_name your.domain.com;
    ssl_certificate     /path/to/fullchain.pem;
    ssl_certificate_key /path/to/privkey.pem;

    client_max_body_size 55m;

    location / {
        proxy_pass http://127.0.0.1:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 九、修改对外端口

若 80 端口已被占用，编辑 `.env`：

```env
HTTP_PORT=8088
```

然后：

```bash
docker compose up -d
```

访问：`http://服务器IP:8088`，安全组需放行 **8088**。

---

## 十、故障排查

### 1. 页面打不开

```bash
docker compose ps
curl -I http://127.0.0.1
```

- 检查安全组是否放行 `HTTP_PORT`  
- 检查 `check_sec_frontend` 是否 running  

### 2. 能打开页面但登录失败 / 接口 502

```bash
docker compose logs backend --tail 100
```

常见原因：

- MySQL 未就绪：等待 1～2 分钟或 `docker compose restart backend`  
- `.env` 中 `MYSQL_ROOT_PASSWORD` 与启动后不一致（改过密码需 `docker compose down -v` 重建，会丢数据）  

### 3. 首次构建失败（内存不足）

```bash
# 查看构建日志
docker compose build backend 2>&1 | tail -50
```

可尝试增加 swap，或在本机构建镜像后 `docker save/load` 到服务器。

### 4. 文件上传失败

确认 Nginx `client_max_body_size` 为 55m（项目已配置）。查看后端日志是否有磁盘权限问题。

### 5. 更新代码后未生效

```bash
docker compose up -d --build --force-recreate
```

---

## 十一、项目 Docker 文件说明

| 文件 | 作用 |
|------|------|
| `Dockerfile` | 后端 Spring Boot 镜像 |
| `Check_Sec-ui/Dockerfile` | 前端构建 + Nginx |
| `Check_Sec-ui/nginx.conf` | 静态资源 + `/api` 反向代理 |
| `docker-compose.yml` | 编排 MySQL + 后端 + 前端 |
| `.env` | 密码、JWT、端口（勿提交 Git） |
| `src/main/resources/application-docker.yml` | 容器内 Spring 配置 |

---

## 十二、快速检查清单

- [ ] 服务器已安装 Docker 与 Compose  
- [ ] 安全组已放行 80（或自定义端口）  
- [ ] 已复制 `.env.example` 为 `.env` 并修改密码  
- [ ] 执行 `docker compose up -d --build` 成功  
- [ ] `docker compose ps` 三个服务均为 running  
- [ ] 浏览器可访问并 admin 登录成功  

按以上步骤操作即可完成云服务器 Docker 部署。若某一步报错，把 `docker compose logs` 相关片段发来即可继续排查。
