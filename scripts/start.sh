#!/bin/bash

# Uiineed Todo List 启动脚本
# 检查并配置环境变量，确保安全启动

set -e

echo "🚀 启动 Uiineed Todo List 应用..."

# 检查环境变量文件
ENV_FILE=".env"
if [ ! -f "$ENV_FILE" ]; then
    echo "❌ 错误：未找到 .env 文件"
    echo "请复制 .env.example 到 .env 并配置相关参数："
    echo "cp .env.example .env"
    exit 1
fi

# 加载环境变量
source .env

# 验证必需的环境变量
echo "🔍 验证环境变量配置..."

# 检查数据库密码
if [ -z "$MYSQL_ROOT_PASSWORD" ] || [ "$MYSQL_ROOT_PASSWORD" = "your_secure_root_password_here" ]; then
    echo "❌ 错误：请在 .env 文件中设置安全的 MySQL root 密码"
    exit 1
fi

if [ -z "$MYSQL_PASSWORD" ] || [ "$MYSQL_PASSWORD" = "your_secure_db_password_here" ]; then
    echo "❌ 错误：请在 .env 文件中设置安全的数据库密码"
    exit 1
fi

# 检查JWT密钥
if [ -z "$JWT_SECRET" ] || [ "$JWT_SECRET" = "your_jwt_secret_key_at_least_64_characters_long_random_string_here" ]; then
    echo "❌ 错误：请在 .env 文件中设置安全的 JWT 密钥（至少64字符）"
    exit 1
fi

if [ ${#JWT_SECRET} -lt 64 ]; then
    echo "❌ 错误：JWT 密钥长度不足，需要至少 64 个字符"
    exit 1
fi

# 检查微信配置（可选）
if [ -z "$WECHAT_OPEN_APP_ID" ] || [ "$WECHAT_OPEN_APP_ID" = "your_wechat_app_id_here" ]; then
    echo "⚠️  警告：未配置微信开放平台 AppID，微信登录功能将不可用"
fi

if [ -z "$WECHAT_OPEN_APP_SECRET" ] || [ "$WECHAT_OPEN_APP_SECRET" = "your_wechat_app_secret_here" ]; then
    echo "⚠️  警告：未配置微信开放平台 AppSecret，微信登录功能将不可用"
fi

# 创建必要的目录
echo "📁 创建必要的目录..."
mkdir -p logs
mkdir -p mysql/conf.d
mkdir -p redis
mkdir -p nginx/conf.d
mkdir -p nginx/ssl

# 生成MySQL配置文件
if [ ! -f "mysql/conf.d/my.cnf" ]; then
    echo "📄 生成 MySQL 配置文件..."
    cat > mysql/conf.d/my.cnf << EOF
[mysql]
default-character-set=utf8mb4

[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
max_connections=1000
innodb_buffer_pool_size=512M
slow_query_log=1
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=2
EOF
fi

# 生成Redis配置文件
if [ ! -f "redis/redis.conf" ]; then
    echo "📄 生成 Redis 配置文件..."
    cat > redis/redis.conf << EOF
bind 0.0.0.0
port 6379
timeout 300
keepalive 60
maxmemory 256mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
appendonly yes
appendfsync everysec
EOF
fi

# 生成Nginx配置文件
if [ ! -f "nginx/conf.d/default.conf" ]; then
    echo "📄 生成 Nginx 配置文件..."
    cat > nginx/conf.d/default.conf << EOF
server {
    listen 80;
    server_name localhost;

    # 前端静态文件
    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
        try_files \$uri \$uri/ /index.html;
    }

    # API代理
    location /api/ {
        proxy_pass http://app:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    # 健康检查
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
EOF
fi

# 检查Docker和Docker Compose
echo "🐳 检查 Docker 环境..."
if ! command -v docker &> /dev/null; then
    echo "❌ 错误：未找到 Docker，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ 错误：未找到 Docker Compose，请先安装 Docker Compose"
    exit 1
fi

# 构建并启动服务
echo "🔨 构建并启动服务..."
docker-compose down --remove-orphans
docker-compose build --no-cache
docker-compose up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 10

# 检查服务状态
echo "🔍 检查服务状态..."
docker-compose ps

# 检查应用健康状态
echo "🏥 检查应用健康状态..."
if curl -f http://localhost/health > /dev/null 2>&1; then
    echo "✅ 应用启动成功！"
    echo "🌐 前端地址: http://localhost"
    echo "📡 后端API: http://localhost/api"
else
    echo "⚠️  应用可能还在启动中，请稍等几分钟后访问"
fi

echo "📋 查看日志："
echo "  docker-compose logs -f app      # 查看应用日志"
echo "  docker-compose logs -f mysql   # 查看数据库日志"
echo "  docker-compose logs -f redis   # 查看缓存日志"
echo "  docker-compose logs -f nginx   # 查看代理日志"

echo "🛑 停止服务："
echo "  docker-compose down"

echo "🧹 清理数据："
echo "  docker-compose down -v        # 注意：这会删除所有数据"

echo "🎉 Uiineed Todo List 启动完成！"