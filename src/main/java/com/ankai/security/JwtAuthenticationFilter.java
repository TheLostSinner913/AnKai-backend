package com.ankai.security;

import com.ankai.utils.JwtUtil;
import com.ankai.utils.LogUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LogUtil.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 跳过不需要JWT验证的路径
            String requestPath = request.getRequestURI();
            String method = request.getMethod();
            LogUtil.info(logger, "JWT过滤器处理请求: {} {}", method, requestPath);

            if (shouldSkipFilter(requestPath)) {
                LogUtil.info(logger, "跳过JWT验证，直接放行: {}", requestPath);
                filterChain.doFilter(request, response);
                return;
            }

            // 从请求头中获取JWT Token
            String jwt = getJwtFromRequest(request);
            LogUtil.info(logger, "=== JWT Token检查 ===");
            LogUtil.info(logger, "Token存在: {}", jwt != null);

            if (jwt != null) {
                LogUtil.info(logger, "Token长度: {}", jwt.length());
                LogUtil.info(logger, "Token前20位: {}", jwt.length() > 20 ? jwt.substring(0, 20) + "..." : jwt);
            }

            if (StringUtils.hasText(jwt)) {
                LogUtil.info(logger, "开始验证Token...");
                boolean isValid = jwtUtil.validateToken(jwt);
                LogUtil.info(logger, "Token验证结果: {}", isValid);

                if (isValid) {
                    // 从Token中获取用户名
                    String username = jwtUtil.getUsernameFromToken(jwt);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 加载用户详情
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // 验证Token
                        if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                            // 创建认证对象
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            // 设置到Security上下文
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            LogUtil.info(logger, "用户 {} 认证成功，已设置到Security上下文", username);
                        } else {
                            LogUtil.warn(logger, "Token验证失败，用户名: {}", username);
                        }
                    } else {
                        LogUtil.warn(logger, "无法从Token中获取用户名或用户已认证");
                    }
                } else {
                    LogUtil.warn(logger, "Token验证失败");
                }
            } else {
                LogUtil.warn(logger, "Token为空或格式不正确");
            }
        } catch (Exception ex) {
            LogUtil.error(logger, "无法设置用户认证", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否应该跳过JWT过滤器
     */
    private boolean shouldSkipFilter(String requestPath) {
        // 跳过认证相关的接口
        if (requestPath.startsWith("/auth/")) {
            return true;
        }

        // 跳过测试接口
        if (requestPath.startsWith("/test/")) {
            return true;
        }

        // 跳过密码工具接口
        if (requestPath.startsWith("/password/")) {
            return true;
        }

        // 跳过健康检查接口
        if (requestPath.startsWith("/health/")) {
            return true;
        }

        // 跳过Swagger相关接口
        if (requestPath.startsWith("/swagger-ui/") ||
                requestPath.startsWith("/v3/api-docs") ||
                requestPath.equals("/swagger-ui.html") ||
                requestPath.startsWith("/swagger-resources/") ||
                requestPath.startsWith("/webjars/")) {
            return true;
        }

        // 跳过静态资源
        if (requestPath.startsWith("/css/") ||
                requestPath.startsWith("/js/") ||
                requestPath.startsWith("/images/") ||
                requestPath.equals("/favicon.ico")) {
            return true;
        }

        // 跳过错误页面
        if (requestPath.equals("/error")) {
            return true;
        }

        return false;
    }

    /**
     * 从请求头中获取JWT Token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        LogUtil.info(logger, "=== Authorization头检查 ===");
        LogUtil.info(logger, "Authorization头存在: {}", bearerToken != null);

        if (bearerToken != null) {
            LogUtil.info(logger, "Authorization头内容: {}", bearerToken);
            LogUtil.info(logger, "是否以'Bearer '开头: {}", bearerToken.startsWith("Bearer "));
        }

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            LogUtil.info(logger, "成功提取Token，长度: {}", token.length());
            LogUtil.info(logger, "Token前20位: {}", token.length() > 20 ? token.substring(0, 20) + "..." : token);
            return token;
        }

        LogUtil.warn(logger, "Authorization头格式不正确或为空，无法提取Token");
        return null;
    }
}
