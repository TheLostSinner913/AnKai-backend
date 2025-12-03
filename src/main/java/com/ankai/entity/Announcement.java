package com.ankai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统公告实体类
 *
 * @author AnKai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_announcement")
@Schema(description = "系统公告")
public class Announcement extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("title")
    @Schema(description = "公告标题")
    private String title;

    @TableField("content")
    @Schema(description = "公告内容")
    private String content;

    @TableField("announcement_type")
    @Schema(description = "公告类型：1-普通 2-重要 3-紧急")
    private Integer announcementType;

    @TableField("target_type")
    @Schema(description = "发布范围：1-全员 2-指定用户 3-指定角色")
    private Integer targetType;

    @TableField("status")
    @Schema(description = "状态：0-草稿 1-已发布 2-已撤回")
    private Integer status;

    @TableField("publish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @TableField("is_top")
    @Schema(description = "是否置顶：0-否 1-是")
    private Integer isTop;

    @TableField("top_order")
    @Schema(description = "置顶排序")
    private Integer topOrder;

    @TableField("view_count")
    @Schema(description = "浏览次数")
    private Integer viewCount;

    @TableField("create_by")
    @Schema(description = "创建人ID")
    private Long createBy;

    @TableField("update_by")
    @Schema(description = "更新人ID")
    private Long updateBy;

    /**
     * 创建人姓名（非数据库字段，用于展示）
     */
    @TableField(exist = false)
    @Schema(description = "创建人姓名")
    private String createByName;
}
