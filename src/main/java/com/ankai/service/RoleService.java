package com.ankai.service;

import com.ankai.entity.Role;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author AnKai
 * @since 2024-01-01
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     *
     * @param current  当前页
     * @param size     每页大小
     * @param roleName 角色名称
     * @param roleCode 角色编码
     * @param status   状态
     * @return 分页结果
     */
    IPage<Role> getRolePage(Long current, Long size, String roleName, String roleCode, Integer status);

    /**
     * 根据ID查询角色
     *
     * @param id 角色ID
     * @return 角色信息
     */
    Role getRoleById(Long id);

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Role getRoleByCode(String roleCode);

    /**
     * 新增角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean createRole(Role role);

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long id);

    /**
     * 批量删除角色
     *
     * @param ids 角色ID列表
     * @return 是否成功
     */
    boolean batchDeleteRole(List<Long> ids);

    /**
     * 为角色分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色的权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<Role> getAllRoles();
}

