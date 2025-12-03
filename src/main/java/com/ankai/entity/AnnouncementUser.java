package com.ankai.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告用户关联实体类
 *
 * @author AnKai
 */
@Data
@TableName("sys_announcement_user")
@Schema(description = "公告用户关联")
public class AnnouncementUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("announcement_id")
    @Schema(description = "公告ID")
    private Long announcementId;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("is_read")
    @Schema(description = "是否已读：0-未读 1-已读")
    private Integer isRead;

    @TableField("read_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

