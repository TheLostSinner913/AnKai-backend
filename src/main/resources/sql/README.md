# AnKai RBAC权限系统数据库初始化

## 📋 概述

本目录包含AnKai后台管理系统的完整RBAC权限系统数据库初始化脚本。

## 🗄️ 数据库结构

### 核心表结构

1. **用户相关表**
   - `sys_user` - 用户表
   - `sys_user_role` - 用户角色关联表

2. **权限相关表**
   - `sys_role` - 角色表
   - `sys_permission` - 权限表
   - `sys_role_permission` - 角色权限关联表

3. **组织架构表**
   - `sys_department` - 部门表

## 🚀 快速开始

### 1. 执行初始化脚本

```bash
# 方法1：使用MySQL命令行
mysql -u root -p < init.sql

# 方法2：使用MySQL Workbench
# 打开init.sql文件并执行

# 方法3：使用Navicat等工具
# 导入并执行init.sql文件
```

### 2. 验证安装

执行以下SQL验证数据是否正确插入：

```sql
-- 检查用户数据
SELECT id, username, real_name, status FROM sys_user;

-- 检查角色数据
SELECT id, role_name, role_code, status FROM sys_role;

-- 检查权限数据
SELECT id, permission_name, permission_code, permission_type FROM sys_permission LIMIT 10;

-- 检查用户角色关联
SELECT u.username, r.role_name 
FROM sys_user u 
JOIN sys_user_role ur ON u.id = ur.user_id 
JOIN sys_role r ON ur.role_id = r.id;
```

## 👥 测试账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | Ankai2025 | 超级管理员 | 拥有所有权限 |
| zhangsan | 123456 | 普通用户 | 基础权限 |
| lisi | 123456 | 普通用户 | 基础权限 |
| wangwu | 123456 | 普通用户 | 基础权限 |
| zhaoliu | 123456 | 部门管理员 | 部门管理权限 |
| guest | 123456 | 访客 | 只读权限 |

## 🔐 权限说明

### 权限类型

- **1** - 菜单权限：控制页面访问
- **2** - 按钮权限：控制操作按钮显示
- **3** - 接口权限：控制API访问

### 数据权限范围

- **1** - 全部数据
- **2** - 本部门及下级部门数据
- **3** - 本部门数据
- **4** - 仅本人数据
- **5** - 自定义数据权限

### 状态说明

- **status**: 0=禁用, 1=启用
- **visible**: 0=隐藏, 1=显示
- **deleted**: 0=未删除, 1=已删除

## 🏢 组织架构

```
AnKai科技
├── 技术部
│   ├── 前端开发组
│   ├── 后端开发组
│   └── 测试组
├── 产品部
└── 运营部
```

## 📝 权限菜单结构

```
首页 (dashboard)
系统管理 (system)
├── 用户管理 (system:user)
├── 角色管理 (system:role)
├── 权限管理 (system:permission)
├── 部门管理 (system:dept)
└── 菜单管理 (system:menu)
用户管理 (user-management)
监控中心 (monitor)
├── 在线用户 (monitor:online)
├── 系统日志 (monitor:log)
└── 服务监控 (monitor:server)
```

## 🔧 自定义配置

### 添加新用户

```sql
-- 1. 插入用户
INSERT INTO sys_user (username, password, email, real_name, department_id, status) 
VALUES ('newuser', '$2a$10$...', 'newuser@ankai.com', '新用户', 1, 1);

-- 2. 分配角色
INSERT INTO sys_user_role (user_id, role_id) 
VALUES (LAST_INSERT_ID(), 4); -- 4=普通用户角色
```

### 添加新权限

```sql
-- 1. 插入权限
INSERT INTO sys_permission (parent_id, permission_name, permission_code, permission_type, path, component, icon, sort_order) 
VALUES (0, '新模块', 'new:module', 1, '/new', 'NewModule', 'AppstoreOutlined', 10);

-- 2. 分配给角色
INSERT INTO sys_role_permission (role_id, permission_id) 
VALUES (1, LAST_INSERT_ID()); -- 1=超级管理员角色
```

## ⚠️ 注意事项

1. **密码安全**: 生产环境请修改默认密码
2. **权限配置**: 根据实际业务需求调整权限配置
3. **数据备份**: 执行前请备份现有数据
4. **环境配置**: 确保数据库连接配置正确

## 🔄 更新日志

- **v1.0** - 初始版本，包含基础RBAC权限系统
- 支持用户、角色、权限、部门管理
- 提供完整的测试数据

## 📞 技术支持

如有问题，请联系技术支持团队。
