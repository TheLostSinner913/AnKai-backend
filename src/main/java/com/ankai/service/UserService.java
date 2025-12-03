package com.ankai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ankai.common.PageRequest;
import com.ankai.dto.UserWithRolesDTO;
import com.ankai.entity.User;

import java.util.List;

/**
 * 用户Service接口
 *
 * @author AnKai
 * @since 2024-01-01
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询
     *
     * @param pageRequest 分页请求参数
     * @return 分页结果
     */
    Page<User> page(PageRequest pageRequest);

    /**
     * 根据ID查询用户（不包含密码）
     *
     * @param id 用户ID
     * @return 用户信息（密码字段为null）
     */
    User getById(Long id);

    /**
     * 根据ID查询用户（包含密码）- 仅供内部使用
     *
     * @param id 用户ID
     * @return 完整的用户信息（包含密码）
     */
    User getByIdWithPassword(Long id);

    /**
     * 为用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 分页查询用户（包含角色信息）
     *
     * @param pageRequest 分页请求参数
     * @return 分页结果（包含角色信息）
     */
    Page<UserWithRolesDTO> pageWithRoles(PageRequest pageRequest);
}
