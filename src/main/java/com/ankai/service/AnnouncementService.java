package com.ankai.service;

import com.ankai.entity.Announcement;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 系统公告Service接口
 *
 * @author AnKai
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 获取用户可见的公告列表
     */
    List<Announcement> getVisibleAnnouncements(Long userId, int limit);

    /**
     * 发布公告
     */
    boolean publish(Long announcementId, Long userId, List<Long> targetUserIds);

    /**
     * 撤回公告
     */
    boolean withdraw(Long announcementId);

    /**
     * 标记公告已读
     */
    boolean markAsRead(Long announcementId, Long userId);

    /**
     * 统计用户未读公告数量
     */
    int countUnreadAnnouncements(Long userId);
}

