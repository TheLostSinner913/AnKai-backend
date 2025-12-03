package com.ankai.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Component
public class JwtUtil {

    private static final Logger logger = LogUtil.getLogger(JwtUtil.class);

    // JWT密钥
    @Value("${jwt.secret:ankai-backend-jwt-secret-key-2024}")
    private String secret;

    // JWT过期时间（默认2小时）
    @Value("${jwt.expiration:7200}")
    private Long expiration;

    // 记住我过期时间（默认7天）
    @Value("${jwt.expiration-remember:604800}")
    private Long expirationRemember;

    /**
     * 生成JWT Token
     *
     * @param userId     用户ID
     * @param username   用户名
     * @param rememberMe 是否记住我
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, boolean rememberMe) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        long expireTime = rememberMe ? expirationRemember : expiration;
        return createToken(claims, username, expireTime);
    }

    /**
     * 创建Token
     *
     * @param claims     声明
     * @param subject    主题
     * @param expireTime 过期时间（秒）
     * @return JWT Token
     */
    private String createToken(Map<String, Object> claims, String subject, long expireTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireTime * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            LogUtil.error(logger, "从Token中获取用户名失败", e);
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            LogUtil.error(logger, "从Token中获取用户ID失败", e);
            return null;
        }
    }

    /**
     * 从Token中获取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            LogUtil.error(logger, "从Token中获取过期时间失败", e);
            return null;
        }
    }

    /**
     * 验证Token是否有效
     *
     * @param token    JWT Token
     * @param username 用户名
     * @return 是否有效
     */
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            LogUtil.error(logger, "验证Token失败", e);
            return false;
        }
    }

    /**
     * 验证Token是否有效（不验证用户名）
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            LogUtil.error(logger, "验证Token失败", e);
            return false;
        }
    }

    /**
     * 检查Token是否过期
     *
     * @param token JWT Token
     * @return 是否过期
     */
    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 从Token中获取Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSigningKey() {
        try {
            // 确保密钥长度至少为64字节（512位）
            byte[] keyBytes = secret.getBytes("UTF-8");

            // 如果密钥长度不够，使用SHA-256扩展
            if (keyBytes.length < 64) {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String extendedSecret = secret;

                // 重复密钥直到长度足够
                while (extendedSecret.getBytes("UTF-8").length < 64) {
                    extendedSecret += secret;
                }

                keyBytes = digest.digest(extendedSecret.getBytes("UTF-8"));

                // 如果还不够长，再次扩展
                if (keyBytes.length < 64) {
                    byte[] extendedKey = new byte[64];
                    System.arraycopy(keyBytes, 0, extendedKey, 0, keyBytes.length);
                    System.arraycopy(keyBytes, 0, extendedKey, keyBytes.length, 64 - keyBytes.length);
                    keyBytes = extendedKey;
                }
            }

            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            LogUtil.error(logger, "生成签名密钥失败", e);
            // 降级方案：生成一个安全的随机密钥
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    /**
     * 刷新Token
     *
     * @param token 原Token
     * @return 新Token
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();

            return generateToken(userId, username, false);
        } catch (Exception e) {
            LogUtil.error(logger, "刷新Token失败", e);
            return null;
        }
    }
}
