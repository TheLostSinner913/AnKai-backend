package com.ankai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
@Schema(description = "权限信息")
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父权限ID
     */
    @TableField("parent_id")
    @Schema(description = "父权限ID", example = "0")
    private Long parentId;

    /**
     * 权限名称
     */
    @TableField("permission_name")
    @Schema(description = "权限名称", example = "用户管理")
    private String permissionName;

    /**
     * 权限编码
     */
    @TableField("permission_code")
    @Schema(description = "权限编码", example = "system:user")
    private String permissionCode;

    /**
     * 权限类型 (1-菜单, 2-按钮, 3-接口)
     */
    @TableField("permission_type")
    @Schema(description = "权限类型", example = "1")
    private Integer permissionType;

    /**
     * 路由路径
     */
    @TableField("path")
    @Schema(description = "路由路径", example = "/system/user")
    private String path;

    /**
     * 组件路径
     */
    @TableField("component")
    @Schema(description = "组件路径", example = "system/User")
    private String component;

    /**
     * 图标
     */
    @TableField("icon")
    @Schema(description = "图标", example = "UserOutlined")
    private String icon;

    /**
     * 排序
     */
    @TableField("sort_order")
    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    /**
     * 状态 (0-禁用, 1-启用)
     */
    @TableField("status")
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 是否显示 (0-隐藏, 1-显示)
     */
    @TableField("visible")
    @Schema(description = "是否显示", example = "1")
    private Integer visible;

    /**
     * 创建人
     */
    @TableField("create_by")
    @Schema(description = "创建人")
    private Long createBy;

    /**
     * 更新人
     */
    @TableField("update_by")
    @Schema(description = "更新人")
    private Long updateBy;
}

