-- ==================== AnKai RBAC权限系统初始化脚本 ====================
-- ==================== AnKai RBAC权限系统初始化脚本 ====================
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `ankai_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ankai_db`;

-- ==================== 用户相关表 ====================

-- 创建用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '0' COMMENT '性别 (0-未知, 1-男, 2-女)',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `department_id` bigint DEFAULT NULL COMMENT '部门ID',
  `position` varchar(50) DEFAULT NULL COMMENT '职位',
  `status` tinyint DEFAULT '1' COMMENT '用户状态 (0-禁用, 1-启用)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `login_count` int DEFAULT '0' COMMENT '登录次数',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识 (0-未删除, 1-已删除)',
  `version` int DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username_deleted` (`username`, `deleted`),
  UNIQUE KEY `uk_email_deleted` (`email`, `deleted`),
  KEY `idx_status` (`status`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `data_scope` tinyint DEFAULT '1' COMMENT '数据权限范围 (1-全部, 2-本部门及下级, 3-本部门, 4-仅本人, 5-自定义)',
  `status` tinyint DEFAULT '1' COMMENT '状态 (0-禁用, 1-启用)',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识 (0-未删除, 1-已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 创建权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父权限ID',
  `permission_name` varchar(50) NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `permission_type` tinyint NOT NULL COMMENT '权限类型 (1-菜单, 2-按钮, 3-接口)',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态 (0-禁用, 1-启用)',
  `visible` tinyint DEFAULT '1' COMMENT '是否显示 (0-隐藏, 1-显示)',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识 (0-未删除, 1-已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_permission_type` (`permission_type`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 创建用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 创建角色权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 创建部门表
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门ID',
  `department_name` varchar(50) NOT NULL COMMENT '部门名称',
  `department_code` varchar(50) NOT NULL COMMENT '部门编码',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态 (0-禁用, 1-启用)',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识 (0-未删除, 1-已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_department_code` (`department_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- ==================== 初始化数据 ====================

-- 插入部门数据
INSERT INTO `sys_department` (`id`, `parent_id`, `department_name`, `department_code`, `leader`, `phone`, `email`, `sort_order`, `status`) VALUES
(1, 0, 'AnKai科技', 'ANKAI', 'AnKai', '400-888-8888', 'contact@ankai.com', 1, 1),
(2, 1, '技术部', 'TECH', '技术总监', '400-888-8801', 'tech@ankai.com', 1, 1),
(3, 1, '产品部', 'PRODUCT', '产品总监', '400-888-8802', 'product@ankai.com', 2, 1),
(4, 1, '运营部', 'OPERATION', '运营总监', '400-888-8803', 'operation@ankai.com', 3, 1),
(5, 2, '前端开发组', 'FRONTEND', '前端组长', '400-888-8811', 'frontend@ankai.com', 1, 1),
(6, 2, '后端开发组', 'BACKEND', '后端组长', '400-888-8812', 'backend@ankai.com', 2, 1),
(7, 2, '测试组', 'TEST', '测试组长', '400-888-8813', 'test@ankai.com', 3, 1);

-- 插入角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`, `sort_order`, `data_scope`, `status`) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, 1, 1),
(2, '系统管理员', 'ADMIN', '系统管理员，拥有大部分权限', 2, 2, 1),
(3, '部门管理员', 'DEPT_ADMIN', '部门管理员，管理本部门用户', 3, 3, 1),
(4, '普通用户', 'USER', '普通用户，基础权限', 4, 4, 1),
(5, '访客', 'GUEST', '访客用户，只读权限', 5, 4, 1);

-- 插入权限数据（菜单权限）
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `path`, `component`, `icon`, `sort_order`, `status`, `visible`) VALUES
-- 一级菜单
(1, 0, '首页', 'dashboard', 1, '/dashboard', 'Dashboard', 'HomeOutlined', 1, 1, 1),
(2, 0, '系统管理', 'system', 1, '/system', 'Layout', 'SettingOutlined', 2, 1, 1),
(3, 0, '用户管理', 'user-management', 1, '/user', 'Layout', 'UserOutlined', 3, 1, 1),
(4, 0, '监控中心', 'monitor', 1, '/monitor', 'Layout', 'MonitorOutlined', 4, 1, 1),

-- 系统管理子菜单
(10, 2, '用户管理', 'system:user', 1, '/system/user', 'system/User', 'UserOutlined', 1, 1, 1),
(11, 2, '角色管理', 'system:role', 1, '/system/role', 'system/Role', 'TeamOutlined', 2, 1, 1),
(12, 2, '权限管理', 'system:permission', 1, '/system/permission', 'system/Permission', 'SafetyOutlined', 3, 1, 1),
(13, 2, '部门管理', 'system:dept', 1, '/system/dept', 'system/Department', 'ApartmentOutlined', 4, 1, 1),
(14, 2, '菜单管理', 'system:menu', 1, '/system/menu', 'system/Menu', 'MenuOutlined', 5, 1, 1),

-- 监控中心子菜单
(20, 4, '在线用户', 'monitor:online', 1, '/monitor/online', 'monitor/Online', 'UserSwitchOutlined', 1, 1, 1),
(21, 4, '系统日志', 'monitor:log', 1, '/monitor/log', 'monitor/Log', 'FileTextOutlined', 2, 1, 1),
(22, 4, '服务监控', 'monitor:server', 1, '/monitor/server', 'monitor/Server', 'CloudServerOutlined', 3, 1, 1);

-- 插入按钮权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `sort_order`, `status`, `visible`) VALUES
-- 用户管理按钮权限
(100, 10, '用户查询', 'system:user:query', 2, 1, 1, 0),
(101, 10, '用户新增', 'system:user:add', 2, 2, 1, 0),
(102, 10, '用户修改', 'system:user:edit', 2, 3, 1, 0),
(103, 10, '用户删除', 'system:user:delete', 2, 4, 1, 0),
(104, 10, '用户导出', 'system:user:export', 2, 5, 1, 0),
(105, 10, '用户导入', 'system:user:import', 2, 6, 1, 0),
(106, 10, '重置密码', 'system:user:resetPwd', 2, 7, 1, 0),

-- 角色管理按钮权限
(110, 11, '角色查询', 'system:role:query', 2, 1, 1, 0),
(111, 11, '角色新增', 'system:role:add', 2, 2, 1, 0),
(112, 11, '角色修改', 'system:role:edit', 2, 3, 1, 0),
(113, 11, '角色删除', 'system:role:delete', 2, 4, 1, 0),
(114, 11, '分配权限', 'system:role:permission', 2, 5, 1, 0),

-- 权限管理按钮权限
(120, 12, '权限查询', 'system:permission:query', 2, 1, 1, 0),
(121, 12, '权限新增', 'system:permission:add', 2, 2, 1, 0),
(122, 12, '权限修改', 'system:permission:edit', 2, 3, 1, 0),
(123, 12, '权限删除', 'system:permission:delete', 2, 4, 1, 0),

-- 部门管理按钮权限
(130, 13, '部门查询', 'system:dept:query', 2, 1, 1, 0),
(131, 13, '部门新增', 'system:dept:add', 2, 2, 1, 0),
(132, 13, '部门修改', 'system:dept:edit', 2, 3, 1, 0),
(133, 13, '部门删除', 'system:dept:delete', 2, 4, 1, 0);

-- 插入用户数据（密码：admin=Ankai2025, 其他=123456）
INSERT INTO `sys_user` (`id`, `username`, `password`, `email`, `phone`, `real_name`, `gender`, `department_id`, `position`, `status`, `remark`) VALUES
(1, 'admin', '$2a$10$8.UnVuG9HHPz/VuofMuV2OX1/QZbKyf5rV9dLrDXXMf9BqZGOqTzu', 'admin@ankai.com', '13800138000', '系统管理员', 1, 1, 'CEO', 1, '系统超级管理员账号'),
(2, 'zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXISwKr9fF8bNZnlOWgLqbr7Ipe', 'zhangsan@ankai.com', '13800138001', '张三', 1, 5, '前端工程师', 1, '前端开发工程师'),
(3, 'lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXISwKr9fF8bNZnlOWgLqbr7Ipe', 'lisi@ankai.com', '13800138002', '李四', 2, 6, 'Java工程师', 1, '后端开发工程师'),
(4, 'wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXISwKr9fF8bNZnlOWgLqbr7Ipe', 'wangwu@ankai.com', '13800138003', '王五', 1, 7, '测试工程师', 1, '软件测试工程师'),
(5, 'zhaoliu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXISwKr9fF8bNZnlOWgLqbr7Ipe', 'zhaoliu@ankai.com', '13800138004', '赵六', 2, 3, '产品经理', 1, '产品经理'),
(6, 'guest', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXISwKr9fF8bNZnlOWgLqbr7Ipe', 'guest@ankai.com', '13800138005', '访客用户', 0, 1, '访客', 1, '访客测试账号');

-- 插入用户角色关联数据
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES
(1, 1, 1), -- admin用户分配超级管理员角色
(2, 2, 4), -- zhangsan用户分配普通用户角色
(3, 3, 4), -- lisi用户分配普通用户角色
(4, 4, 4), -- wangwu用户分配普通用户角色
(5, 5, 3), -- zhaoliu用户分配部门管理员角色
(6, 6, 5); -- guest用户分配访客角色

-- 插入角色权限关联数据
-- 超级管理员拥有所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- 超级管理员(1) - 所有菜单权限
(1, 1), (1, 2), (1, 3), (1, 4),
(1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(1, 20), (1, 21), (1, 22),
-- 超级管理员(1) - 所有按钮权限
(1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105), (1, 106),
(1, 110), (1, 111), (1, 112), (1, 113), (1, 114),
(1, 120), (1, 121), (1, 122), (1, 123),
(1, 130), (1, 131), (1, 132), (1, 133);

-- 系统管理员(2) - 大部分权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(2, 1), (2, 2), (2, 3), (2, 4),
(2, 10), (2, 11), (2, 12), (2, 13),
(2, 20), (2, 21), (2, 22),
(2, 100), (2, 101), (2, 102), (2, 104), (2, 105), (2, 106),
(2, 110), (2, 111), (2, 112), (2, 114),
(2, 120), (2, 121), (2, 122),
(2, 130), (2, 131), (2, 132);

-- 部门管理员(3) - 部门相关权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(3, 1), (3, 3),
(3, 10), (3, 13),
(3, 100), (3, 101), (3, 102), (3, 106),
(3, 130), (3, 131), (3, 132);

-- 普通用户(4) - 基础权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(4, 1), (4, 3),
(4, 100);

-- 访客(5) - 只读权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(5, 1),
(5, 100);

-- ==================== 初始化完成 ====================

/*
测试账号信息：
1. 超级管理员
   用户名：admin
   密码：Ankai2025
   权限：所有权限

2. 普通用户
   用户名：zhangsan, lisi, wangwu
   密码：123456
   权限：基础权限

3. 部门管理员
   用户名：zhaoliu
   密码：123456
   权限：部门管理权限

4. 访客用户
   用户名：guest
   密码：123456
   权限：只读权限

权限说明：
- permission_type: 1=菜单, 2=按钮, 3=接口
- data_scope: 1=全部, 2=本部门及下级, 3=本部门, 4=仅本人, 5=自定义
- status: 0=禁用, 1=启用
- visible: 0=隐藏, 1=显示
- deleted: 0=未删除, 1=已删除

使用说明：
1. 执行此SQL文件创建数据库和表结构
2. 插入初始数据包括用户、角色、权限、部门等
3. 可以使用admin/Ankai2025登录系统进行测试
4. 根据实际需求调整权限配置
*/

-- ==================== 站内信/聊天消息表 ====================
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `sender_name` varchar(50) NOT NULL COMMENT '发送者用户名',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `receiver_name` varchar(50) NOT NULL COMMENT '接收者用户名',
  `title` varchar(200) DEFAULT NULL COMMENT '消息标题（聊天模式可为空）',
  `content` text NOT NULL COMMENT '消息内容',
  `message_type` tinyint DEFAULT '2' COMMENT '消息类型：1-系统通知 2-私信/聊天 3-公告',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读：0-未读 1-已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识 (0-未删除, 1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_sender_receiver` (`sender_id`, `receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信/聊天消息表';

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
  `color` varchar(20) DEFAULT '#1890ff' COMMENT '日历显示颜色',
  `todo_type` tinyint DEFAULT '1' COMMENT '待办类型：1-个人添加 2-系统分配',
  `source_type` varchar(50) DEFAULT NULL COMMENT '来源类型（如：project_task, approval）',
  `source_id` bigint DEFAULT NULL COMMENT '来源ID',
  `remind_time` datetime DEFAULT NULL COMMENT '提醒时间',
  `is_reminded` tinyint DEFAULT '0' COMMENT '是否已提醒：0-否 1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识 (0-未删除, 1-已删除)',
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
  `top_order` int DEFAULT '0' COMMENT '置顶排序（数值越大越靠前）',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `create_by` bigint NOT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除标识 (0-未删除, 1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_announcement_type` (`announcement_type`),
  KEY `idx_is_top` (`is_top`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统公告表';

-- ==================== 公告用户关联表（用于指定用户发布） ====================
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
-- 插入公告管理权限
INSERT INTO `sys_permission` (`id`, `parent_id`, `permission_name`, `permission_code`, `permission_type`, `sort_order`, `status`, `visible`) VALUES
(15, 1, '公告管理', 'system:announcement', 1, 5, 1, 1),
(150, 15, '公告查询', 'system:announcement:query', 2, 1, 1, 0),
(151, 15, '公告新增', 'system:announcement:add', 2, 2, 1, 0),
(152, 15, '公告修改', 'system:announcement:edit', 2, 3, 1, 0),
(153, 15, '公告删除', 'system:announcement:delete', 2, 4, 1, 0),
(154, 15, '公告发布', 'system:announcement:publish', 2, 5, 1, 0);

-- 给超级管理员和系统管理员添加公告权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(1, 15), (1, 150), (1, 151), (1, 152), (1, 153), (1, 154),
(2, 15), (2, 150), (2, 151), (2, 152), (2, 153), (2, 154);
