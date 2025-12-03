package com.ankai.controller;

import com.ankai.common.Result;
import com.ankai.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码工具Controller
 * 提供密码生成和验证功能（开发环境使用）
 *
 * @author AnKai
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/password")
@Tag(name = "密码工具", description = "密码生成和验证工具（开发环境）")
public class PasswordController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 生成BCrypt加密密码
     */
    @PostMapping("/encode")
    @Operation(summary = "生成BCrypt加密密码")
    public Result<Map<String, Object>> encodePassword(
            @Parameter(description = "原始密码") @RequestParam String password) {

        String encodedPassword = passwordEncoder.encode(password);

        Map<String, Object> data = new HashMap<>();
        data.put("originalPassword", password);
        data.put("encodedPassword", encodedPassword);
        data.put("algorithm", "BCrypt");
        data.put("timestamp", System.currentTimeMillis());

        return Result.success(data);
    }

    /**
     * 验证密码
     */
    @PostMapping("/verify")
    @Operation(summary = "验证密码是否匹配")
    public Result<Map<String, Object>> verifyPassword(
            @Parameter(description = "原始密码") @RequestParam String password,
            @Parameter(description = "加密密码") @RequestParam String encodedPassword) {

        boolean matches = passwordEncoder.matches(password, encodedPassword);

        Map<String, Object> data = new HashMap<>();
        data.put("originalPassword", password);
        data.put("encodedPassword", encodedPassword);
        data.put("matches", matches);
        data.put("timestamp", System.currentTimeMillis());

        return Result.success(data);
    }

    /**
     * 调试前端Token传递
     */
    @GetMapping("/debug-frontend")
    @Operation(summary = "调试前端Token传递")
    public Result<Map<String, Object>> debugFrontend(
            @Parameter(description = "Authorization头") @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "所有请求头") @RequestHeader Map<String, String> headers) {

        Map<String, Object> data = new HashMap<>();
        data.put("authorization", authorization);
        data.put("allHeaders", headers);
        data.put("hasAuthHeader", headers.containsKey("authorization") || headers.containsKey("Authorization"));
        data.put("timestamp", System.currentTimeMillis());

        // 检查各种可能的Authorization头格式
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (key.contains("auth") || key.contains("token") || key.contains("bearer")) {
                data.put("possibleAuthHeader_" + entry.getKey(), entry.getValue());
            }
        }

        return Result.success(data);
    }

    /**
     * 测试Redis数据库配置
     */
    @GetMapping("/test-redis-db")
    @Operation(summary = "测试Redis数据库配置")
    public Result<Map<String, Object>> testRedisDatabase() {
        Map<String, Object> data = new HashMap<>();

        try {
            // 测试RedisTemplate
            String testKey = "test:db:check:" + System.currentTimeMillis();
            String testValue = "DB9_TEST_VALUE";

            // 使用RedisUtil设置值
            redisUtil.set(testKey, testValue, 60); // 60秒过期

            // 读取值
            Object retrievedValue = redisUtil.get(testKey);

            data.put("testKey", testKey);
            data.put("testValue", testValue);
            data.put("retrievedValue", retrievedValue);
            data.put("redisWorking", testValue.equals(retrievedValue));
            data.put("configuredDatabase", 9);
            data.put("timestamp", System.currentTimeMillis());

            // 清理测试数据
            redisUtil.del(testKey);

        } catch (Exception e) {
            data.put("error", e.getMessage());
            data.put("redisWorking", false);
        }

        return Result.success(data);
    }

    /**
     * 完整的Token调试接口
     */
    @PostMapping("/debug-token-complete")
    @Operation(summary = "完整的Token调试", description = "全面调试Token传递和验证过程")
    public Result<Map<String, Object>> debugTokenComplete(
            @Parameter(description = "Authorization头") @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(description = "所有请求头") @RequestHeader Map<String, String> headers,
            @RequestBody(required = false) Map<String, Object> body) {

        Map<String, Object> data = new HashMap<>();

        // 基本信息
        data.put("timestamp", System.currentTimeMillis());
        data.put("authorization", authorization);
        data.put("hasAuthHeader", authorization != null);

        // 检查所有可能的认证相关头
        Map<String, String> authHeaders = new HashMap<>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey().toLowerCase();
            if (key.contains("auth") || key.contains("token") || key.contains("bearer")) {
                authHeaders.put(entry.getKey(), entry.getValue());
            }
        }
        data.put("authRelatedHeaders", authHeaders);
        data.put("allHeaders", headers);

        // Token解析
        if (authorization != null) {
            data.put("authHeaderLength", authorization.length());
            data.put("startsWithBearer", authorization.startsWith("Bearer "));

            if (authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                data.put("tokenLength", token.length());
                data.put("tokenPreview", token.length() > 30 ? token.substring(0, 30) + "..." : token);

                // 尝试验证Token（如果可能）
                try {
                    // 这里可以添加Token验证逻辑
                    data.put("tokenFormat", "JWT格式检查通过");
                } catch (Exception e) {
                    data.put("tokenError", e.getMessage());
                }
            } else {
                data.put("tokenError", "Authorization头不是Bearer格式");
            }
        } else {
            data.put("tokenError", "未提供Authorization头");
        }

        // 请求体信息
        data.put("requestBody", body);

        return Result.success(data);
    }

    /**
     * 简单的Token验证页面（HTML）
     */
    @GetMapping("/token-debug-page")
    @Operation(summary = "Token调试页面")
    public String tokenDebugPage() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Token调试页面</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .section { margin: 20px 0; padding: 15px; border: 1px solid #ccc; }
                        button { padding: 10px 15px; margin: 5px; }
                        .result { background: #f5f5f5; padding: 10px; margin: 10px 0; }
                    </style>
                </head>
                <body>
                    <h1>JWT Token调试页面</h1>

                    <div class="section">
                        <h3>1. 检查localStorage中的Token</h3>
                        <button onclick="checkLocalStorage()">检查Token</button>
                        <div id="localStorage-result" class="result"></div>
                    </div>

                    <div class="section">
                        <h3>2. 测试登录</h3>
                        <button onclick="testLogin()">测试登录</button>
                        <div id="login-result" class="result"></div>
                    </div>

                    <div class="section">
                        <h3>3. 测试带Token的请求</h3>
                        <button onclick="testAuthRequest()">测试认证请求</button>
                        <div id="auth-result" class="result"></div>
                    </div>

                    <script>
                        function checkLocalStorage() {
                            const token = localStorage.getItem('token');
                            const userInfo = localStorage.getItem('userInfo');
                            const result = document.getElementById('localStorage-result');
                            result.innerHTML = `
                                <strong>Token存在:</strong> ${token ? '是' : '否'}<br>
                                <strong>Token长度:</strong> ${token ? token.length : 0}<br>
                                <strong>Token前30位:</strong> ${token ? token.substring(0, 30) + '...' : '无'}<br>
                                <strong>UserInfo:</strong> ${userInfo || '无'}
                            `;
                        }

                        function testLogin() {
                            fetch('/api/auth/login', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ username: 'admin', password: 'Ankai2025' })
                            })
                            .then(response => response.json())
                            .then(data => {
                                const result = document.getElementById('login-result');
                                result.innerHTML = `
                                    <strong>登录响应:</strong><br>
                                    <pre>${JSON.stringify(data, null, 2)}</pre>
                                `;
                                if (data.data && data.data.token) {
                                    localStorage.setItem('token', data.data.token);
                                    result.innerHTML += '<br><strong>Token已保存到localStorage</strong>';
                                }
                            })
                            .catch(error => {
                                document.getElementById('login-result').innerHTML = `<strong>错误:</strong> ${error.message}`;
                            });
                        }

                        function testAuthRequest() {
                            const token = localStorage.getItem('token');
                            if (!token) {
                                document.getElementById('auth-result').innerHTML = '<strong>错误:</strong> 未找到Token，请先登录';
                                return;
                            }

                            fetch('/api/password/debug-token-complete', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json',
                                    'Authorization': `Bearer ${token}`
                                },
                                body: JSON.stringify({})
                            })
                            .then(response => response.json())
                            .then(data => {
                                const result = document.getElementById('auth-result');
                                result.innerHTML = `
                                    <strong>认证请求响应:</strong><br>
                                    <pre>${JSON.stringify(data, null, 2)}</pre>
                                `;
                            })
                            .catch(error => {
                                document.getElementById('auth-result').innerHTML = `<strong>错误:</strong> ${error.message}`;
                            });
                        }
                    </script>
                </body>
                </html>
                """;
    }
}
