package com.ankai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户实体类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户信息")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    @TableField("username")
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    @TableField("password")
    @Schema(description = "密码", example = "123456")
    private String password;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @TableField("email")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 头像URL
     */
    @TableField("avatar")
    @Schema(description = "头像URL")
    private String avatar;

    /**
     * 用户状态 (0-禁用, 1-启用)
     */
    @TableField("status")
    @Schema(description = "用户状态", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    @Schema(description = "备注")
    private String remark;
}
