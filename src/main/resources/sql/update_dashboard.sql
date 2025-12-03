-- ==================== 首页功能更新脚本 ====================
-- 执行此脚本以添加待办事项、公告等首页功能所需的表和权限

-- ==================== 待办事项表 ====================
DROP TABLE IF EXISTS `sys_todo`;
CREATE TABLE `sys_todo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '待办ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `title` varchar(200) NOT NULL COMMENT '待办标题',
  `description` text COMMENT '待办描述',
  `todo_date` date NOT NULL COMMENT '待办日期',
  `start_time` time DEFAULT NULL COMMENT '开始时间',
  `end_time` time DEFAULT NULL COMMENT '结束时间',
  `priority` tinyint DEFAULT '2' COMMENT '优先级：1-低 2-中 3-高',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-待办 1-进行中 2-已完成 3-已取消',
  `color` varchar(20) DEFAULT '#1890ff' COMMENT '颜色标记',
  `todo_type` tinyint DEFAULT '1' COMMENT '待办类型：1-个人添加 2-系统分配',
  `source_type` varchar(50) DEFAULT NULL COMMENT '来源类型（如：project_task, approval等）',
  `source_id` bigint DEFAULT NULL COMMENT '来源ID',
  `remind_time` datetime DEFAULT NULL COMMENT '提醒时间',
  `is_reminded` tinyint DEFAULT '0' COMMENT '是否已提醒：0-否 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_todo_date` (`todo_date`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_user_date` (`user_id`, `todo_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='待办事项表';

-- ==================== 系统公告表 ====================
DROP TABLE IF EXISTS `sys_announcement`;
CREATE TABLE `sys_announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `announcement_type` tinyint DEFAULT '1' COMMENT '公告类型：1-普通 2-重要 3-紧急',
  `target_type` tinyint DEFAULT '1' COMMENT '发布范围：1-全员 2-指定用户 3-指定角色',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-草稿 1-已发布 2-已撤回',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶：0-否 1-是',
  `top_order` int DEFAULT '0' COMMENT '置顶排序（数字越大越靠前）',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除：0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_announcement_type` (`announcement_type`),
  KEY `idx_is_top` (`is_top`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告表';

-- ==================== 公告用户关联表 ====================
DROP TABLE IF EXISTS `sys_announcement_user`;
CREATE TABLE `sys_announcement_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `announcement_id` bigint NOT NULL COMMENT '公告ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读：0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_announcement_user` (`announcement_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告用户关联表';

-- ==================== 公告权限数据 ====================
-- 插入公告管理权限（如果不存在）
INSERT IGNORE INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `sort_order`, `status`, `visible`) VALUES
(15, 1, '公告管理', 'system:announcement', 1, 5, 1, 1),
(150, 15, '公告查询', 'system:announcement:query', 2, 1, 1, 0),
(151, 15, '公告新增', 'system:announcement:add', 2, 2, 1, 0),
(152, 15, '公告修改', 'system:announcement:edit', 2, 3, 1, 0),
(153, 15, '公告删除', 'system:announcement:delete', 2, 4, 1, 0),
(154, 15, '公告发布', 'system:announcement:publish', 2, 5, 1, 0);

-- 给超级管理员和系统管理员添加公告权限（如果不存在）
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 15), (1, 150), (1, 151), (1, 152), (1, 153), (1, 154),
(2, 15), (2, 150), (2, 151), (2, 152), (2, 153), (2, 154);

-- ==================== 修改用户表唯一索引（支持逻辑删除） ====================
-- 如果之前没有执行过，请执行以下语句
-- ALTER TABLE sys_user DROP INDEX uk_username;
-- ALTER TABLE sys_user DROP INDEX uk_email;
-- ALTER TABLE sys_user ADD UNIQUE INDEX uk_username_deleted (username, deleted);
-- ALTER TABLE sys_user ADD UNIQUE INDEX uk_email_deleted (email, deleted);

