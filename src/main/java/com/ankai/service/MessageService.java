package com.ankai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ankai.common.PageRequest;
import com.ankai.dto.ChatSession;
import com.ankai.entity.Message;

import java.util.List;

/**
 * 站内信服务接口
 *
 * @author AnKai
 * @since 2024-01-01
 */
public interface MessageService extends IService<Message> {

    /**
     * 发送消息（聊天风格，无标题）
     */
    boolean sendMessage(Long senderId, String senderName, Long receiverId, String receiverName, String content);

    /**
     * 获取与指定用户的聊天记录
     */
    Page<Message> getChatHistory(Long userId, Long otherUserId, PageRequest pageRequest);

    /**
     * 获取聊天会话列表
     */
    List<ChatSession> getChatSessions(Long userId);

    /**
     * 分页查询收到的消息
     */
    Page<Message> pageReceivedMessages(Long userId, PageRequest pageRequest);

    /**
     * 分页查询发送的消息
     */
    Page<Message> pageSentMessages(Long userId, PageRequest pageRequest);

    /**
     * 标记消息为已读
     */
    boolean markAsRead(Long messageId, Long userId);

    /**
     * 标记与某用户的聊天为已读
     */
    boolean markChatAsRead(Long userId, Long otherUserId);

    /**
     * 批量标记为已读
     */
    boolean batchMarkAsRead(List<Long> messageIds, Long userId);

    /**
     * 标记所有消息为已读
     */
    boolean markAllAsRead(Long userId);

    /**
     * 获取未读消息数量
     */
    int getUnreadCount(Long userId);

    /**
     * 删除消息
     */
    boolean deleteMessage(Long messageId, Long userId);
}
