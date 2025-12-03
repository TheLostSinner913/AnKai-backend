package com.ankai.controller;

import com.ankai.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试Controller - 验证前后端连接
 *
 * @author AnKai
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/test")
@Tag(name = "连接测试", description = "测试前后端连接")
public class TestController {

    /**
     * 测试连接
     */
    @GetMapping("/hello")
    @Operation(summary = "测试连接")
    public Result<Map<String, Object>> hello() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello from AnKai Backend!");
        data.put("timestamp", LocalDateTime.now());
        data.put("status", "success");

        return Result.success(data);
    }

    /**
     * 获取服务器信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取服务器信息")
    public Result<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("serverName", "AnKai Backend");
        data.put("version", "1.0.0");
        data.put("framework", "Spring Boot 3.0.6");
        data.put("database", "MyBatis Plus");
        data.put("currentTime", LocalDateTime.now());

        return Result.success(data);
    }

}
