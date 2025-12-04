package com.ankai.service.impl;

import com.ankai.service.SseService;
import com.ankai.utils.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE服务实现类
 *
 * @author AnKai
 */
@Service
public class SseServiceImpl implements SseService {

    private static final Logger logger = LogUtil.getLogger(SseServiceImpl.class);

    /**
     * 存储所有用户的SSE连接
     * Key: userId, Value: SseEmitter
     */
    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * SSE连接超时时间：30分钟
     */
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L;

    @Override
    public SseEmitter createConnection(Long userId) {
        // 如果已存在连接，先移除旧连接
        if (emitterMap.containsKey(userId)) {
            removeConnection(userId);
        }

        // 创建新的SSE连接，设置超时时间
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // 设置回调
        emitter.onCompletion(() -> {
            LogUtil.info(logger, "SSE连接完成: userId={}", userId);
            emitterMap.remove(userId);
        });

        emitter.onTimeout(() -> {
            LogUtil.info(logger, "SSE连接超时: userId={}", userId);
            emitterMap.remove(userId);
        });

        emitter.onError((e) -> {
            LogUtil.warn(logger, "SSE连接错误: userId={}, error={}", userId, e.getMessage());
            emitterMap.remove(userId);
        });

        emitterMap.put(userId, emitter);
        LogUtil.info(logger, "SSE连接建立: userId={}, 当前在线: {}", userId, emitterMap.size());

        // 发送连接成功消息
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("{\"message\":\"SSE连接成功\"}"));
        } catch (IOException e) {
            LogUtil.error(logger, "发送连接成功消息失败", e);
        }

        return emitter;
    }

    @Override
    public void removeConnection(Long userId) {
        SseEmitter emitter = emitterMap.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                // 忽略关闭时的异常
            }
            LogUtil.info(logger, "SSE连接移除: userId={}", userId);
        }
    }

    @Override
    public void sendToUser(Long userId, String eventType, Object data) {
        SseEmitter emitter = emitterMap.get(userId);
        if (emitter == null) {
            LogUtil.debug(logger, "用户不在线，无法推送: userId={}", userId);
            return;
        }

        try {
            String jsonData = objectMapper.writeValueAsString(data);
            emitter.send(SseEmitter.event()
                    .name(eventType)
                    .data(jsonData));
            LogUtil.debug(logger, "SSE推送成功: userId={}, eventType={}", userId, eventType);
        } catch (IOException e) {
            LogUtil.warn(logger, "SSE推送失败，移除连接: userId={}", userId);
            removeConnection(userId);
        }
    }

    @Override
    public void sendToAll(String eventType, Object data) {
        LogUtil.info(logger, "SSE广播: eventType={}, 在线用户数={}", eventType, emitterMap.size());
        emitterMap.keySet().forEach(userId -> sendToUser(userId, eventType, data));
    }

    @Override
    public boolean isUserOnline(Long userId) {
        return emitterMap.containsKey(userId);
    }

    @Override
    public int getOnlineCount() {
        return emitterMap.size();
    }
}

