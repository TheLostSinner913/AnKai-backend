package com.ankai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AnKai后端应用启动类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.ankai.mapper")
public class AnKaiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnKaiBackendApplication.class, args);
        System.out.println("AnKai Backend Application Started Successfully!");
    }
}
