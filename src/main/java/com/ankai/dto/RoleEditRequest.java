package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色编辑请求DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "角色编辑请求")
public class RoleEditRequest {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", example = "1")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "产品经理")
    private String roleName;

    /**
     * 角色编码
     */
    @Schema(description = "角色编码", example = "PRODUCT_MANAGER")
    private String roleCode;

    /**
     * 角色描述
     */
    @Schema(description = "角色描述", example = "产品经理角色")
    private String description;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    /**
     * 数据权限范围 (1-全部, 2-本部门及下级, 3-本部门, 4-仅本人, 5-自定义)
     */
    @Schema(description = "数据权限范围", example = "3")
    private Integer dataScope;

    /**
     * 状态 (0-禁用, 1-启用)
     */
    @Schema(description = "状态", example = "1")
    private Integer status;
}

