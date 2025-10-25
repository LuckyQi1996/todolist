# Uiineed Todo List

<div align="center">

[中文文档](#中文介绍) | [English](#english-intro)

<img src="public/img/ricocc/preview-uiineed-todo-list-zh.jpg" alt="Preview Chinese" width="640" height="auto" style="border-radius:12px;display:inline-block;margin:12px;">
<img src="public/img/ricocc/preview-uiineed-todo-list-zh-2.jpg" alt="Preview Chinese 2" width="640" height="auto" style="border-radius:12px;display:inline-block;margin:12px;">

**✨ 极简主义待办事项管理应用 | 支持本地存储和云端同步**

</div>

---

## 📋 项目概述

Uiineed Todo List 是一个现代化的待办事项管理应用，提供两种使用模式：

🏠 **纯前端模式** - 无需安装，数据本地存储，开箱即用
☁️ **云端模式** - 用户认证，数据同步，多设备访问

## 🚀 快速开始

### 📱 纯前端模式 (推荐)

1. **下载项目**
   ```bash
   git clone https://github.com/LuckyQi1996/todolist.git
   cd todolist
   ```

2. **直接使用**
   ```bash
   # 打开英文版
   open index.html

   # 打开中文版
   open index-zh.html
   ```

### ☁️ 云端模式

1. **环境要求**
   - Docker & Docker Compose
   - 微信开放平台账号

2. **启动服务**
   ```bash
   # 配置环境变量
   export WECHAT_OPEN_APP_ID="your_app_id"
   export WECHAT_OPEN_APP_SECRET="your_app_secret"

   # 启动所有服务
   docker-compose up -d
   ```

3. **访问应用**
   - 前端：http://localhost
   - 后端API：http://localhost:8080/api

## 📚 文档导航

| 文档 | 描述 | 适用人群 |
|------|------|----------|
| [📖 ARCHITECTURE.md](./ARCHITECTURE.md) | 完整架构文档 | 开发者、架构师 |
| [🤖 CLAUDE.md](./CLAUDE.md) | AI开发指南 | Claude AI助手 |

## 🛠️ 技术栈

### 前端技术
- **Vue.js 2.x** - 渐进式JavaScript框架
- **SCSS** - CSS预处理器
- **LocalStorage** - 本地数据存储

### 后端技术 (云端模式)
- **Spring Boot** - Java企业级框架
- **MySQL** - 关系型数据库
- **Redis** - 缓存数据库
- **JWT** - 用户认证
- **微信开放平台** - 用户登录

### 部署技术
- **Docker** - 容器化
- **Nginx** - 反向代理
- **Docker Compose** - 服务编排

## 🌟 核心特性

### 📱 待办事项管理
- ✅ 添加、编辑、删除待办事项
- 🏷️ 状态管理（待办、进行中、已完成）
- 🗂️ 分类和优先级设置
- 🔄 拖拽排序
- 📥 数据导入导出

### 🌐 多语言支持
- 🇨🇳 简体中文
- 🇺🇸 English
- 🌐 自动语言检测

### 📱 响应式设计
- 📱 移动端优化
- 💻 桌面端适配
- 🎨 触摸友好的交互

### 🔐 云端功能 (可选)
- 🔐 微信扫码登录
- ☁️ 数据云端同步
- 📱 多设备访问
- 🔄 实时数据更新

## 🎨 设计理念

- **极简主义** - 专注核心功能，去除冗余
- **用户友好** - 直观的界面，流畅的体验
- **性能优先** - 快速响应，低资源占用
- **隐私保护** - 本地存储，数据安全

## 📁 项目结构

```
uiineed-todo-list/
├── 📁 前端应用
│   ├── index.html              # 英文版主页面
│   ├── index-zh.html           # 中文版主页面
│   └── 📁 public/              # 静态资源
├── 📁 后端应用 (可选)
│   └── backend/                # Spring Boot项目
├── 📁 部署配置 (可选)
│   └── docker-compose.yml      # Docker编排
├── 📄 文档
│   ├── README.md               # 项目说明 (本文件)
│   ├── ARCHITECTURE.md         # 架构文档
│   └── CLAUDE.md               # AI开发指南
└── 📄 许可证
    └── LICENSE                 # MIT许可证
```

## 🔧 自定义配置

### 个人信息设置

在HTML文件中取消注释以下代码并替换为您的信息：

```html
<!-- 个人信息区域 -->
<div class="about">
    <img src="public/img/author.jpg" class="author" alt="">
    <h3>您的姓名</h3>
    <p>您的描述</p>

    <!-- 社交媒体链接 -->
    <div class="social">
        <a href="您的GitHub地址" target="_blank">
            <img src="public/img/social/github.svg" alt="GitHub">
        </a>
        <!-- 添加更多社交媒体链接 -->
    </div>
</div>
```

### 语言设置

将中文设为默认首页：

```html
<div class="language switch-language">
    <a href="index.html" target="_self" class="en">En</a>
    <span>/</span>
    <a href="javascript:void(0)" class="zh active">中</a>
</div>
```

## 🌐 在线体验

- **中文版**: [https://ricocc.com/todo/](https://ricocc.com/todo/)
- **英文版**: [https://ricocc.com/todo-en/](https://ricocc.com/todo-en/)

## 🤝 贡献指南

欢迎贡献代码、报告问题或提出建议！

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 开源协议

本项目采用 [MIT License](./LICENSE) 开源协议。

## 💝 支持

如果这个项目对您有帮助，请给个 ⭐ Star 支持一下！

---

<div align="center">

**Made with ❤️ by [Ricocc](https://ricocc.com)**

</div>

---

## 中文介绍

[📍 English Intro Click Here](#english-intro)

Uiineed Todo List 是一个极简主义的待办事项管理应用。在工作时使用 Todo List 是非常常见的，市面上的 Todo List 类产品非常多，无论是电脑端的应用还是手机端的 APP、小程序等，而且也考虑用户的多样化需求，开发了各种功能，还有一些社交属性，有些还免不了广告的存在。

我个人的产品诉求是：基础、干净简洁、不需要额外功能、视觉合格。

所以不如尝试自己写一个 Todo List 应用，既可以满足自己每天不同的想法和需求，也可以自己决定视觉风格，无论多么奇葩，只要自己喜欢就行。

在设计上，我参考了 Figma 社区 aakarshna 的 Noted 设计规范，并根据自己的设计做了调整。在前端上使用了 Vue 2.x 和 Sass，尽量使用 base64 减少文件数量。

### 使用说明

在使用上为了尽量简洁，我使用了 CDN 的形式引入Vue，不需要安装，下载完直接打开 `html` 文件即可使用。

**特色功能：**
- 📝 基础的待办事项管理
- 🎨 简洁优雅的设计
- 📱 完美适配移动端
- 🌐 支持中英文切换
- 💾 本地数据存储
- 📥 数据导入导出功能
- 🔄 拖拽排序
- 🗂️ 分类管理

**线上地址：**
- 中文版：https://ricocc.com/todo/
- 英文版：https://ricocc.com/todo-en/