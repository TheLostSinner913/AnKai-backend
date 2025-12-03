package com.ankai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 登录响应DTO
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@Schema(description = "登录响应")
public class LoginResponse {

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌")
    private String token;

    /**
     * 令牌类型
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    @Schema(description = "过期时间（秒）", example = "7200")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {
        /**
         * 用户ID
         */
        @Schema(description = "用户ID")
        private Long id;

        /**
         * 用户名
         */
        @Schema(description = "用户名")
        private String username;

        /**
         * 真实姓名
         */
        @Schema(description = "真实姓名")
        private String realName;

        /**
         * 邮箱
         */
        @Schema(description = "邮箱")
        private String email;

        /**
         * 头像
         */
        @Schema(description = "头像")
        private String avatar;

        /**
         * 角色列表
         */
        @Schema(description = "角色列表")
        private List<String> roles;

        /**
         * 权限列表
         */
        @Schema(description = "权限列表")
        private List<String> permissions;
    }
}
