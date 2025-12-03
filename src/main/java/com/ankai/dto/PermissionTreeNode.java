package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限树节点DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "权限树节点")
public class PermissionTreeNode {

    /**
     * 权限ID
     */
    @Schema(description = "权限ID")
    private Long id;

    /**
     * 父权限ID
     */
    @Schema(description = "父权限ID")
    private Long parentId;

    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    private String permissionName;

    /**
     * 权限编码
     */
    @Schema(description = "权限编码")
    private String permissionCode;

    /**
     * 权限类型 (1-菜单, 2-按钮, 3-接口)
     */
    @Schema(description = "权限类型")
    private Integer permissionType;

    /**
     * 路由路径
     */
    @Schema(description = "路由路径")
    private String path;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 状态 (0-禁用, 1-启用)
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否显示 (0-隐藏, 1-显示)
     */
    @Schema(description = "是否显示")
    private Integer visible;

    /**
     * 子权限列表
     */
    @Schema(description = "子权限列表")
    private List<PermissionTreeNode> children = new ArrayList<>();
}

