package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色创建请求DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "角色创建请求")
public class RoleCreateRequest {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 50, message = "角色名称长度必须在2-50个字符之间")
    @Schema(description = "角色名称", example = "产品经理")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(min = 2, max = 50, message = "角色编码长度必须在2-50个字符之间")
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

