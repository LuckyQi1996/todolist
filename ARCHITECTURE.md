# 代码架构文档

## 项目概述

Uiineed Todo List 是一个现代化的全栈待办事项管理应用，支持纯前端本地存储和后端云端同步两种模式。前端基于 Vue 2.x 的极简主义设计，后端采用 Spring Boot + MySQL 架构，支持微信扫码登录和数据持久化。

## 架构演进

### v1.0 - 纯前端架构
- 静态网页应用，数据存储在浏览器 localStorage
- 无需服务器，支持离线使用
- 单用户本地数据管理

### v2.0 - 全栈架构（当前版本）
- 前后端分离，支持用户认证和多设备同步
- 微信扫码登录 + JWT 认证
- MySQL 数据持久化
- Docker 容器化部署
- 向下兼容 v1.0 的纯前端模式

## 整体架构

### 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                         前端层 (Frontend)                        │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │   index.html    │  │  index-zh.html  │  │   login.html    │  │
│  │    (英文版)      │  │    (中文版)      │  │   (登录页面)     │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │              Vue.js 2.x + API Service Layer               │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                                │
                                │ HTTPS / JWT
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                         网关层 (Gateway)                        │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                     Nginx 反向代理                          │  │
│  │            • SSL 终止 • 负载均衡 • 静态文件服务               │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                                │
                                │ HTTP
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        应用层 (Application)                      │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                Spring Boot 应用程序                         │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  │ AuthController│  │TodoController│  │   安全过滤器链        │  │
│  │  └─────────────┘  └─────────────┘  │  • JWT 认证           │  │
│  │  ┌─────────────┐  ┌─────────────┐  │  • 微信 OAuth2       │  │
│  │  │UserService  │  │TodoService  │  │  • CORS 配置          │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                                │
                                │ JDBC
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                         数据层 (Data)                           │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │     MySQL       │  │     Redis       │  │   文件存储       │  │
│  │   (主数据库)     │  │    (缓存)       │  │   (日志/备份)    │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 项目目录结构

```
uiineed-todo-list/
├── 📁 前端应用 (Frontend Application)
│   ├── index.html                    # 英文版主应用文件
│   ├── index-zh.html                 # 中文版主应用文件
│   ├── login.html                    # 登录页面 (新增)
│   └── 📁 public/                    # 静态资源目录
│       ├── 📁 css/                   # 样式文件
│       │   ├── normalize.css         # CSS 重置样式
│       │   ├── style.scss            # SCSS 源文件
│       │   ├── style.css             # 编译后的 CSS
│       │   └── style.min.css         # 压缩版 CSS
│       ├── 📁 js/                   # JavaScript 文件
│       │   └── vue.js                # Vue 2.x 库文件
│       └── 📁 img/                  # 图片资源
│           ├── favicon.png           # 网站图标
│           ├── todo.svg              # 待办事项图标
│           ├── delete.svg            # 删除图标
│           ├── complete.svg          # 完成图标
│           ├── restore.svg           # 恢复图标
│           ├── design.svg            # 设计图标
│           ├── author.jpg            # 作者头像
│           ├── wechat_qrcode.jpg     # 微信二维码
│           ├── 📁 social/            # 社交媒体图标
│           │   ├── github.svg        # GitHub 图标
│           │   ├── x.svg             # X (Twitter) 图标
│           │   ├── mail.svg          # 邮件图标
│           │   ├── dribbble.svg      # Dribbble 图标
│           │   ├── bluesky.svg       # Bluesky 图标
│           │   ├── bilibili.svg      # 哔哩哔哩图标
│           │   ├── zcool.svg         # 站酷图标
│           │   ├── behance.svg       # Behance 图标 (新增)
│           │   └── xiaohongshu.svg   # 小红书图标
│           └── 📁 ricocc/            # 项目预览图
│               ├── preview-uiineed-todo-list-zh.jpg
│               ├── preview-uiineed-todo-list-zh-2.jpg
│               ├── preview-uiineed-todo-list-en.jpg
│               ├── preview-uiineed-todo-list-en-2.jpg
│               └── zanshangma.jpg     # 赞赏码
│
├── 📁 后端应用 (Backend Application) (新增)
│   └── 📁 src/main/java/com/uiineed/todo/
│       ├── 📁 config/                # 配置类
│       │   ├── JwtConfig.java        # JWT 配置
│       │   └── WeChatConfig.java     # 微信配置
│       ├── 📁 controller/            # 控制器层
│       │   ├── AuthController.java   # 认证控制器
│       │   └── TodoController.java   # 待办事项控制器
│       ├── 📁 entity/                # 实体类
│       │   ├── User.java             # 用户实体
│       │   ├── Todo.java             # 待办事项实体
│       │   └── TodoCategory.java     # 分类实体
│       ├── 📁 mapper/                # 数据访问层
│       │   ├── UserMapper.java       # 用户数据访问
│       │   └── TodoMapper.java       # 待办事项数据访问
│       ├── 📁 service/               # 业务逻辑层
│       │   ├── UserService.java      # 用户服务
│       │   ├── TodoService.java      # 待办事项服务
│       │   └── WeChatService.java    # 微信服务
│       ├── 📁 security/              # 安全相关
│       │   ├── JwtAuthenticationFilter.java  # JWT 认证过滤器
│       │   ├── JwtAuthenticationEntryPoint.java # JWT 认证入口点
│       │   └── SecurityConfig.java   # 安全配置
│       ├── 📁 common/                # 通用类
│       │   ├── ApiResult.java        # API 响应结果
│       │   └── ResultCode.java       # 结果状态码
│       ├── 📁 util/                  # 工具类
│       │   └── JwtUtil.java          # JWT 工具类
│       ├── 📁 vo/                    # 值对象
│       │   ├── LoginResponse.java    # 登录响应
│       │   ├── QrCodeResponse.java   # 二维码响应
│       │   ├── TodoResponse.java     # 待办事项响应
│       │   ├── TodoCreateRequest.java # 创建请求
│       │   └── TodoUpdateRequest.java # 更新请求
│       └── 📁 exception/             # 异常处理
│           ├── GlobalExceptionHandler.java # 全局异常处理器
│           └── BusinessException.java     # 业务异常
│   ├── 📁 database/                  # 数据库脚本
│   │   └── schema.sql                # 数据库结构
│   ├── Dockerfile                    # Docker 构建文件
│   └── pom.xml                       # Maven 配置
│
├── 📁 前端扩展 (Frontend Extensions) (新增)
│   ├── api-service.js                # API 服务层
│   └── login.html                    # 登录页面
│
├── 📁 部署配置 (Deployment Configuration) (新增)
│   ├── 📁 nginx/                     # Nginx 配置
│   │   └── nginx.conf                # Nginx 配置文件
│   └── docker-compose.yml            # Docker 编排文件
│
├── 📄 文档 (Documentation)
│   ├── README.md                     # 项目说明文档
│   ├── CLAUDE.md                     # Claude 开发指南
│   ├── ARCHITECTURE.md               # 架构设计文档 (本文件)
│   ├── BACKEND_API.md                # 后端 API 文档 (新增)
│   ├── README_BACKEND.md             # 后端集成说明 (新增)
│   └── LICENSE                       # 开源许可证
```

