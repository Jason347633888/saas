# ============================================================
# Gateway Admin 服务 Docker 构建
#
# 构建命令: docker build -f Dockerfile.gateway-admin -t wemirr-platform-gateway-admin:latest .
# 运行命令: docker run -p 15001:15001 wemirr-platform-gateway-admin:latest
# ============================================================

# 阶段1: 构建
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制整个项目用于构建
COPY . .

# 构建项目 - 直接从模块目录构建
RUN cd wemirr-platform-dependencies && mvn clean install -DskipTests -N -q && \
    cd ../wemirr-platform-framework && mvn clean install -DskipTests -q && \
    cd ../wemirr-platform-feign && mvn clean install -DskipTests -q && \
    cd ../wemirr-platform-gateway-admin && mvn clean package -DskipTests -q

# 阶段2: 运行
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建非 root 用户运行
RUN addgroup -g 1000 app && adduser -u 1000 -G app -s /bin/sh -D app

# 创建日志目录
RUN mkdir -p /app/logs && chown -R app:app /app/logs

# 复制 jar 文件
COPY --from=builder /app/wemirr-platform-gateway-admin/target/*.jar app.jar

# 切换用户
USER app

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:15001/actuator/health || exit 1

# 暴露端口
EXPOSE 15001

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
