-- Uiineed Todo List 数据库设计
-- 创建数据库
CREATE DATABASE IF NOT EXISTS uiineed_todo CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE uiineed_todo;

-- 用户表
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `wechat_openid` varchar(100) NOT NULL COMMENT '微信OpenID',
  `wechat_unionid` varchar(100) DEFAULT NULL COMMENT '微信UnionID',
  `nickname` varchar(50) DEFAULT NULL COMMENT '微信昵称',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `country` varchar(50) DEFAULT NULL COMMENT '国家',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `language` varchar(20) DEFAULT 'zh_CN' COMMENT '语言偏好',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `login_count` int DEFAULT '0' COMMENT '登录次数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wechat_openid` (`wechat_openid`),
  KEY `idx_wechat_unionid` (`wechat_unionid`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 待办事项分类表
CREATE TABLE `todo_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `color` varchar(7) DEFAULT '#33322E' COMMENT '分类颜色（十六进制）',
  `icon` varchar(20) DEFAULT 'folder' COMMENT '分类图标',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_sort_order` (`sort_order`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项分类表';

-- 待办事项主表
CREATE TABLE `todos` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '待办事项ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `title` varchar(500) NOT NULL COMMENT '待办事项标题',
  `description` text COMMENT '详细描述',
  `priority` tinyint DEFAULT '1' COMMENT '优先级：1-低，2-中，3-高',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待办，1-进行中，2-已完成，3-已取消',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `due_date` datetime DEFAULT NULL COMMENT '截止日期',
  `reminder_time` datetime DEFAULT NULL COMMENT '提醒时间',
  `sort_order` int DEFAULT '0' COMMENT '排序序号',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  `deleted_at` datetime DEFAULT NULL COMMENT '删除时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_user_status` (`user_id`, `status`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`category_id`) REFERENCES `todo_categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项表';

-- 用户设置表
CREATE TABLE `user_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `setting_key` varchar(50) NOT NULL COMMENT '设置键',
  `setting_value` text COMMENT '设置值',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_setting` (`user_id`, `setting_key`),
  KEY `idx_user_id` (`user_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表';

-- 用户会话表（JWT黑名单等）
CREATE TABLE `user_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `token` varchar(500) DEFAULT NULL COMMENT 'JWT Token',
  `refresh_token` varchar(500) DEFAULT NULL COMMENT '刷新Token',
  `device_info` varchar(200) DEFAULT NULL COMMENT '设备信息',
  `ip_address` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `expires_at` datetime DEFAULT NULL COMMENT '过期时间',
  `is_active` tinyint DEFAULT '1' COMMENT '是否活跃：0-失效，1-活跃',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_token` (`token`),
  KEY `idx_expires_at` (`expires_at`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';

-- 数据同步记录表（用于多设备同步）
CREATE TABLE `sync_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '同步记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `entity_type` varchar(20) NOT NULL COMMENT '实体类型：todo, category',
  `entity_id` bigint NOT NULL COMMENT '实体ID',
  `action_type` varchar(20) NOT NULL COMMENT '操作类型：create, update, delete',
  `sync_data` json COMMENT '同步数据',
  `device_id` varchar(100) DEFAULT NULL COMMENT '设备ID',
  `sync_timestamp` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间戳',
  `is_synced` tinyint DEFAULT '0' COMMENT '是否已同步：0-未同步，1-已同步',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_entity` (`entity_type`, `entity_id`),
  KEY `idx_sync_timestamp` (`sync_timestamp`),
  KEY `idx_is_synced` (`is_synced`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据同步记录表';

-- 初始化默认分类数据
INSERT INTO `todo_categories` (`user_id`, `name`, `color`, `icon`, `sort_order`) VALUES
(0, '个人', '#33322E', 'user', 1),
(0, '工作', '#8CD4CB', 'briefcase', 2),
(0, '学习', '#f8d966', 'book', 3),
(0, '生活', '#F6A89E', 'home', 4);

-- 初始化用户设置默认值
INSERT INTO `user_settings` (`user_id`, `setting_key`, `setting_value`) VALUES
(0, 'language', 'zh_CN'),
(0, 'theme', 'light'),
(0, 'default_category_id', '1'),
(0, 'auto_sync', 'true'),
(0, 'reminder_enabled', 'true'),
(0, 'slogan', 'Act Now, Simplify Life.☕');