## 技术栈

### 前端技术栈 (Frontend Technology Stack)

#### 核心框架
- **Vue.js 2.x** - 渐进式 JavaScript 框架
  - 响应式数据绑定
  - 组件化开发
  - 生命周期管理
  - 过渡动画系统

#### 样式技术
- **SCSS/Sass** - CSS 预处理器
  - 变量和混入
  - 嵌套规则
  - 模块化样式
- **CSS3** - 现代样式特性
  - Flexbox 布局
  - CSS Grid
  - 自定义属性 (CSS Variables)
  - 动画和过渡

#### 构建工具
- **无构建工具架构** - 保持简单性
  - 直接在浏览器中运行
  - CDN 资源加载
  - 零配置开发

### 后端技术栈 (Backend Technology Stack)

#### 核心框架
- **Spring Boot 2.7.15** - Java 企业级应用框架
  - 自动配置
  - 嵌入式服务器 (Tomcat)
  - 生产就绪特性
  - 丰富的生态系统

#### 数据访问层
- **MyBatis Plus** - ORM 框架
  - 简化 CRUD 操作
  - 代码生成器
  - 分页插件
  - 条件构造器

#### 数据库
- **MySQL 8.0** - 关系型数据库
  - ACID 事务支持
  - JSON 数据类型
  - 性能优化
  - 高可用性

#### 缓存
- **Redis 7** - 内存数据库
  - 高性能缓存
  - 数据结构丰富
  - 持久化支持
  - 集群模式

#### 认证与安全
- **JWT (JSON Web Token)** - 无状态认证
  - 令牌-based 认证
  - 跨域支持
  - 可扩展性
- **Spring Security** - 安全框架
  - 认证和授权
  - CORS 配置
  - 密码加密

#### 第三方集成
- **微信开放平台** - 用户认证
  - OAuth 2.0 授权
  - 扫码登录
  - 用户信息获取

### 部署技术栈 (Deployment Technology Stack)

#### 容器化
- **Docker** - 容器化平台
  - 应用容器化
  - 环境一致性
  - 快速部署
- **Docker Compose** - 多容器编排
  - 服务编排
  - 网络配置
  - 数据卷管理

#### 反向代理
- **Nginx** - Web 服务器
  - 反向代理
  - 负载均衡
  - SSL 终止
  - 静态文件服务

#### 监控与日志
- **应用日志** - Spring Boot Actuator
- **访问日志** - Nginx 访问记录
- **健康检查** - 服务状态监控

### 开发工具 (Development Tools)

#### 后端开发
- **Maven 3.8+** - 项目管理和构建工具
- **JDK 11+** - Java 开发环境
- **MySQL Workbench** - 数据库管理工具

#### 版本控制
- **Git** - 分布式版本控制系统
- **GitHub** - 代码托管平台

#### API 开发
- **RESTful API** - 架构风格
- **JSON** - 数据交换格式
- **Postman** - API 测试工具

## 架构模式

### 1. 前后端分离架构 (Frontend-Backend Separation)

```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端 (Client)                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                     浏览器应用                               │  │
│  │  ┌─────────────────┐  ┌─────────────────┐                  │  │
│  │  │   Vue.js SPA    │  │  API Service    │                  │  │
│  │  │                 │  │     Layer       │                  │  │
│  │  │  • 状态管理      │  │  • HTTP 请求     │                  │  │
│  │  │  • 路由管理      │  │  • 响应处理      │                  │  │
│  │  │  • 组件渲染      │  │  • 错误处理      │                  │  │
│  │  └─────────────────┘  └─────────────────┘                  │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                                │
                                │ HTTPS REST API
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        服务器 (Server)                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                   Spring Boot 应用                          │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  │   Web层     │  │   Service层 │  │     Repository层     │  │
│  │  │ Controller  │  │   Service   │  │      Mapper         │  │
│  │  │             │  │             │  │                     │  │
│  │  │ • REST API  │  │ • 业务逻辑   │  │ • 数据访问           │  │
│  │  │ • 参数校验   │  │ • 事务管理   │  │ • SQL 映射           │  │
│  │  │ • 异常处理   │  │ • 缓存管理   │  │ • 连接池管理         │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────────┘  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 2. 数据架构模式 (Data Architecture Pattern)

#### 多层数据存储
```
┌─────────────────────────────────────────────────────────────────┐
│                      数据存储层次                               │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │   缓存层        │  │   应用层        │  │   持久层        │  │
│  │                 │  │                 │  │                 │  │
│  │  • Redis 缓存   │  │  • 内存数据     │  │  • MySQL 数据库  │  │
│  │  • 会话存储      │  │  • 临时状态     │  │  • 主数据存储     │  │
│  │  • 热点数据      │  │  • 计算结果     │  │  • 事务保证      │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

