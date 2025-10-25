-- =====================================================
-- Uiineed Todo List 数据库性能优化脚本
-- =====================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS uiineed_todo
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE uiineed_todo;

-- =====================================================
-- 1. 用户表优化
-- =====================================================

-- 删除原表（如果存在）
DROP TABLE IF EXISTS users;

-- 创建优化后的用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    openid VARCHAR(128) NOT NULL UNIQUE COMMENT '微信OpenID',
    unionid VARCHAR(128) DEFAULT NULL COMMENT '微信UnionID（可选）',
    nickname VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
    avatar_url VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    language VARCHAR(10) DEFAULT 'zh_CN' COMMENT '语言偏好',
    city VARCHAR(50) DEFAULT NULL COMMENT '城市',
    province VARCHAR(50) DEFAULT NULL COMMENT '省份',
    country VARCHAR(50) DEFAULT NULL COMMENT '国家',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    last_login_at TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_openid (openid),
    INDEX idx_unionid (unionid),
    INDEX idx_status (status),
    INDEX idx_last_login (last_login_at),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 2. 待办事项分类表优化
-- =====================================================

-- 删除原表（如果存在）
DROP TABLE IF EXISTS todo_categories;

-- 创建优化后的分类表
CREATE TABLE todo_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    color VARCHAR(7) DEFAULT '#1890ff' COMMENT '分类颜色（十六进制）',
    icon VARCHAR(50) DEFAULT NULL COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认分类：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_sort_order (sort_order),
    UNIQUE KEY uk_user_category_name (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项分类表';

-- =====================================================
-- 3. 待办事项表优化
-- =====================================================

-- 删除原表（如果存在）
DROP TABLE IF EXISTS todos;

-- 创建优化后的待办事项表
CREATE TABLE todos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '待办事项ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    description TEXT DEFAULT NULL COMMENT '描述',
    priority TINYINT DEFAULT 2 COMMENT '优先级：1-低，2-中，3-高，4-紧急',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待办，1-进行中，2-已完成，3-已取消',
    completed_at TIMESTAMP NULL DEFAULT NULL COMMENT '完成时间',
    due_date TIMESTAMP NULL DEFAULT NULL COMMENT '截止日期',
    reminder_time TIMESTAMP NULL DEFAULT NULL COMMENT '提醒时间',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    tags VARCHAR(500) DEFAULT NULL COMMENT '标签（JSON数组格式）',
    attachment_urls TEXT DEFAULT NULL COMMENT '附件URLs（JSON数组格式）',
    estimated_duration INT DEFAULT NULL COMMENT '预估时长（分钟）',
    actual_duration INT DEFAULT NULL COMMENT '实际时长（分钟）',
    completed: BOOLEAN DEFAULT FALSE COMMENT '是否完成（兼容字段）',
    removed: BOOLEAN DEFAULT FALSE COMMENT '是否删除（兼容字段）',
    deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT '删除时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES todo_categories(id) ON DELETE SET NULL,

    -- 主要索引
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_user_status (user_id, status),
    INDEX idx_user_category (user_id, category_id),
    INDEX idx_due_date (due_date),
    INDEX idx_priority (priority),
    INDEX idx_reminder_time (reminder_time),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at),

    -- 复合索引（优化常用查询）
    INDEX idx_user_status_created (user_id, status, created_at DESC),
    INDEX idx_user_priority_status (user_id, priority, status),
    INDEX idx_user_due_status (user_id, due_date, status),
    INDEX idx_user_category_status (user_id, category_id, status),
    INDEX idx_user_reminder_status (user_id, reminder_time, status),

    -- 兼容性索引
    INDEX idx_completed (completed),
    INDEX idx_removed (removed),
    INDEX idx_user_completed (user_id, completed),
    INDEX idx_user_removed (user_id, removed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项表';

-- =====================================================
-- 4. 用户设置表优化
-- =====================================================

-- 删除原表（如果存在）
DROP TABLE IF EXISTS user_settings;

-- 创建优化后的用户设置表
CREATE TABLE user_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设置ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    setting_key VARCHAR(100) NOT NULL COMMENT '设置键',
    setting_value TEXT DEFAULT NULL COMMENT '设置值',
    setting_type VARCHAR(20) DEFAULT 'string' COMMENT '设置类型：string, number, boolean, json',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_setting (user_id, setting_key),
    INDEX idx_user_id (user_id),
    INDEX idx_setting_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置表';

-- =====================================================
-- 5. 操作日志表（新增）
-- =====================================================

CREATE TABLE user_operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    resource_type VARCHAR(50) DEFAULT NULL COMMENT '资源类型',
    resource_id BIGINT DEFAULT NULL COMMENT '资源ID',
    old_value TEXT DEFAULT NULL COMMENT '旧值（JSON格式）',
    new_value TEXT DEFAULT NULL COMMENT '新值（JSON格式）',
    ip_address VARCHAR(45) DEFAULT NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_resource (resource_type, resource_id),
    INDEX idx_created_at (created_at),
    INDEX idx_user_action_time (user_id, action, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户操作日志表';

-- =====================================================
-- 6. 插入默认数据
-- =====================================================

-- 插入默认分类
INSERT INTO todo_categories (user_id, name, color, icon, is_default, sort_order) VALUES
(0, '个人', '#1890ff', 'user', 1, 1),
(0, '工作', '#52c41a', 'work', 1, 2),
(0, '学习', '#faad14', 'book', 1, 3),
(0, '生活', '#f5222d', 'home', 1, 4);

-- =====================================================
-- 7. 创建视图（优化查询）
-- =====================================================

-- 用户待办事项统计视图
CREATE VIEW user_todo_stats AS
SELECT
    user_id,
    COUNT(*) as total_count,
    SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as pending_count,
    SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as in_progress_count,
    SUM(CASE WHEN status = 2 THEN 1 ELSE 0 END) as completed_count,
    SUM(CASE WHEN status = 3 THEN 1 ELSE 0 END) as cancelled_count,
    SUM(CASE WHEN due_date < CURDATE() AND status IN (0,1) THEN 1 ELSE 0 END) as overdue_count
FROM todos
WHERE deleted_at IS NULL
GROUP BY user_id;

-- 今日待办事项视图
CREATE VIEW today_todos AS
SELECT
    t.*,
    c.name as category_name,
    c.color as category_color
FROM todos t
LEFT JOIN todo_categories c ON t.category_id = c.id
WHERE
    t.deleted_at IS NULL
    AND (
        DATE(t.due_date) = CURDATE()
        OR DATE(t.created_at) = CURDATE()
    )
ORDER BY t.priority DESC, t.sort_order ASC;

-- =====================================================
-- 8. 创建存储过程（优化批量操作）
-- =====================================================

-- 批量更新待办事项状态
DELIMITER //
CREATE PROCEDURE batch_update_todo_status(
    IN p_user_id BIGINT,
    IN p_todo_ids TEXT,
    IN p_status TINYINT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE todo_id BIGINT;
    DECLARE todo_cursor CURSOR FOR
        SELECT CAST(id AS UNSIGNED) FROM (
            SELECT TRIM(BOTH ',' FROM SUBSTRING_INDEX(SUBSTRING_INDEX(p_todo_ids, ',', numbers.n), ',', -1)) as id
            FROM (
                SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
                SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
            ) numbers
            WHERE CHAR_LENGTH(p_todo_ids) - CHAR_LENGTH(REPLACE(p_todo_ids, ',', '')) >= numbers.n - 1
        ) AS ids
        WHERE id != '' AND id REGEXP '^[0-9]+$';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN todo_cursor;

    read_loop: LOOP
        FETCH todo_cursor INTO todo_id;
        IF done THEN
            LEAVE read_loop;
        END IF;

        UPDATE todos
        SET status = p_status,
            updated_at = CURRENT_TIMESTAMP,
            completed_at = CASE WHEN p_status = 2 THEN CURRENT_TIMESTAMP ELSE NULL END
        WHERE id = todo_id AND user_id = p_user_id;

    END LOOP;

    CLOSE todo_cursor;
END //
DELIMITER ;

-- 清理过期日志（保留30天）
DELIMITER //
CREATE PROCEDURE cleanup_old_logs()
BEGIN
    DELETE FROM user_operation_logs
    WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

    SELECT ROW_COUNT() as deleted_count;
END //
DELIMITER ;

-- =====================================================
-- 9. 创建触发器（自动维护数据一致性）
-- =====================================================

-- 待办事项状态变更时自动更新完成时间
DELIMITER //
CREATE TRIGGER update_completed_at
    BEFORE UPDATE ON todos
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        IF NEW.status = 2 THEN
            SET NEW.completed_at = CURRENT_TIMESTAMP;
            SET NEW.completed = TRUE;
        ELSE
            SET NEW.completed_at = NULL;
            SET NEW.completed = FALSE;
        END IF;
    END IF;

    IF OLD.removed != NEW.removed THEN
        IF NEW.removed = TRUE THEN
            SET NEW.deleted_at = CURRENT_TIMESTAMP;
            SET NEW.status = 3;
        ELSE
            SET NEW.deleted_at = NULL;
        END IF;
    END IF;
END //
DELIMITER ;

-- 记录待办事项操作日志
DELIMITER //
CREATE TRIGGER log_todo_operations
    AFTER INSERT ON todos
    FOR EACH ROW
BEGIN
    INSERT INTO user_operation_logs (user_id, action, resource_type, resource_id, new_value)
    VALUES (NEW.user_id, 'CREATE', 'todo', NEW.id, JSON_OBJECT(
        'title', NEW.title,
        'description', NEW.description,
        'priority', NEW.priority,
        'status', NEW.status
    ));
END //
DELIMITER ;

-- =====================================================
-- 10. 性能监控查询
-- =====================================================

-- 查看表大小和索引使用情况
SELECT
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)',
    table_rows,
    index_length,
    data_length
FROM information_schema.tables
WHERE table_schema = 'uiineed_todo'
ORDER BY (data_length + index_length) DESC;

-- 查看慢查询日志状态
SHOW VARIABLES LIKE 'slow_query_log%';

-- 查看索引使用情况
SELECT
    table_name,
    index_name,
    cardinality,
    sub_part,
    packed,
    nullable,
    index_type
FROM information_schema.statistics
WHERE table_schema = 'uiineed_todo'
ORDER BY table_name, seq_in_index;

-- =====================================================
-- 11. 数据库优化建议
-- =====================================================

-- 1. 定期优化表
-- OPTIMIZE TABLE users, todos, todo_categories, user_settings, user_operation_logs;

-- 2. 分析表统计信息
-- ANALYZE TABLE users, todos, todo_categories, user_settings, user_operation_logs;

-- 3. 检查表状态
-- CHECK TABLE users, todos, todo_categories, user_settings, user_operation_logs;

-- =====================================================
-- 完成！
-- =====================================================