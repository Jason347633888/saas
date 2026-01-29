# Wemirr Platform 部署指南

## 一、环境准备

### 1.1 安装 Docker

```bash
# Ubuntu
sudo apt-get update
sudo apt-get install docker.io docker-compose

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 添加当前用户到 docker 组
sudo usermod -aG docker $USER
```

### 1.2 创建 Docker 网络

```bash
# 创建网络
docker network create wemirr

# 验证网络
docker network ls
```

## 二、启动中间件

### 2.1 启动 MySQL、Redis、Nacos、RabbitMQ

```bash
cd 附件/docker

# 启动中间件服务
docker-compose up -d mysql redis nacos rabbitmq

# 查看启动状态
docker-compose ps

# 查看日志
docker-compose logs -f mysql
```

### 2.2 初始化数据库

```bash
# 执行数据库初始化脚本
mysql -h 127.0.0.1 -u root -p123456 < ../sql/v4-dev.sql

# 执行网关管理权限 SQL
mysql -h 127.0.0.1 -u root -p123456 < ../sql/gateway-admin-permissions.sql
```

### 2.3 配置 Nacos

1. 访问 http://localhost:8848/nacos
2. 默认用户名/密码: nacos/nacos
3. 在配置管理中添加 `wemirr-platform-gateway-admin.yml` 配置
4. 配置内容参考: `deploy/nacos/wemirr-platform-gateway-admin.yml`

## 三、构建并启动应用服务

### 3.1 方式一: 使用 Docker Compose 统一构建（推荐）

```bash
cd 附件/docker

# 构建并启动所有服务（包括应用）
docker-compose up -d

# 只启动应用服务
docker-compose up -d gateway gateway-admin

# 查看日志
docker-compose logs -f gateway
docker-compose logs -f gateway-admin
```

### 3.2 方式二: 手动构建 Docker 镜像

```bash
# 构建 Gateway 镜像
cd wemirr-platform-gateway
docker build -t wemirr-platform-gateway:latest .

# 构建 Gateway Admin 镜像
cd wemirr-platform-gateway-admin
docker build -t wemirr-platform-gateway-admin:latest .

# 运行容器
docker run -d --network wemirr \
  -p 15000:15000 \
  -e NACOS_HOST=nacos \
  -e REDIS_HOST=redis \
  --name wemirr-gateway \
  wemirr-platform-gateway:latest

docker run -d --network wemirr \
  -p 15001:15001 \
  -e NACOS_HOST=nacos \
  -e REDIS_HOST=redis \
  --name wemirr-gateway-admin \
  wemirr-platform-gateway-admin:latest
```

## 四、服务访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| Gateway | http://localhost:15000 | API 网关入口 |
| Gateway Admin | http://localhost:15001/doc.html | 网关管理 API 文档 |
| Nacos | http://localhost:8848/nacos | 配置中心 |
| MySQL | localhost:3306 | 数据库 |
| Redis | localhost:6379 | 缓存 |
| RabbitMQ | localhost:15672 | 消息队列管理 |

## 五、网关管理 API

### 5.1 认证

所有网关管理 API 需要在请求头中携带 Token:

```bash
Authorization: Bearer <token>
```

### 5.2 API 列表

| 功能 | 方法 | 路径 | 权限 |
|------|------|------|------|
| 查询路由规则 | GET | /gateway/admin/routes | gateway:route:list |
| 新增路由规则 | POST | /gateway/admin/routes | gateway:route:create |
| 修改路由规则 | PUT | /gateway/admin/routes/{id} | gateway:route:modify |
| 发布路由规则 | POST | /gateway/admin/routes/{id}/publish | gateway:route:publish |
| 删除路由规则 | DELETE | /gateway/admin/routes/{id} | gateway:route:remove |
| 查询黑名单 | GET | /gateway/admin/blacklist | gateway:blacklist:list |
| 新增黑名单 | POST | /gateway/admin/blacklist | gateway:blacklist:create |
| 修改黑名单 | PUT | /gateway/admin/blacklist/{id} | gateway:blacklist:modify |
| 删除黑名单 | DELETE | /gateway/admin/blacklist/{id} | gateway:blacklist:remove |
| 查询限流规则 | GET | /gateway/admin/limits | gateway:limit:list |
| 新增限流规则 | POST | /gateway/admin/limits | gateway:limit:create |
| 修改限流规则 | PUT | /gateway/admin/limits/{id} | gateway:limit:modify |
| 删除限流规则 | DELETE | /gateway/admin/limits/{id} | gateway:limit:remove |
| 查询服务列表 | GET | /gateway/admin/discoveries | gateway:discovery:list |

### 5.3 示例请求

```bash
# 查询路由规则
curl -X GET http://localhost:15000/gateway/admin/routes \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 新增黑名单
curl -X POST http://localhost:15000/gateway/admin/blacklist \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "ip": "192.168.1.100",
    "method": "POST",
    "path": "/api/attack",
    "description": "恶意IP封禁"
  }'
```

## 六、常用命令

### 6.1 服务管理

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 重启单个服务
docker-compose restart gateway

# 查看服务日志
docker-compose logs -f --tail=100 gateway

# 查看服务状态
docker-compose ps
```

### 6.2 服务缩放

```bash
# Gateway 服务扩容到 2 个实例
docker-compose up -d --scale gateway=2

# 注意: 端口冲突时需要修改映射端口
```

### 6.3 清理

```bash
# 删除所有容器和数据卷
docker-compose down -v

# 清理未使用的镜像
docker image prune -a
```

## 七、问题排查

### 7.1 服务无法启动

```bash
# 查看详细错误日志
docker-compose logs gateway

# 检查容器健康状态
docker inspect wemirr-gateway
```

### 7.2 Nacos 配置不生效

1. 检查 Nacos 控制台配置是否正确
2. 确认配置格式为 YAML
3. 检查服务是否正确连接 Nacos

### 7.3 数据库连接失败

```bash
# 测试 MySQL 连接
docker exec -it wemirr-mysql mysql -u root -p123456

# 检查数据库是否初始化
show databases;
use wemirr_platform;
show tables;
```

## 八、生产环境建议

1. **安全配置**
   - 修改默认密码
   - 启用 Nacos 认证
   - 配置 SSL/TLS

2. **资源优化**
   - 根据实际负载调整 JVM 参数
   - 配置 Redis 持久化策略
   - 开启 MySQL 慢查询日志

3. **监控告警**
   - 配置服务健康检查
   - 添加日志收集（ELK/Loki）
   - 设置资源使用告警

4. **高可用**
   - 使用 Docker Swarm 或 Kubernetes 部署
   - Nacos 集群模式
   - MySQL 主从复制
