package com.ankai.service;

import com.ankai.dto.PermissionTreeNode;
import com.ankai.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author AnKai
 * @since 2024-01-01
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 获取权限树
     *
     * @return 权限树
     */
    List<PermissionTreeNode> getPermissionTree();

    /**
     * 根据ID查询权限
     *
     * @param id 权限ID
     * @return 权限信息
     */
    Permission getPermissionById(Long id);

    /**
     * 新增权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean createPermission(Permission permission);

    /**
     * 更新权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean updatePermission(Permission permission);

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    boolean deletePermission(Long id);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);

    /**
     * 查询所有权限
     *
     * @return 权限列表
     */
    List<Permission> getAllPermissions();
}