#### 数据一致性策略
- **强一致性**: 数据库事务保证
- **最终一致性**: 缓存更新策略
- **本地优先**: 前端 localStorage 作为离线缓存

### 3. 安全架构模式 (Security Architecture Pattern)

#### 认证授权流程
```
用户请求 → 微信扫码 → JWT Token → API 调用 → 数据访问
    ↑           ↓           ↓           ↓           ↓
   重定向     获取授权令牌   令牌验证    权限检查    业务逻辑
```

#### 安全防护层次
1. **网络层**: HTTPS 加密传输
2. **应用层**: JWT 认证 + CORS 配置
3. **数据层**: SQL 注入防护 + 数据加密
4. **业务层**: 输入验证 + 权限控制

## 认证与安全架构详解

### 1. 微信 OAuth2.0 认证流程

```
┌─────────────────────────────────────────────────────────────────┐
│                    微信登录认证流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  用户访问登录页面 ──┐                                          │
│                     │                                          │
│                     ▼                                          │
│            前端请求二维码 ──┐                                   │
│                         │                                      │
│                         ▼                                      │
│                  后端生成微信授权URL                             │
│                         │                                      │
│                         ▼                                      │
│                  返回二维码给前端                                │
│                         │                                      │
│                         ▼                                      │
│                  用户使用微信扫码                                │
│                         │                                      │
│                         ▼                                      │
│               微信重定向到回调地址                               │
│                         │                                      │
│                         ▼                                      │
│                  后端处理微信回调                                │
│                  ┌────────┬────────┐                           │
│                  │ 获取用户 │ 交换Token │                           │
│                  │ 信息     │ 和刷新Token │                           │
│                  └────────┴────────┘                           │
│                         │                                      │
│                         ▼                                      │
│                  生成JWT Token并返回                              │
│                         │                                      │
│                         ▼                                      │
│                  前端保存Token并跳转                               │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

#### 关键组件说明

**1. WeChatConfig.java**
- 微信开放平台配置管理
- AppID 和 AppSecret 安全存储
- 回调地址配置

**2. WeChatService.java**
- 微信授权码交换
- 用户信息获取
- Token 刷新逻辑

**3. JwtUtil.java**
- JWT Token 生成和验证
- 用户身份信息编码
- Token 过期时间管理

### 2. JWT 认证体系

#### Token 结构
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "用户ID",
    "nickname": "用户昵称",
    "avatar": "头像URL",
    "iat": 1640995200,
    "exp": 1641600000,
    "roles": ["USER"]
  },
  "signature": "HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)"
}
```

#### Token 生命周期管理
```
Token 获取 → Token 存储 → Token 使用 → Token 刷新 → Token 锭销
    ↑           ↓           ↓           ↓           ↓
   登录成功   LocalStorage  每次API请求   自动刷新     登出操作
```

#### 安全措施
- **密钥管理**: 生产环境使用强密钥
- **过期时间**: 合理设置 Token 有效期
- **刷新机制**: 支持无感刷新
- **黑名单**: 支持 Token 主动失效

### 3. API 安全防护

#### 请求验证流程
```
HTTP 请求 → CORS 检查 → JWT 验证 → 权限检查 → 业务处理
     ↑           ↓           ↓          ↓          ↓
   前端发起    域名验证      Token解析   角色权限    控制器执行
```

#### 安全过滤器链 (Spring Security)
```
SecurityConfig.java
├── JwtAuthenticationEntryPoint     # 认证失败处理
├── JwtAuthenticationFilter        # JWT 认证过滤器
├── CorsConfigurationSource        # CORS 配置源
└── PasswordEncoder                # 密码编码器 (预留)
```

#### 关键安全配置
```java
// JWT 认证配置
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // 禁用 CSRF (JWT 方式)
    // 启用 CORS
    // 配置会话管理为 STATELESS
    // 配置请求权限规则
}
```

### 4. 数据安全策略

#### 数据传输安全
- **HTTPS 强制**: 生产环境强制使用 HTTPS
- **API 加密**: 敏感数据传输加密
- **请求签名**: 关键操作请求签名验证

#### 数据存储安全
```sql
-- 用户敏感信息存储
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    openid VARCHAR(64) UNIQUE NOT NULL,    -- 微信 OpenID
    nickname VARCHAR(100),                 -- 用户昵称
    avatar_url VARCHAR(500),               -- 头像URL
    language VARCHAR(10) DEFAULT 'zh_CN',  -- 语言偏好
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 数据访问控制
- **用户隔离**: 基于用户ID的数据隔离
- **权限验证**: 每个API调用验证用户权限
- **SQL注入防护**: 使用MyBatis参数化查询
- **输入验证**: 严格的参数校验和格式验证

### 5. 异常处理机制

#### 全局异常处理器
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ApiResult<?> handleBusinessException(BusinessException e);

    @ExceptionHandler(AuthenticationException.class)
    public ApiResult<?> handleAuthenticationException(AuthenticationException e);

    @ExceptionHandler(Exception.class)
    public ApiResult<?> handleGenericException(Exception e);
}
```

#### 错误响应标准化
```json
{
  "code": 401,
  "message": "认证失败，请重新登录",
  "timestamp": 1640995200000,
  "data": null,
  "path": "/api/todos"
}
```

