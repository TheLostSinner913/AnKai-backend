package com.ankai.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码编码工具类
 * 用于生成BCrypt加密密码
 *
 * @author AnKai
 * @since 2024-01-01
 */
public class PasswordEncoderUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成加密密码
        String password1 = "Ankai2025";
        String password2 = "123456";

        String encodedPassword1 = encoder.encode(password1);
        String encodedPassword2 = encoder.encode(password2);

        System.out.println("原密码: " + password1);
        System.out.println("加密后: " + encodedPassword1);
        System.out.println();
        System.out.println("原密码: " + password2);
        System.out.println("加密后: " + encodedPassword2);

        // 验证密码
        boolean matches1 = encoder.matches(password1, encodedPassword1);
        boolean matches2 = encoder.matches(password2, encodedPassword2);
        System.out.println("验证结果1: " + matches1);
        System.out.println("验证结果2: " + matches2);
    }
}
