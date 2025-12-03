package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色权限分配请求DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "角色权限分配请求")
public class RolePermissionRequest {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    /**
     * 权限ID列表
     */
    @NotNull(message = "权限ID列表不能为空")
    @Schema(description = "权限ID列表", example = "[1, 2, 3]")
    private List<Long> permissionIds;
}