### 6. 安全监控与审计

#### 安全事件记录
- **登录日志**: 用户登录成功/失败记录
- **操作日志**: 关键操作审计追踪
- **异常日志**: 安全异常实时告警

#### 访问控制
- **频率限制**: API 访问频率控制
- **IP 白名单**: 管理端IP访问限制
- **异常检测**: 异常访问模式识别

## 核心文件详细说明

### 1. 主要应用文件

#### `index.html` / `index-zh.html`
**功能**: 应用程序的入口文件和主要容器
**作用**:
- 定义完整的 HTML 结构和布局
- 集成 Vue 2.x 框架
- 实现自定义弹窗系统（替换原生 alert/confirm）
- 包含完整的待办事项管理逻辑
- 处理数据导入/导出功能
- 实现拖拽排序功能
- 管理语言检测和切换

**关键组件**:
- **输入组件**: 添加新待办事项的输入框
- **列表组件**: 显示和管理待办事项列表
- **过滤组件**: 按状态筛选待办事项
- **侧边栏**: 快捷操作和批量管理功能
- **导航栏**: 语言切换和关于信息

### 2. 样式文件系统

#### `public/css/normalize.css`
**功能**: CSS 样式重置
**作用**: 统一不同浏览器的默认样式，确保跨浏览器一致性

#### `public/css/style.scss`
**功能**: 主要样式源文件
**架构特点**:
- 使用 SCSS 预处理器
- 采用 CSS 自定义属性（CSS Variables）实现主题化
- 响应式设计，使用 `@mixin respond-to()` 处理断点
- 模块化组织样式代码

**主要模块**:
- **变量定义**: 颜色、字体、间距等设计系统
- **基础样式**: HTML 元素重置和通用样式
- **布局组件**: 容器、网格系统
- **UI 组件**: 按钮、输入框、卡片等
- **动画系统**: 过渡效果和关键帧动画
- **响应式**: 移动端适配

#### `public/css/style.css` / `style.min.css`
**功能**: 编译后的 CSS 文件
**作用**: 浏览器可直接使用的样式文件

### 3. JavaScript 框架

#### `public/js/vue.js`
**功能**: Vue 2.x 框架库
**作用**: 提供响应式数据绑定和组件化开发能力

## 应用架构模式

### 1. 数据流架构

```
用户操作 → Vue 实例 → LocalStorage → UI 更新
    ↑                                    ↓
UI 渲染 ← 响应式更新 ← 数据变化监听 ←
```

**关键数据结构**:
```javascript
{
    todos: [
        {
            id: Number,           // 唯一标识
            title: String,        // 待办内容
            completed: Boolean,   // 完成状态
            removed: Boolean      // 删除状态
        }
    ],
    recycleBin: Array,           // 回收站
    intention: String,           // 当前过滤状态
    slogan: String               // 个性化标语
}
```

### 2. 状态管理

**LocalStorage 存储结构**:
- `uiineed-todos`: 主要待办事项数据
- `uiineed-slogan`: 用户自定义标语
- `uiineed-todos-lang`: 语言偏好设置

**Vue 实例状态**:
- 响应式数据绑定
- 深度监听 todos 数组变化
- 计算属性处理过滤逻辑
- 方法管理用户交互

### 3. 组件化架构

虽然使用 Vue 2.x，但采用单文件组件模式：

**主要功能模块**:
1. **TodoInput**: 输入和添加功能
2. **TodoList**: 列表显示和操作
3. **TodoItem**: 单个待办事项
4. **FilterBar**: 过滤和筛选
5. **SideBar**: 批量操作和数据管理
6. **Navigation**: 语言切换和关于信息

### 4. 事件系统

**用户交互事件**:
- **键盘事件**: Enter 提交、Escape 取消
- **鼠标事件**: 双击编辑、拖拽排序
- **触摸事件**: 移动端交互优化

**Vue 生命周期**:
- `mounted`: 初始化和事件绑定
- `watch`: 数据变化监听和持久化

## 功能实现架构

### 1. 待办事项管理

```javascript
// 核心操作流程
添加: 输入验证 → 生成ID → 添加到数组 → 保存到LocalStorage
编辑: 双击触发 → 进入编辑模式 → 保存更改
完成: 状态切换 → 视觉更新 → 数据持久化
删除: 软删除 → 移入回收站 → 可恢复
```

### 2. 拖拽排序系统

**实现原理**:
- HTML5 Drag and Drop API
- Vue 过渡动画系统
- 自定义动画钩子函数

**事件处理**:
- `dragstart`: 开始拖拽
- `dragenter`: 进入目标区域
- `dragover`: 拖拽过程中
- `beforeEnter/enter/afterEnter`: 动画控制

### 3. 数据导入/导出

**导出流程**:
1. 读取 localStorage 数据
2. JSON 格式化
3. 生成时间戳文件名
4. 创建下载链接

**导入流程**:
1. 文件选择和验证
2. 内容解析（JSON/TXT）
3. ID 重新分配
4. 合并到现有数据

### 4. 响应式设计架构

**断点系统**:
```scss
$breakpoints: (
    'xs': 350px,    // 超小屏
    'sm': 640px,    // 小屏
    'md': 768px,    // 中屏
    'lg': 992px     // 大屏
);
```

**适配策略**:
- 移动端优先设计
- 断点响应式布局
- 触摸友好的交互设计
- 灵活的网格系统

## 性能优化策略

### 1. 资源优化
- **图片优化**: Base64 编码小图标，减少 HTTP 请求
- **CSS 压缩**: 提供压缩版样式文件
- **字体优化**: 使用系统字体栈

