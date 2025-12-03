package com.ankai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 在线用户服务
 * 使用Redis管理用户在线状态
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Service
public class OnlineUserService {

    private static final String ONLINE_USER_KEY = "online:user:";
    private static final String ONLINE_USERS_SET_KEY = "online:users";
    private static final long ONLINE_TIMEOUT_MINUTES = 30; // 30分钟过期

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户上线
     */
    public void userOnline(Long userId, String username) {
        String key = ONLINE_USER_KEY + userId;
        Map<String, Object> onlineInfo = new HashMap<>();
        onlineInfo.put("userId", userId);
        onlineInfo.put("username", username);
        onlineInfo.put("loginTime", System.currentTimeMillis());
        onlineInfo.put("lastActiveTime", System.currentTimeMillis());

        redisTemplate.opsForHash().putAll(key, onlineInfo);
        redisTemplate.expire(key, ONLINE_TIMEOUT_MINUTES, TimeUnit.MINUTES);

        // 添加到在线用户集合
        redisTemplate.opsForSet().add(ONLINE_USERS_SET_KEY, userId.toString());
    }

    /**
     * 用户下线
     */
    public void userOffline(Long userId) {
        String key = ONLINE_USER_KEY + userId;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(ONLINE_USERS_SET_KEY, userId.toString());
    }

    /**
     * 刷新用户活跃时间
     */
    public void refreshActiveTime(Long userId) {
        String key = ONLINE_USER_KEY + userId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForHash().put(key, "lastActiveTime", System.currentTimeMillis());
            redisTemplate.expire(key, ONLINE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        }
    }

    /**
     * 检查用户是否在线
     */
    public boolean isOnline(Long userId) {
        String key = ONLINE_USER_KEY + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 获取所有在线用户ID列表
     */
    public Set<Long> getOnlineUserIds() {
        Set<Object> members = redisTemplate.opsForSet().members(ONLINE_USERS_SET_KEY);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }

        // 验证并清理无效的在线用户
        Set<Long> validOnlineUsers = new HashSet<>();
        for (Object member : members) {
            Long userId = Long.parseLong(member.toString());
            if (isOnline(userId)) {
                validOnlineUsers.add(userId);
            } else {
                // 清理无效的用户
                redisTemplate.opsForSet().remove(ONLINE_USERS_SET_KEY, member);
            }
        }
        return validOnlineUsers;
    }

    /**
     * 获取在线用户数量
     */
    public long getOnlineCount() {
        return getOnlineUserIds().size();
    }

    /**
     * 批量检查用户在线状态
     */
    public Map<Long, Boolean> batchCheckOnline(List<Long> userIds) {
        Map<Long, Boolean> result = new HashMap<>();
        Set<Long> onlineUserIds = getOnlineUserIds();
        for (Long userId : userIds) {
            result.put(userId, onlineUserIds.contains(userId));
        }
        return result;
    }
}
