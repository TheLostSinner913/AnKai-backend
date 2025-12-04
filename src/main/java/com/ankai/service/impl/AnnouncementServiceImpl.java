package com.ankai.service.impl;

import com.ankai.dto.SseEventData;
import com.ankai.entity.Announcement;
import com.ankai.entity.AnnouncementUser;
import com.ankai.mapper.AnnouncementMapper;
import com.ankai.mapper.AnnouncementUserMapper;
import com.ankai.service.AnnouncementService;
import com.ankai.service.SseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 系统公告Service实现
 *
 * @author AnKai
 */
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {

    private final AnnouncementUserMapper announcementUserMapper;
    private final SseService sseService;

    @Override
    public List<Announcement> getVisibleAnnouncements(Long userId, int limit) {
        return baseMapper.selectVisibleAnnouncements(userId, limit);
    }

    @Override
    @Transactional
    public boolean publish(Long announcementId, Long userId, List<Long> targetUserIds) {
        // 更新公告状态
        boolean updated = lambdaUpdate()
                .eq(Announcement::getId, announcementId)
                .set(Announcement::getStatus, 1)
                .set(Announcement::getPublishTime, LocalDateTime.now())
                .update();

        if (!updated) {
            return false;
        }

        // 获取公告信息用于推送
        Announcement announcement = getById(announcementId);

        // 如果是指定用户发布，插入关联记录并推送
        if (targetUserIds != null && !targetUserIds.isEmpty()) {
            if (announcement != null && announcement.getTargetType() == 2) {
                for (Long targetUserId : targetUserIds) {
                    AnnouncementUser au = new AnnouncementUser();
                    au.setAnnouncementId(announcementId);
                    au.setUserId(targetUserId);
                    au.setIsRead(0);
                    announcementUserMapper.insert(au);
                }
                // 推送给指定用户
                pushAnnouncementToUsers(announcement, targetUserIds);
            }
        } else {
            // 全员公告，广播给所有在线用户
            if (announcement != null && announcement.getTargetType() == 1) {
                pushAnnouncementToAll(announcement);
            }
        }

        return true;
    }

    /**
     * 推送公告给指定用户
     */
    private void pushAnnouncementToUsers(Announcement announcement, List<Long> userIds) {
        SseEventData eventData = buildAnnouncementEventData(announcement);
        for (Long targetUserId : userIds) {
            sseService.sendToUser(targetUserId, "announcement", eventData);
        }
    }

    /**
     * 广播公告给所有用户
     */
    private void pushAnnouncementToAll(Announcement announcement) {
        SseEventData eventData = buildAnnouncementEventData(announcement);
        sseService.sendToAll("announcement", eventData);
    }

    /**
     * 构建公告推送数据
     */
    private SseEventData buildAnnouncementEventData(Announcement announcement) {
        SseEventData eventData = new SseEventData();
        eventData.setType("new_announcement");
        eventData.setMessage("您有一条新公告");
        eventData.setData(Map.of(
                "announcementId", announcement.getId(),
                "title", announcement.getTitle(),
                "announcementType", announcement.getAnnouncementType(),
                "content", announcement.getContent().length() > 100
                        ? announcement.getContent().substring(0, 100) + "..."
                        : announcement.getContent()));
        return eventData;
    }

    @Override
    public boolean withdraw(Long announcementId) {
        return lambdaUpdate()
                .eq(Announcement::getId, announcementId)
                .set(Announcement::getStatus, 2) // 已撤回
                .update();
    }

    @Override
    public boolean markAsRead(Long announcementId, Long userId) {
        // 增加浏览次数
        baseMapper.incrementViewCount(announcementId);

        // 查找或创建用户阅读记录
        AnnouncementUser existing = announcementUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AnnouncementUser>()
                        .eq(AnnouncementUser::getAnnouncementId, announcementId)
                        .eq(AnnouncementUser::getUserId, userId));

        if (existing != null) {
            if (existing.getIsRead() == 0) {
                existing.setIsRead(1);
                existing.setReadTime(LocalDateTime.now());
                return announcementUserMapper.updateById(existing) > 0;
            }
            return true;
        } else {
            // 全员公告，首次阅读创建记录
            AnnouncementUser au = new AnnouncementUser();
            au.setAnnouncementId(announcementId);
            au.setUserId(userId);
            au.setIsRead(1);
            au.setReadTime(LocalDateTime.now());
            return announcementUserMapper.insert(au) > 0;
        }
    }

    @Override
    public int countUnreadAnnouncements(Long userId) {
        // 统计用户未读的可见公告数
        List<Announcement> visible = getVisibleAnnouncements(userId, 100);
        int unread = 0;
        for (Announcement a : visible) {
            AnnouncementUser au = announcementUserMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AnnouncementUser>()
                            .eq(AnnouncementUser::getAnnouncementId, a.getId())
                            .eq(AnnouncementUser::getUserId, userId)
                            .eq(AnnouncementUser::getIsRead, 1));
            if (au == null) {
                unread++;
            }
        }
        return unread;
    }
}
