package com.ankai.controller;

import com.ankai.service.SseService;
import com.ankai.utils.JwtUtil;
import com.ankai.utils.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE控制器 - 服务端推送事件
 *
 * @author AnKai
 */
@RestController
@RequestMapping("/sse")
@Tag(name = "SSE推送", description = "服务端推送事件接口")
public class SseController {

    private static final Logger logger = LogUtil.getLogger(SseController.class);

    @Autowired
    private SseService sseService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 订阅SSE事件流
     * 前端通过 EventSource 连接此接口
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "订阅SSE事件流")
    public SseEmitter subscribe(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        LogUtil.info(logger, "用户订阅SSE: userId={}", userId);
        return sseService.createConnection(userId);
    }

    /**
     * 断开SSE连接
     */
    @DeleteMapping("/unsubscribe")
    @Operation(summary = "断开SSE连接")
    public void unsubscribe(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        LogUtil.info(logger, "用户断开SSE: userId={}", userId);
        sseService.removeConnection(userId);
    }

    /**
     * 获取在线用户数量
     */
    @GetMapping("/online-count")
    @Operation(summary = "获取在线用户数量")
    public int getOnlineCount() {
        return sseService.getOnlineCount();
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}

