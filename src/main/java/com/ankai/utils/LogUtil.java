package com.ankai.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * 提供统一的日志记录方法
 *
 * @author AnKai
 * @since 2024-01-01
 */
public class LogUtil {

    /**
     * 获取Logger实例
     *
     * @param clazz 类
     * @return Logger实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 记录INFO级别日志
     *
     * @param logger Logger实例
     * @param message 日志消息
     * @param args 参数
     */
    public static void info(Logger logger, String message, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(message, args);
        }
    }

    /**
     * 记录ERROR级别日志
     *
     * @param logger Logger实例
     * @param message 日志消息
     * @param throwable 异常
     */
    public static void error(Logger logger, String message, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error(message, throwable);
        }
    }

    /**
     * 记录ERROR级别日志
     *
     * @param logger Logger实例
     * @param message 日志消息
     * @param args 参数
     */
    public static void error(Logger logger, String message, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(message, args);
        }
    }

    /**
     * 记录DEBUG级别日志
     *
     * @param logger Logger实例
     * @param message 日志消息
     * @param args 参数
     */
    public static void debug(Logger logger, String message, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, args);
        }
    }

    /**
     * 记录WARN级别日志
     *
     * @param logger Logger实例
     * @param message 日志消息
     * @param args 参数
     */
    public static void warn(Logger logger, String message, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, args);
        }
    }
}
