package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户信息（含角色列表）DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户信息（含角色列表）")
public class UserWithRolesDTO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "用户状态 (0-禁用, 1-启用)")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private List<RoleInfo> roles;

    /**
     * 角色简要信息
     */
    @Data
    @Schema(description = "角色简要信息")
    public static class RoleInfo {
        @Schema(description = "角色ID")
        private Long id;

        @Schema(description = "角色名称")
        private String roleName;

        @Schema(description = "角色编码")
        private String roleCode;
    }
}

