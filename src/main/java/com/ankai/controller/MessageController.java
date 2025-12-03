package com.ankai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ankai.common.PageRequest;
import com.ankai.common.Result;
import com.ankai.dto.ChatSession;
import com.ankai.dto.SendMessageRequest;
import com.ankai.entity.Message;
import com.ankai.entity.User;
import com.ankai.service.MessageService;
import com.ankai.service.UserService;
import com.ankai.utils.JwtUtil;
import com.ankai.utils.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 站内信Controller
 *
 * @author AnKai
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/message")
@Tag(name = "站内信管理", description = "站内信相关接口")
public class MessageController {

    private static final Logger logger = LogUtil.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 发送消息（聊天风格）
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息")
    public Result<Boolean> sendMessage(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody SendMessageRequest request) {

        String token = extractToken(authorization);
        Long senderId = jwtUtil.getUserIdFromToken(token);
        String senderName = jwtUtil.getUsernameFromToken(token);

        // 获取接收者信息
        User receiver = userService.getById(request.getReceiverId());
        if (receiver == null) {
            return Result.error("接收者不存在");
        }

        boolean result = messageService.sendMessage(
                senderId, senderName,
                request.getReceiverId(), receiver.getUsername(),
                request.getContent());

        LogUtil.info(logger, "发送消息: {} -> {}", senderName, receiver.getUsername());
        return result ? Result.success(true) : Result.error("发送失败");
    }

    /**
     * 获取聊天会话列表
     */
    @GetMapping("/sessions")
    @Operation(summary = "获取聊天会话列表")
    public Result<List<ChatSession>> getChatSessions(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        List<ChatSession> sessions = messageService.getChatSessions(userId);
        return Result.success(sessions);
    }

    /**
     * 获取与指定用户的聊天记录
     */
    @PostMapping("/chat/{otherUserId}")
    @Operation(summary = "获取与指定用户的聊天记录")
    public Result<Page<Message>> getChatHistory(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long otherUserId,
            @RequestBody PageRequest pageRequest) {

        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Page<Message> page = messageService.getChatHistory(userId, otherUserId, pageRequest);
        return Result.success(page);
    }

    /**
     * 获取收到的消息列表
     */
    @PostMapping("/received")
    @Operation(summary = "获取收到的消息列表")
    public Result<Page<Message>> getReceivedMessages(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PageRequest pageRequest) {

        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Page<Message> page = messageService.pageReceivedMessages(userId, pageRequest);
        return Result.success(page);
    }

    /**
     * 获取发送的消息列表
     */
    @PostMapping("/sent")
    @Operation(summary = "获取发送的消息列表")
    public Result<Page<Message>> getSentMessages(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PageRequest pageRequest) {

        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        Page<Message> page = messageService.pageSentMessages(userId, pageRequest);
        return Result.success(page);
    }

    /**
     * 标记与某用户的所有消息为已读
     */
    @PutMapping("/chat/{otherUserId}/read")
    @Operation(summary = "标记与某用户的聊天为已读")
    public Result<Boolean> markChatAsRead(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long otherUserId) {

        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 标记来自该用户的所有未读消息为已读
        boolean result = messageService.markChatAsRead(userId, otherUserId);
        return result ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "标记消息为已读")
    public Result<Boolean> markAsRead(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {

        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        boolean result = messageService.markAsRead(id, userId);
        return result ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 批量标记为已读
     */
    @PutMapping("/batch-read")
    @Operation(summary = "批量标记为已读")
    public Result<Boolean> batchMarkAsRead(
            @RequestHeader("Authorization") String authorization,
            @RequestBody List<Long> ids) {

        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        boolean result = messageService.batchMarkAsRead(ids, userId);
        return result ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 标记所有消息为已读
     */
    @PutMapping("/read-all")
    @Operation(summary = "标记所有消息为已读")
    public Result<Boolean> markAllAsRead(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        boolean result = messageService.markAllAsRead(userId);
        return result ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count")
    @Operation(summary = "获取未读消息数量")
    public Result<Integer> getUnreadCount(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        int count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息")
    public Result<Boolean> deleteMessage(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {

        String token = extractToken(authorization);
        Long userId = jwtUtil.getUserIdFromToken(token);

        boolean result = messageService.deleteMessage(id, userId);
        return result ? Result.success(true) : Result.error("删除失败");
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
