package com.ankai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ankai.dto.LoginRequest;
import com.ankai.dto.LoginResponse;
import com.ankai.entity.Permission;
import com.ankai.entity.Role;
import com.ankai.entity.User;
import com.ankai.exception.BusinessException;
import com.ankai.mapper.PermissionMapper;
import com.ankai.mapper.RoleMapper;
import com.ankai.mapper.UserMapper;
import com.ankai.security.CustomUserDetails;
import com.ankai.service.AuthService;
import com.ankai.service.OnlineUserService;
import com.ankai.utils.JwtUtil;
import com.ankai.utils.LogUtil;
import com.ankai.utils.RedisUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务实现类 - 基于Spring Security + JWT
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LogUtil.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OnlineUserService onlineUserService;

    // Token黑名单Redis key前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "auth:blacklist:";

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        LogUtil.info(logger, "用户登录请求: {}", loginRequest.getUsername());

        try {
            // 1. 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            // 2. 获取认证用户信息
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 3. 生成JWT Token
            String token = jwtUtil.generateToken(
                    user.getId(),
                    user.getUsername(),
                    loginRequest.getRememberMe() != null && loginRequest.getRememberMe());

            // 4. 查询用户角色代码
            List<Role> roles = roleMapper.selectRolesByUserId(user.getId());
            List<String> roleCodes = roles.stream()
                    .map(Role::getRoleCode)
                    .collect(Collectors.toList());

            // 5. 查询用户权限码
            List<Permission> permissions = permissionMapper.selectPermissionsByUserId(user.getId());
            List<String> permissionCodes = permissions.stream()
                    .map(Permission::getPermissionCode)
                    .filter(code -> code != null && !code.isEmpty())
                    .collect(Collectors.toList());

            // 6. 构建用户信息
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRealName(user.getRealName());
            userInfo.setEmail(user.getEmail());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setRoles(roleCodes); // 使用角色代码，如 SUPER_ADMIN, ADMIN
            userInfo.setPermissions(permissionCodes); // 使用权限码，如 role:query, role:add

            // 6. 构建响应
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTokenType("Bearer");
            response.setExpiresIn(
                    loginRequest.getRememberMe() != null && loginRequest.getRememberMe() ? 604800L : 7200L);
            response.setUserInfo(userInfo);

            // 7. 设置用户在线状态
            onlineUserService.userOnline(user.getId(), user.getUsername());

            LogUtil.info(logger, "用户登录成功: {}", loginRequest.getUsername());
            return response;

        } catch (Exception e) {
            LogUtil.error(logger, "用户登录失败: {}", loginRequest.getUsername(), e);
            throw BusinessException.of(401, "用户名或密码错误");
        }
    }

    @Override
    public boolean logout(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            return false;
        }

        // 获取用户信息并设置离线
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            onlineUserService.userOffline(userId);
        }

        // 将token加入黑名单（Redis）
        String username = jwtUtil.getUsernameFromToken(token);
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        // 设置过期时间为2小时（与JWT默认过期时间一致）
        redisUtil.set(blacklistKey, System.currentTimeMillis(), 7200);

        LogUtil.info(logger, "用户登出成功: {}", username);
        return true;
    }

    @Override
    public LoginResponse.UserInfo getCurrentUser(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }

        // 检查token是否在黑名单中（Redis）
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        if (redisUtil.hasKey(blacklistKey)) {
            return null;
        }

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            return null;
        }

        try {
            // 从token中获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);

            // 查询用户详细信息
            User user = userMapper.selectByUsernameWithDeleted(username);
            if (user == null || user.getDeleted() == 1 || user.getStatus() == 0) {
                return null;
            }

            // 查询用户角色代码
            List<Role> roles = roleMapper.selectRolesByUserId(userId);
            List<String> roleCodes = roles.stream()
                    .map(Role::getRoleCode)
                    .collect(Collectors.toList());

            // 查询用户权限码
            List<Permission> permissions = permissionMapper.selectPermissionsByUserId(userId);
            List<String> permissionCodes = permissions.stream()
                    .map(Permission::getPermissionCode)
                    .filter(code -> code != null && !code.isEmpty())
                    .collect(Collectors.toList());

            // 构建用户信息
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setRealName(user.getRealName());
            userInfo.setEmail(user.getEmail());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setRoles(roleCodes); // 使用角色代码，如 SUPER_ADMIN, ADMIN
            userInfo.setPermissions(permissionCodes); // 使用权限码

            return userInfo;

        } catch (Exception e) {
            LogUtil.error(logger, "获取当前用户信息失败", e);
            return null;
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }

        // 检查token是否在黑名单中（Redis）
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        if (redisUtil.hasKey(blacklistKey)) {
            return false;
        }

        return jwtUtil.validateToken(token);
    }
}