### 2. 运行时优化
- **事件委托**: 减少事件监听器数量
- **防抖处理**: 输入验证和自动保存
- **动画优化**: 使用 CSS3 硬件加速

### 3. 数据优化
- **懒加载**: 按需加载数据
- **增量更新**: 只更新变化的数据项
- **本地缓存**: 避免重复网络请求

## 部署架构详解

### 1. Docker 容器化部署

#### 容器架构图
```
┌─────────────────────────────────────────────────────────────────┐
│                      Docker Host 环境                           │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────┐  │
│  │    Nginx    │  │ Spring Boot │  │    MySQL    │  │  Redis  │  │
│  │   容器      │  │    容器      │  │    容器      │  │  容器   │  │
│  │             │  │             │  │             │  │         │  │
│  │ • 反向代理   │  │ • 应用服务   │  │ • 数据存储   │  │ • 缓存  │  │
│  │ • SSL终止   │  │ • JWT认证   │  │ • 主从复制   │  │ • 会话  │  │
│  │ • 静态文件   │  │ • 业务逻辑   │  │ • 备份恢复   │  │ • 队列  │  │
│  │ • 负载均衡   │  │ • API接口   │  │ • 数据持久化 │  │ • 持久化│  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────┘  │
│         │               │               │               │         │
│         └───────────────┼───────────────┼───────────────┼─────────┘
│                         │               │               │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                  Docker 网络                               │  │
│  │           uiineed-network (bridge)                        │  │
│  └─────────────────────────────────────────────────────────────┘  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                    数据卷                                  │  │
│  │  mysql_data │ redis_data │ app_logs │ nginx_ssl │ frontend  │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

#### Docker Compose 配置详解
```yaml
# docker-compose.yml 核心配置
version: '3.8'

services:
  # 前端代理层
  nginx:
    image: nginx:alpine
    ports: ["80:80", "443:443"]
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./frontend:/usr/share/nginx/html:ro
    depends_on: [app]

  # 应用服务层
  app:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/uiineed_todo
      JWT_SECRET: ${JWT_SECRET}
      WECHAT_OPEN_APP_ID: ${WECHAT_OPEN_APP_ID}
    depends_on: [mysql, redis]
    volumes: [app_logs:/app/logs]

  # 数据服务层
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: uiineed_todo
    volumes:
      - mysql_data:/var/lib/mysql
      - ./backend/database/schema.sql:/docker-entrypoint-initdb.d/schema.sql:ro

  redis:
    image: redis:7-alpine
    volumes: [redis_data:/data]
    command: redis-server --appendonly yes
```

### 2. 生产环境部署策略

#### 部署架构层次
```
┌─────────────────────────────────────────────────────────────────┐
│                        负载均衡层                               │
│                    (云服务商 ELB/SLB)                          │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Web 服务器集群                              │
│                  Nginx + SSL 终止                              │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    应用服务器集群                                │
│              Spring Boot 多实例部署                            │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      数据库集群                                  │
│                   MySQL 主从架构                                │
└─────────────────────────────────────────────────────────────────┘
```

#### 环境配置管理
```bash
# 开发环境 (dev)
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/uiineed_todo_dev
JWT_SECRET=dev-jwt-secret

# 测试环境 (test)
SPRING_PROFILES_ACTIVE=test
SPRING_DATASOURCE_URL=jdbc:mysql://test-db:3306/uiineed_todo_test
JWT_SECRET=test-jwt-secret

# 生产环境 (prod)
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://prod-db-cluster:3306/uiineed_todo
JWT_SECRET=${JWT_SECRET_FROM_VAULT}
WECHAT_OPEN_APP_ID=${WECHAT_APP_ID_FROM_VAULT}
```

### 3. CI/CD 部署流水线

#### 持续集成流程
```
代码提交 → 自动构建 → 单元测试 → 代码扫描 → 构建镜像 → 部署测试环境 → 自动化测试 → 部署生产环境
    ↑         ↓          ↓          ↓          ↓           ↓              ↓           ↓
   Git Push   Maven      Test       SonarQube   Docker     Test Env       E2E Test    Production
```

#### Dockerfile 最佳实践
```dockerfile
# 多阶段构建示例
FROM maven:3.8.4-openjdk-11-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/todo-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 4. 监控与运维

#### 应用监控体系
```
┌─────────────────────────────────────────────────────────────────┐
│                      监控系统架构                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   应用监控   │  │   系统监控   │  │      日志监控            │  │
│  │             │  │             │  │                         │  │
│  │ • Spring    │  │ • Prometheus │  │ • ELK Stack             │  │
│  │   Actuator  │  │ • Grafana   │  │ • 应用日志               │  │
│  │ • JVM指标   │  │ • 系统指标   │  │ • 访问日志               │  │
│  │ • 业务指标   │  │ • 告警规则   │  │ • 错误追踪               │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                    健康检查与告警                             │  │
│  │  • 服务健康检查 • 自动恢复 • 告警通知 • 故障转移             │  │
│  └─────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

#### 关键监控指标
- **应用指标**: QPS、响应时间、错误率、JVM状态
- **业务指标**: 用户活跃度、待办事项完成率、登录成功率
- **系统指标**: CPU、内存、磁盘、网络使用率
- **数据库指标**: 连接池状态、查询性能、慢查询监控

### 5. 数据备份与恢复

#### 备份策略
```bash
# MySQL 数据库备份
#!/bin/bash
# 每日全量备份
mysqldump --single-transaction --routines --triggers \
  -h mysql-host -u backup-user -p uiineed_todo > \
  backup_$(date +%Y%m%d_%H%M%S).sql

# 增量备份 (基于binlog)
mysqlbinlog --start-datetime="2023-12-01 00:00:00" \
  --stop-datetime="2023-12-01 23:59:59" \
  mysql-bin.000001 > incremental_$(date +%Y%m%d).sql
