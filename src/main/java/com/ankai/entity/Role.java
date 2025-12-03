package com.ankai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@Schema(description = "角色信息")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @TableField("role_name")
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    /**
     * 角色编码
     */
    @TableField("role_code")
    @Schema(description = "角色编码", example = "ADMIN")
    private String roleCode;

    /**
     * 角色描述
     */
    @TableField("description")
    @Schema(description = "角色描述", example = "系统管理员")
    private String description;

    /**
     * 排序
     */
    @TableField("sort_order")
    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    /**
     * 数据权限范围 (1-全部, 2-本部门及下级, 3-本部门, 4-仅本人, 5-自定义)
     */
    @TableField("data_scope")
    @Schema(description = "数据权限范围", example = "1")
    private Integer dataScope;

    /**
     * 状态 (0-禁用, 1-启用)
     */
    @TableField("status")
    @Schema(description = "状态", example = "1")
    private Integer status;

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
