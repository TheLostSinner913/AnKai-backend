package com.ankai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 待办事项实体类
 *
 * @author AnKai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_todo")
@Schema(description = "待办事项")
public class Todo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("user_id")
    @Schema(description = "所属用户ID")
    private Long userId;

    @TableField("title")
    @Schema(description = "待办标题")
    private String title;

    @TableField("description")
    @Schema(description = "待办描述")
    private String description;

    @TableField("todo_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "待办日期")
    private LocalDate todoDate;

    @TableField("start_time")
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "开始时间")
    private LocalTime startTime;

    @TableField("end_time")
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "结束时间")
    private LocalTime endTime;

    @TableField("priority")
    @Schema(description = "优先级：1-低 2-中 3-高")
    private Integer priority;

    @TableField("status")
    @Schema(description = "状态：0-待办 1-进行中 2-已完成 3-已取消 4-已忽略")
    private Integer status;

    @TableField("color")
    @Schema(description = "日历显示颜色")
    private String color;

    @TableField("todo_type")
    @Schema(description = "待办类型：1-个人添加 2-系统分配")
    private Integer todoType;

    @TableField("source_type")
    @Schema(description = "来源类型")
    private String sourceType;

    @TableField("source_id")
    @Schema(description = "来源ID")
    private Long sourceId;

    @TableField("remind_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "提醒时间")
    private LocalDateTime remindTime;

    @TableField("is_reminded")
    @Schema(description = "是否已提醒：0-否 1-是")
    private Integer isReminded;
}