```

#### Redis 缓存备份
```bash
# Redis RDB 快照备份
redis-cli BGSAVE
cp /var/lib/redis/dump.rdb /backup/redis/dump_$(date +%Y%m%d_%H%M%S).rdb

# Redis AOF 备份
redis-cli BGREWRITEAOF
cp /var/lib/redis/appendonly.aof /backup/redis/appendonly_$(date +%Y%m%d_%H%M%S).aof
```

### 6. 安全加固措施

#### 网络安全
- **防火墙配置**: 只开放必要端口 (80, 443, 22)
- **VPN访问**: 管理端口通过VPN访问
- **DDoS防护**: 云服务商DDoS防护
- **WAF配置**: Web应用防火墙

#### 应用安全
- **密钥管理**: 使用Vault或KMS管理敏感信息
- **证书管理**: 自动SSL证书更新 (Let's Encrypt)
- **安全扫描**: 定期进行安全漏洞扫描
- **访问控制**: 基于角色的访问控制 (RBAC)

## 数据流架构

### 1. 用户操作数据流

```
┌─────────────────────────────────────────────────────────────────┐
│                     用户操作数据流                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  用户操作 ──┐                                                   │
│             │                                                  │
│             ▼                                                  │
│      前端事件处理                                                  │
│             │                                                  │
│             ▼                                                  │
│      API调用封装                                                  │
│             │                                                  │
│             ▼                                                  │
│      HTTP请求发送                                                │
│             │                                                  │
│             ▼                                                  │
│      Nginx反向代理                                                │
│             │                                                  │
│             ▼                                                  │
│      Spring Boot应用                                             │
│             │                                                  │
│             ▼                                                  │
│      JWT认证验证                                                  │
│             │                                                  │
│             ▼                                                  │
│      业务逻辑处理                                                  │
│             │                                                  │
│             ▼                                                  │
│      数据库操作                                                  │
│             │                                                  │
│             ▼                                                  │
│      Redis缓存更新                                                │
│             │                                                  │
│             ▼                                                  │
│      响应数据返回                                                  │
│             │                                                  │
│             ▼                                                  │
│      前端状态更新                                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2. 认证数据流

```
┌─────────────────────────────────────────────────────────────────┐
│                     微信认证数据流                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  用户点击微信登录 ──┐                                            │
│                   │                                           │
│                   ▼                                           │
│            前端请求二维码                                          │
│                   │                                           │
│                   ▼                                           │
│            后端生成微信授权URL                                      │
│                   │                                           │
│                   ▼                                           │
│            返回二维码和状态码                                        │
│                   │                                           │
│                   ▼                                           │
│            用户微信扫码                                            │
│                   │                                           │
│                   ▼                                           │
│            微信重定向到回调地址                                       │
│                   │                                           │
│                   ▼                                           │
│            后端接收code和state参数                                   │
│                   │                                           │
│                   ▼                                           │
│            调用微信API获取access_token                              │
│                   │                                           │
│                   ▼                                           │
│            获取用户信息                                            │
│                   │                                           │
│                   ▼                                           │
│            创建/更新用户记录                                        │
│                   │                                           │
│                   ▼                                           │
│            生成JWT Token和RefreshToken                             │
│                   │                                           │
│                   ▼                                           │
│            返回登录成功响应                                         │
│                   │                                           │
│                   ▼                                           │
│            前端保存Token并跳转主页面                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## API 集成架构

### 1. 前端API服务层

#### API Service 架构
```javascript
// frontend/api-service.js 核心架构
class ApiService {
    constructor() {
        this.baseURL = process.env.API_BASE_URL || 'http://localhost:8080/api';
        this.token = localStorage.getItem('uiineed-jwt-token');
    }

    // HTTP请求拦截器
    request(config) {
        // 添加认证头
        config.headers = {
            ...config.headers,
            'Authorization': `Bearer ${this.token}`
        };

        // 统一错误处理
        return this.httpClient.request(config)
            .catch(this.handleError.bind(this));
    }

    // API方法映射
    async getTodos(params = {}) {
        return this.request({
            method: 'GET',
            url: '/todos',
            params
        });
    }

    async createTodo(todoData) {
        return this.request({
            method: 'POST',
            url: '/todos',
            data: todoData
        });
    }

    // 统一错误处理
    handleError(error) {
        if (error.response?.status === 401) {
            // Token过期，尝试刷新
            return this.refreshToken();
        }
        throw error;
    }
}
```

### 2. 双模式兼容架构

#### 前端数据适配层
```javascript
// 数据存储适配器
class DataAdapter {
    constructor() {
        this.mode = this.detectMode(); // 'local' or 'cloud'
        this.apiService = new ApiService();
        this.localStorage = new LocalStorageService();
    }

    // 自动检测运行模式
    detectMode() {
        return this.apiService.isAvailable() ? 'cloud' : 'local';
    }

    // 统一的数据访问接口
    async getTodos() {
        if (this.mode === 'cloud') {
            return await this.apiService.getTodos();
        } else {
            return this.localStorage.getTodos();
        }
    }

    async createTodo(todoData) {
        if (this.mode === 'cloud') {
            return await this.apiService.createTodo(todoData);
        } else {
            return this.localStorage.createTodo(todoData);
        }
    }

    // 数据同步机制 (云模式)
    async syncData() {
        if (this.mode === 'cloud') {
            const localData = this.localStorage.getTodos();
            const remoteData = await this.apiService.getTodos();

            // 合并数据逻辑
            return this.mergeData(localData, remoteData);
        }
    }
}
```

### 3. 版本兼容性

#### API版本管理
```java
// 后端API版本控制
@RestController
@RequestMapping("/api/v1/todos")
public class TodoControllerV1 {
    // v1版本的API实现
}

