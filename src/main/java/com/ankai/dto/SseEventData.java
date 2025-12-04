package com.ankai.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * SSE事件推送数据
 *
 * @author AnKai
 */
@Data
public class SseEventData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件类型：new_message, new_announcement, new_todo, unread_update
     */
    private String type;

    /**
     * 未读消息总数（站内信）
     */
    private Integer unreadCount;

    /**
     * 未读公告数
     */
    private Integer unreadAnnouncementCount;

    /**
     * 待办事项数
     */
    private Integer pendingTodoCount;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 附加数据（消息详情等）
     */
    private Object data;
}
