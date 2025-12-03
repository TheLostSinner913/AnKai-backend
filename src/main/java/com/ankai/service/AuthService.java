package com.ankai.service;

import com.ankai.dto.LoginRequest;
import com.ankai.dto.LoginResponse;

/**
 * 认证服务接口
 *
 * @author AnKai
 * @since 2024-01-01
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户登出
     *
     * @param token 令牌
     * @return 是否成功
     */
    boolean logout(String token);

    /**
     * 获取当前用户信息
     *
     * @param token 令牌
     * @return 用户信息
     */
    LoginResponse.UserInfo getCurrentUser(String token);

    /**
     * 验证令牌
     *
     * @param token 令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
}