@RestController
@RequestMapping("/api/v2/todos")
public class TodoControllerV2 {
    // v2版本的API实现 (向后兼容)
}
```

#### 前端版本适配
```javascript
// 前端API版本管理
class ApiVersionManager {
    constructor() {
        this.version = this.detectApiVersion();
    }

    detectApiVersion() {
        // 根据后端支持的版本选择合适的API版本
        return 'v1'; // 或 'v2'
    }

    getApiUrl(endpoint) {
        return `/api/${this.version}${endpoint}`;
    }
}
```

## 安全性考虑

### 1. 现代安全架构
- **多层防护**: 网络、应用、数据、业务四层安全防护
- **零信任原则**: 每个请求都需要验证和授权
- **最小权限原则**: 用户只能访问必要的资源
- **安全左移**: 在开发和测试阶段就考虑安全性

### 2. 数据安全与隐私
- **加密存储**: 敏感数据加密存储
- **传输加密**: HTTPS强制加密传输
- **访问日志**: 完整的用户访问日志记录
- **数据脱敏**: 日志中的敏感信息脱敏处理

### 3. 合规性要求
- **GDPR合规**: 支持用户数据导出和删除
- **数据本地化**: 根据要求数据存储在指定地区
- **安全审计**: 定期安全审计和渗透测试

## 扩展性设计

### 1. 微服务架构预留
- **服务拆分**: 预留用户服务、待办事项服务、通知服务等
- **服务发现**: 支持服务注册与发现机制
- **配置中心**: 集中化配置管理
- **熔断器**: 服务间调用熔断保护

### 2. 多租户架构
- **数据隔离**: 基于租户ID的数据隔离
- **资源隔离**: 计算资源按租户分配
- **配置隔离**: 租户级别的个性化配置
- **计费系统**: 基于使用量的计费模式

### 3. 国际化与本地化
- **多语言支持**: 动态语言包加载
- **时区处理**: 用户本地时区自动适配
- **货币格式**: 支持多种货币格式
- **文化适配**: 不同地区的文化差异适配

### 4. 性能扩展
- **水平扩展**: 支持应用服务器水平扩展
- **数据库扩展**: 支持读写分离和分库分表
- **缓存策略**: 多级缓存优化性能
- **CDN加速**: 静态资源CDN分发

这个现代化的架构设计确保了应用的高可用性、可扩展性、安全性和可维护性，同时保持了优秀的用户体验和开发效率。通过采用成熟的技术栈和最佳实践，系统具备了企业级应用的所有特性。

---

## API 接口参考

### 基础信息

- **基础URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

### 认证接口

#### 1. 获取微信登录二维码

获取微信扫码登录二维码。

**请求:**
```http
GET /auth/qrcode
```

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": 1698765432000,
  "data": {
    "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANS...",
    "state": "uuid-string",
    "authUrl": "https://open.weixin.qq.com/connect/qrconnect?...",
    "message": "请使用微信扫描二维码登录"
  }
}
```

#### 2. 微信登录回调

微信授权成功后的回调接口。

**请求:**
```http
GET /auth/wechat/callback?code=CODE&state=STATE
```

**参数:**
- `code`: 微信授权码
- `state`: 状态参数

**响应:**
```json
{
  "code": 200,
  "message": "登录成功",
  "timestamp": 1698765432000,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 1699365432000,
    "user": {
      "id": 1,
      "nickname": "用户昵称",
      "avatarUrl": "头像URL",
      "language": "zh_CN"
    }
  }
}
```

#### 3. 刷新Token

使用刷新Token获取新的访问Token。

**请求:**
```http
POST /auth/refresh
Content-Type: application/x-www-form-urlencoded

refreshToken=REFRESH_TOKEN
```

**响应:**
```json
{
  "code": 200,
  "message": "Token刷新成功",
  "timestamp": 1698765432000,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 1699365432000
  }
}
```

#### 4. 登出

**请求:**
```http
POST /auth/logout
Authorization: Bearer YOUR_TOKEN
```

**响应:**
```json
{
  "code": 200,
  "message": "登出成功",
  "timestamp": 1698765432000,
  "data": null
}
```

#### 5. 获取当前用户信息

**请求:**
```http
GET /auth/me
Authorization: Bearer YOUR_TOKEN
```

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": 1698765432000,
  "data": {
    "id": 1,
    "nickname": "用户昵称",
    "avatarUrl": "头像URL",
    "language": "zh_CN"
  }
}
```

### 待办事项管理接口

#### 1. 获取待办事项列表

**请求:**
```http
GET /todos?status=0&page=1&size=20
Authorization: Bearer YOUR_TOKEN
```

**参数:**
- `status` (可选): 状态过滤
  - `0`: 待办
  - `1`: 进行中
  - `2`: 已完成
  - `3`: 已取消
- `page`: 页码（默认1）
- `size`: 每页大小（默认20）

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": 1698765432000,
  "data": {
    "records": [
      {
        "id": 1,
        "title": "完成项目文档",
        "description": "需要完成的项目说明文档",
        "priority": 2,
        "status": 0,
        "completedAt": null,
        "dueDate": "2023-12-31 18:00:00",
        "reminderTime": "2023-12-30 09:00:00",
        "sortOrder": 1,
        "categoryId": 1,
        "completed": false,
        "removed": false,
        "deletedAt": null,
        "createdAt": "2023-12-01 10:00:00",
        "updatedAt": "2023-12-01 10:00:00"
      }
    ],
    "total": 10,
    "size": 20,
    "current": 1,
    "pages": 1
  }
}
```

#### 2. 创建待办事项

