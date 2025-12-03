package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户角色分配请求DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户角色分配请求")
public class UserRoleRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 角色ID列表
     */
    @NotNull(message = "角色ID列表不能为空")
    @Schema(description = "角色ID列表", example = "[1, 2]")
    private List<Long> roleIds;
}

