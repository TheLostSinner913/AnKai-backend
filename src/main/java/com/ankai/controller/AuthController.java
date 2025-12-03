package com.ankai.controller;

import com.ankai.common.Result;
import com.ankai.dto.LoginRequest;
import com.ankai.dto.LoginResponse;
import com.ankai.service.AuthService;
import com.ankai.utils.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 认证Controller
 *
 * @author AnKai
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "用户认证", description = "登录、登出、获取用户信息等接口")
public class AuthController {

    private static final Logger logger = LogUtil.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LogUtil.info(logger, "接收到登录请求: {}", loginRequest.getUsername());

        LoginResponse response = authService.login(loginRequest);
        return Result.success(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Boolean> logout(
            @Parameter(description = "Authorization头中的token") @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        boolean result = authService.logout(token);

        LogUtil.info(logger, "用户登出: {}", result ? "成功" : "失败");
        return Result.success(result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/currentUser")
    @Operation(summary = "获取当前用户信息")
    public Result<LoginResponse.UserInfo> getCurrentUser(
            @Parameter(description = "Authorization头中的token") @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        LoginResponse.UserInfo userInfo = authService.getCurrentUser(token);

        if (userInfo == null) {
            return Result.error(401, "未登录或token已过期");
        }

        return Result.success(userInfo);
    }

    /**
     * 验证token
     */
    @GetMapping("/validate")
    @Operation(summary = "验证token")
    public Result<Boolean> validateToken(
            @Parameter(description = "Authorization头中的token") @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        boolean valid = authService.validateToken(token);

        return Result.success(valid);
    }

    /**
     * 从Authorization头中提取token
     */
    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
