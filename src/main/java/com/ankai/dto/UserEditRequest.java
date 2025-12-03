package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 用户编辑请求DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户编辑请求")
public class UserEditRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 用户名（编辑时不可修改）
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 密码（可选，为空则不修改）
     */
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    @Schema(description = "新密码（可选，为空则不修改）", example = "123456")
    private String password;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 用户状态 (0-禁用, 1-启用)
     */
    @Schema(description = "用户状态", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
