package com.ankai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ankai.common.PageRequest;
import com.ankai.dto.ChatSession;
import com.ankai.entity.Message;
import com.ankai.entity.User;
import com.ankai.mapper.MessageMapper;
import com.ankai.service.MessageService;
import com.ankai.service.OnlineUserService;
import com.ankai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 站内信服务实现类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private UserService userService;

    @Autowired
    private OnlineUserService onlineUserService;

    @Override
    public boolean sendMessage(Long senderId, String senderName, Long receiverId, String receiverName, String content) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setSenderName(senderName);
        message.setReceiverId(receiverId);
        message.setReceiverName(receiverName);
        message.setContent(content);
        message.setMessageType(2); // 默认私信
        message.setIsRead(0);
        message.setDeleted(0);
        return save(message);
    }

    @Override
    public Page<Message> getChatHistory(Long userId, Long otherUserId, PageRequest pageRequest) {
        Page<Message> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        // 查询两人之间的所有消息
        wrapper.and(w -> w
                .and(inner -> inner.eq(Message::getSenderId, userId).eq(Message::getReceiverId, otherUserId))
                .or(inner -> inner.eq(Message::getSenderId, otherUserId).eq(Message::getReceiverId, userId)))
                .orderByDesc(Message::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public List<ChatSession> getChatSessions(Long userId) {
        // 获取所有与当前用户相关的消息
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getSenderId, userId).or().eq(Message::getReceiverId, userId))
                .orderByDesc(Message::getCreateTime);
        List<Message> allMessages = list(wrapper);

        // 按对方用户分组，找出每个会话的最新消息
        Map<Long, ChatSession> sessionMap = new LinkedHashMap<>();
        for (Message msg : allMessages) {
            Long otherUserId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            String otherUserName = msg.getSenderId().equals(userId) ? msg.getReceiverName() : msg.getSenderName();

            if (!sessionMap.containsKey(otherUserId)) {
                ChatSession session = new ChatSession();
                session.setUserId(otherUserId);
                session.setUsername(otherUserName);
                session.setLastMessage(msg.getContent());
                session.setLastMessageTime(msg.getCreateTime());
                session.setUnreadCount(0);
                session.setOnline(onlineUserService.isOnline(otherUserId));

                // 获取用户头像
                User otherUser = userService.getById(otherUserId);
                if (otherUser != null) {
                    session.setAvatar(otherUser.getAvatar());
                    session.setUsername(
                            otherUser.getRealName() != null ? otherUser.getRealName() : otherUser.getUsername());
                }

                sessionMap.put(otherUserId, session);
            }

            // 统计未读消息数（只统计收到的未读消息）
            if (msg.getReceiverId().equals(userId) && msg.getIsRead() == 0) {
                ChatSession session = sessionMap.get(otherUserId);
                session.setUnreadCount(session.getUnreadCount() + 1);
            }
        }

        return new ArrayList<>(sessionMap.values());
    }

    @Override
    public Page<Message> pageReceivedMessages(Long userId, PageRequest pageRequest) {
        Page<Message> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
                .orderByDesc(Message::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public Page<Message> pageSentMessages(Long userId, PageRequest pageRequest) {
        Page<Message> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSenderId, userId)
                .orderByDesc(Message::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public boolean markAsRead(Long messageId, Long userId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getId, messageId)
                .eq(Message::getReceiverId, userId)
                .set(Message::getIsRead, 1)
                .set(Message::getReadTime, LocalDateTime.now());
        return update(wrapper);
    }

    @Override
    public boolean markChatAsRead(Long userId, Long otherUserId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
                .eq(Message::getSenderId, otherUserId)
                .eq(Message::getIsRead, 0)
                .set(Message::getIsRead, 1)
                .set(Message::getReadTime, LocalDateTime.now());
        return update(wrapper);
    }

    @Override
    public boolean batchMarkAsRead(List<Long> messageIds, Long userId) {
        if (messageIds == null || messageIds.isEmpty()) {
            return true;
        }
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Message::getId, messageIds)
                .eq(Message::getReceiverId, userId)
                .set(Message::getIsRead, 1)
                .set(Message::getReadTime, LocalDateTime.now());
        return update(wrapper);
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, 0)
                .set(Message::getIsRead, 1)
                .set(Message::getReadTime, LocalDateTime.now());
        return update(wrapper);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return baseMapper.countUnreadByUserId(userId);
    }

    @Override
    public boolean deleteMessage(Long messageId, Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getId, messageId)
                .and(w -> w.eq(Message::getSenderId, userId).or().eq(Message::getReceiverId, userId));
        return remove(wrapper);
    }
}
