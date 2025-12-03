package com.ankai.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内信实体类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@TableName("sys_message")
@Schema(description = "站内信")
public class Message {

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "消息ID")
    private Long id;

    /**
     * 发送者ID
     */
    @Schema(description = "发送者ID")
    private Long senderId;

    /**
     * 发送者用户名
     */
    @Schema(description = "发送者用户名")
    private String senderName;

    /**
     * 接收者ID
     */
    @Schema(description = "接收者ID")
    private Long receiverId;

    /**
     * 接收者用户名
     */
    @Schema(description = "接收者用户名")
    private String receiverName;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String content;

    /**
     * 消息类型：1-系统通知 2-私信 3-公告
     */
    @Schema(description = "消息类型：1-系统通知 2-私信 3-公告")
    private Integer messageType;

    /**
     * 是否已读：0-未读 1-已读
     */
    @Schema(description = "是否已读：0-未读 1-已读")
    private Integer isRead;

    /**
     * 阅读时间
     */
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    @TableLogic
    @Schema(description = "是否删除")
    private Integer deleted;
}

