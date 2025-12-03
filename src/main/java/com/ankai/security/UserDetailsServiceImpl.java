package com.ankai.security;

import com.ankai.entity.Role;
import com.ankai.entity.User;
import com.ankai.mapper.RoleMapper;
import com.ankai.mapper.UserMapper;
import com.ankai.utils.LogUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security用户详情服务实现
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LogUtil.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LogUtil.info(logger, "加载用户信息: {}", username);

        // 查询用户
        User user = userMapper.selectByUsernameWithDeleted(username);
        if (user == null) {
            LogUtil.warn(logger, "用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查用户状态
        if (user.getDeleted() == 1) {
            LogUtil.warn(logger, "用户已被删除: {}", username);
            throw new UsernameNotFoundException("用户已被删除: " + username);
        }

        if (user.getStatus() == 0) {
            LogUtil.warn(logger, "用户已被禁用: {}", username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 查询用户角色
        List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 添加角色权限（以ROLE_开头）
        authorities.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()))
                .collect(Collectors.toList()));

        // 添加权限（不以ROLE_开头）
        authorities.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleCode()))
                .collect(Collectors.toList()));

        LogUtil.info(logger, "用户 {} 拥有权限: {}", username, authorities);

        // 创建UserDetails对象
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                user.getDeleted() == 0, // accountNonLocked
                authorities,
                user
        );
    }
}
