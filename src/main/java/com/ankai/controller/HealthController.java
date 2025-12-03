package com.ankai.controller;

import com.ankai.common.Result;
import com.ankai.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查Controller
 * 用于验证应用状态和Security配置
 *
 * @author AnKai
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/health")
@Tag(name = "健康检查", description = "应用健康状态检查")
public class HealthController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 健康检查 - 无需认证
     */
    @GetMapping("/check")
    @Operation(summary = "健康检查")
    public Result<Map<String, Object>> healthCheck() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "AnKai Backend is running");
        data.put("version", "1.0.0");

        return Result.success(data);
    }

    /**
     * 认证状态检查 - 需要认证
     */
    @GetMapping("/auth-status")
    @Operation(summary = "认证状态检查")
    public Result<Map<String, Object>> authStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("authenticated", true);
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "User is authenticated");

        return Result.success(data);
    }

    /**
     * Swagger测试接口 - 无需认证
     */
    @GetMapping("/swagger-test")
    @Operation(summary = "Swagger测试接口", description = "用于测试Swagger是否正常工作")
    public Result<Map<String, Object>> swaggerTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("swagger", "working");
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "Swagger is accessible and working properly");
        data.put("documentation", "http://localhost:8080/swagger-ui.html");
        return Result.success(data);
    }

    /**
     * Token测试接口 - 需要认证
     */
    @GetMapping("/token-test")
    @Operation(summary = "Token测试接口", description = "测试Token是否正确传递和验证")
    public Result<Map<String, Object>> tokenTest(
            @Parameter(description = "Authorization头") @RequestHeader(value = "Authorization", required = false) String authorization) {

        Map<String, Object> data = new HashMap<>();
        data.put("authenticated", true);
        data.put("authorization", authorization);
        data.put("hasToken", authorization != null);
        data.put("isBearer", authorization != null && authorization.startsWith("Bearer "));
        data.put("timestamp", LocalDateTime.now());
        data.put("message", "Token验证成功，用户已认证");
        

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            data.put("tokenLength", token.length());
            data.put("tokenPreview", token.length() > 10 ? token.substring(0, 10) + "..." : token);
            
        }

        return Result.success(data);
    }

}
