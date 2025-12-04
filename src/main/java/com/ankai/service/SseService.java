package com.ankai.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE服务接口 - 服务端推送事件
 *
 * @author AnKai
 */
public interface SseService {

    /**
     * 创建用户的SSE连接
     * @param userId 用户ID
     * @return SseEmitter
     */
    SseEmitter createConnection(Long userId);

    /**
     * 移除用户的SSE连接
     * @param userId 用户ID
     */
    void removeConnection(Long userId);

    /**
     * 向指定用户推送消息
     * @param userId 用户ID
     * @param eventType 事件类型（如：message, announcement, todo）
     * @param data 推送的数据
     */
    void sendToUser(Long userId, String eventType, Object data);

    /**
     * 向所有在线用户推送消息
     * @param eventType 事件类型
     * @param data 推送的数据
     */
    void sendToAll(String eventType, Object data);

    /**
     * 检查用户是否在线（有SSE连接）
     * @param userId 用户ID
     * @return 是否在线
     */
    boolean isUserOnline(Long userId);

    /**
     * 获取当前在线用户数量
     * @return 在线用户数
     */
    int getOnlineCount();
}

