package com.ankai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ankai.entity.Role;
import com.ankai.entity.RolePermission;
import com.ankai.exception.BusinessException;
import com.ankai.mapper.PermissionMapper;
import com.ankai.mapper.RoleMapper;
import com.ankai.mapper.RolePermissionMapper;
import com.ankai.service.RoleService;
import com.ankai.utils.LogUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private static final Logger logger = LogUtil.getLogger(RoleServiceImpl.class);

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public IPage<Role> getRolePage(Long current, Long size, String roleName, String roleCode, Integer status) {
        Page<Role> page = new Page<>(current, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        if (StrUtil.isNotBlank(roleCode)) {
            wrapper.like(Role::getRoleCode, roleCode);
        }
        if (status != null) {
            wrapper.eq(Role::getStatus, status);
        }

        wrapper.orderByAsc(Role::getSortOrder);
        return roleMapper.selectPage(page, wrapper);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role getRoleByCode(String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        return roleMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(Role role) {
        // 检查角色编码是否已存在
        Role existRole = getRoleByCode(role.getRoleCode());
        if (existRole != null) {
            throw BusinessException.of(400, "角色编码已存在");
        }

        LogUtil.info(logger, "创建角色: {}", role.getRoleName());
        return roleMapper.insert(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        // 检查角色是否存在
        Role existRole = getRoleById(role.getId());
        if (existRole == null) {
            throw BusinessException.of(404, "角色不存在");
        }

        // 如果修改了角色编码，检查新编码是否已被使用
        if (!existRole.getRoleCode().equals(role.getRoleCode())) {
            Role codeExistRole = getRoleByCode(role.getRoleCode());
            if (codeExistRole != null && !codeExistRole.getId().equals(role.getId())) {
                throw BusinessException.of(400, "角色编码已存在");
            }
        }

        LogUtil.info(logger, "更新角色: {}", role.getRoleName());
        return roleMapper.updateById(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        // 检查角色是否存在
        Role role = getRoleById(id);
        if (role == null) {
            throw BusinessException.of(404, "角色不存在");
        }

        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(id);

        LogUtil.info(logger, "删除角色: {}", role.getRoleName());
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteRole(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 删除角色权限关联
        for (Long id : ids) {
            rolePermissionMapper.deleteByRoleId(id);
        }

        LogUtil.info(logger, "批量删除角色，数量: {}", ids.size());
        return roleMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 检查角色是否存在
        Role role = getRoleById(roleId);
        if (role == null) {
            throw BusinessException.of(404, "角色不存在");
        }

        // 删除原有的角色权限关联
        rolePermissionMapper.deleteByRoleId(roleId);

        // 如果权限列表为空，则只删除不添加
        if (permissionIds == null || permissionIds.isEmpty()) {
            LogUtil.info(logger, "清空角色 {} 的所有权限", role.getRoleName());
            return true;
        }

        // 批量插入新的角色权限关联
        for (Long permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermission.setCreateTime(LocalDateTime.now());
            rolePermissionMapper.insert(rolePermission);
        }

        LogUtil.info(logger, "为角色 {} 分配 {} 个权限", role.getRoleName(), permissionIds.size());
        return true;
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        return rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> getAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1);
        wrapper.orderByAsc(Role::getSortOrder);
        return roleMapper.selectList(wrapper);
    }
}