**请求:**
```http
POST /todos
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "title": "新待办事项",
  "description": "详细描述",
  "priority": 2,
  "dueDate": "2023-12-31 18:00:00",
  "reminderTime": "2023-12-30 09:00:00",
  "categoryId": 1
}
```

**响应:**
```json
{
  "code": 200,
  "message": "创建成功",
  "timestamp": 1698765432000,
  "data": {
    "id": 11,
    "title": "新待办事项",
    "description": "详细描述",
    "priority": 2,
    "status": 0,
    "completedAt": null,
    "dueDate": "2023-12-31 18:00:00",
    "reminderTime": "2023-12-30 09:00:00",
    "sortOrder": 11,
    "categoryId": 1,
    "completed": false,
    "removed": false,
    "deletedAt": null,
    "createdAt": "2023-12-01 10:00:00",
    "updatedAt": "2023-12-01 10:00:00"
  }
}
```

#### 3. 更新待办事项

**请求:**
```http
PUT /todos/11
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json

{
  "title": "更新后的标题",
  "description": "更新后的描述",
  "priority": 3,
  "status": 1,
  "dueDate": "2024-01-15 18:00:00",
  "reminderTime": "2024-01-14 09:00:00",
  "categoryId": 2
}
```

**响应:**
```json
{
  "code": 200,
  "message": "更新成功",
  "timestamp": 1698765432000,
  "data": {
    "id": 11,
    "title": "更新后的标题",
    "description": "更新后的描述",
    "priority": 3,
    "status": 1,
    "completedAt": null,
    "dueDate": "2024-01-15 18:00:00",
    "reminderTime": "2024-01-14 09:00:00",
    "sortOrder": 11,
    "categoryId": 2,
    "completed": false,
    "removed": false,
    "deletedAt": null,
    "createdAt": "2023-12-01 10:00:00",
    "updatedAt": "2023-12-01 11:00:00"
  }
}
```

#### 4. 标记完成/未完成

**标记完成:**
```http
PUT /todos/11/complete
Authorization: Bearer YOUR_TOKEN
```

**标记未完成:**
```http
PUT /todos/11/uncomplete
Authorization: Bearer YOUR_TOKEN
```

**响应:**
```json
{
  "code": 200,
  "message": "标记完成成功",
  "timestamp": 1698765432000,
  "data": null
}
```

#### 5. 删除/恢复待办事项

**删除:**
```http
DELETE /todos/11
Authorization: Bearer YOUR_TOKEN
```

**恢复:**
```http
PUT /todos/11/restore
Authorization: Bearer YOUR_TOKEN
```

**响应:**
```json
{
  "code": 200,
  "message": "删除成功",
  "timestamp": 1698765432000,
  "data": null
}
```

#### 6. 批量操作

**请求:**
```http
POST /todos/batch
Authorization: Bearer YOUR_TOKEN
Content-Type: application/x-www-form-urlencoded

action=complete&ids=1,2,3
```

**参数:**
- `action`: 操作类型
  - `complete`: 批量标记完成
  - `uncomplete`: 批量标记未完成
  - `delete`: 批量删除
- `ids`: 待办事项ID列表（逗号分隔）

**响应:**
```json
{
  "code": 200,
  "message": "批量标记完成成功",
  "timestamp": 1698765432000,
  "data": null
}
```

#### 7. 获取回收站

**请求:**
```http
GET /todos/trash?page=1&size=20
Authorization: Bearer YOUR_TOKEN
```

**响应:**
```json
{
  "code": 200,
  "message": "操作成功",
  "timestamp": 1698765432000,
  "data": {
    "records": [
      {
        "id": 12,
        "title": "已删除的待办事项",
        "description": "描述",
        "priority": 1,
        "status": 0,
        "completedAt": null,
        "dueDate": null,
        "reminderTime": null,
        "sortOrder": 12,
        "categoryId": 1,
        "completed": false,
        "removed": true,
        "deletedAt": "2023-11-30 15:30:00",
        "createdAt": "2023-11-20 10:00:00",
        "updatedAt": "2023-11-30 15:30:00"
      }
    ],
    "total": 5,
    "size": 20,
    "current": 1,
    "pages": 1
  }
}
```

### 状态码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未认证或token已过期 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 错误响应格式

```json
{
  "code": 400,
  "message": "参数校验失败",
  "timestamp": 1698765432000,
  "data": null
}
```

### 使用示例

#### JavaScript 前端调用示例

```javascript
// 设置API基础配置
const apiClient = {
  baseURL: 'http://localhost:8080/api',
  token: localStorage.getItem('uiineed-jwt-token')
};

// 获取待办事项
async function getTodos(params = {}) {
  const response = await fetch(`${apiClient.baseURL}/todos?${new URLSearchParams(params)}`, {
    headers: {
      'Authorization': `Bearer ${apiClient.token}`
    }
  });
  return response.json();
}

// 创建待办事项
async function createTodo(todoData) {
  const response = await fetch(`${apiClient.baseURL}/todos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiClient.token}`
    },
    body: JSON.stringify(todoData)
  });
  return response.json();
}

// 使用示例
getTodos({ status: 0, page: 1, size: 10 })
  .then(data => console.log('待办事项列表:', data));

createTodo({
  title: '学习Vue.js',
  description: '完成Vue.js官方教程学习',
  priority: 2
})
  .then(data => console.log('创建成功:', data));
```

#### cURL 命令示例

```bash
# 获取微信登录二维码
curl -X GET http://localhost:8080/api/auth/qrcode

# 获取待办事项列表
curl -X GET "http://localhost:8080/api/todos?page=1&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 创建待办事项
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "新待办事项",
    "description": "详细描述",
    "priority": 2
  }'

# 标记完成
curl -X PUT http://localhost:8080/api/todos/1/complete \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```