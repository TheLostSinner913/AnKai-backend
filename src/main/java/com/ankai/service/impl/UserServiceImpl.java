package com.ankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ankai.common.PageRequest;
import com.ankai.dto.UserWithRolesDTO;
import com.ankai.entity.Role;
import com.ankai.entity.User;
import com.ankai.entity.UserRole;
import com.ankai.exception.BusinessException;
import com.ankai.mapper.RoleMapper;
import com.ankai.mapper.UserMapper;
import com.ankai.mapper.UserRoleMapper;
import com.ankai.service.UserService;
import com.ankai.utils.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户Service实现类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LogUtil.getLogger(UserServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Page<User> page(PageRequest pageRequest) {
        // 创建分页对象
        Page<User> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());

        // 执行分页查询
        return this.page(page);
    }

    @Override
    public boolean save(User entity) {
        LogUtil.info(logger, "新增用户: {}", entity.getUsername());

        // 1. 检查用户名是否已存在
        if (this.lambdaQuery().eq(User::getUsername, entity.getUsername()).exists()) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 检查邮箱是否已存在（如果提供了邮箱）
        if (StringUtils.hasText(entity.getEmail())) {
            if (this.lambdaQuery().eq(User::getEmail, entity.getEmail()).exists()) {
                throw new RuntimeException("邮箱已存在");
            }
        }

        // 3. 密码加密
        if (StringUtils.hasText(entity.getPassword())) {
            String encodedPassword = passwordEncoder.encode(entity.getPassword());
            entity.setPassword(encodedPassword);
            LogUtil.info(logger, "用户 {} 密码已加密", entity.getUsername());
        } else {
            throw new RuntimeException("密码不能为空");
        }

        // 4. 设置默认状态
        if (entity.getStatus() == null) {
            entity.setStatus(1); // 默认启用
        }

        boolean result = super.save(entity);
        LogUtil.info(logger, "用户 {} 新增{}", entity.getUsername(), result ? "成功" : "失败");
        return result;
    }

    @Override
    public boolean updateById(User entity) {
        LogUtil.info(logger, "更新用户: {}", entity.getUsername());

        // 1. 检查用户是否存在
        User existingUser = this.getById(entity.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 检查邮箱是否已被其他用户使用（如果修改了邮箱）
        if (StringUtils.hasText(entity.getEmail()) && !entity.getEmail().equals(existingUser.getEmail())) {
            if (this.lambdaQuery()
                    .eq(User::getEmail, entity.getEmail())
                    .ne(User::getId, entity.getId())
                    .exists()) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
        }

        // 3. 密码处理
        if (StringUtils.hasText(entity.getPassword())) {
            // 如果提供了新密码，则加密
            String encodedPassword = passwordEncoder.encode(entity.getPassword());
            entity.setPassword(encodedPassword);
            LogUtil.info(logger, "用户 {} 密码已更新并加密", entity.getUsername());
        } else {
            // 如果没有提供密码，保持原密码不变
            entity.setPassword(existingUser.getPassword());
            LogUtil.info(logger, "用户 {} 密码保持不变", entity.getUsername());
        }

        boolean result = super.updateById(entity);
        LogUtil.info(logger, "用户 {} 更新{}", entity.getUsername(), result ? "成功" : "失败");
        return result;
    }

    @Override
    public User getById(Long id) {
        User user = super.getById(id);
        if (user != null) {
            // 查询时不返回密码字段（安全考虑）
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public User getByIdWithPassword(Long id) {
        // 特殊方法：返回包含密码的用户信息（仅供内部使用）
        return super.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 检查用户是否存在
        User user = super.getById(userId);
        if (user == null) {
            throw BusinessException.of(404, "用户不存在");
        }

        // 删除原有的用户角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);

        // 如果角色列表为空，则只删除不添加
        if (roleIds == null || roleIds.isEmpty()) {
            LogUtil.info(logger, "清空用户 {} 的所有角色", user.getUsername());
            return true;
        }

        // 批量插入新的用户角色关联
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(userRole);
        }

        LogUtil.info(logger, "为用户 {} 分配 {} 个角色", user.getUsername(), roleIds.size());
        return true;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserWithRolesDTO> pageWithRoles(PageRequest pageRequest) {
        // 1. 先查询用户分页数据
        Page<User> userPage = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        Page<User> result = this.page(userPage);

        // 2. 转换为带角色的DTO
        Page<UserWithRolesDTO> dtoPage = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        dtoPage.setTotal(result.getTotal());
        dtoPage.setPages(result.getPages());

        List<UserWithRolesDTO> dtoList = new ArrayList<>();
        for (User user : result.getRecords()) {
            UserWithRolesDTO dto = new UserWithRolesDTO();
            BeanUtils.copyProperties(user, dto);
            dto.setId(user.getId());

            // 查询用户的角色列表
            List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
            List<UserWithRolesDTO.RoleInfo> roleInfos = roles.stream().map(role -> {
                UserWithRolesDTO.RoleInfo roleInfo = new UserWithRolesDTO.RoleInfo();
                roleInfo.setId(role.getId());
                roleInfo.setRoleName(role.getRoleName());
                roleInfo.setRoleCode(role.getRoleCode());
                return roleInfo;
            }).collect(Collectors.toList());

            dto.setRoles(roleInfos);
            dtoList.add(dto);
        }

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }
}
