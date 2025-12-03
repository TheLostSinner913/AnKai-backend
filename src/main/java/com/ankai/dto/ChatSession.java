package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天会话DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "聊天会话")
public class ChatSession {

    /**
     * 对方用户ID
     */
    @Schema(description = "对方用户ID")
    private Long userId;

    /**
     * 对方用户名
     */
    @Schema(description = "对方用户名")
    private String username;

    /**
     * 对方头像
     */
    @Schema(description = "对方头像")
    private String avatar;

    /**
     * 最后一条消息内容
     */
    @Schema(description = "最后一条消息内容")
    private String lastMessage;

    /**
     * 最后消息时间
     */
    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;

    /**
     * 未读消息数量
     */
    @Schema(description = "未读消息数量")
    private Integer unreadCount;

    /**
     * 对方是否在线
     */
    @Schema(description = "对方是否在线")
    private Boolean online;
}

