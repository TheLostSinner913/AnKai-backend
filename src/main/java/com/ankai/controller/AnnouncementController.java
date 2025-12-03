package com.ankai.controller;

import com.ankai.common.Result;
import com.ankai.entity.Announcement;
import com.ankai.service.AnnouncementService;
import com.ankai.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统公告控制器
 *
 * @author AnKai
 */
@RestController
@RequestMapping("/announcement")
@RequiredArgsConstructor
@Tag(name = "系统公告管理")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/visible")
    @Operation(summary = "获取用户可见的公告列表")
    public Result<List<Announcement>> getVisibleAnnouncements(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(announcementService.getVisibleAnnouncements(userId, limit));
    }

    @GetMapping("/unread/count")
    @Operation(summary = "获取未读公告数量")
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(announcementService.countUnreadAnnouncements(userId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记公告已读")
    public Result<Boolean> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(announcementService.markAsRead(id, userId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询公告（管理端）")
    public Result<IPage<Announcement>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        Page<Announcement> page = new Page<>(current, size);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(title != null, Announcement::getTitle, title)
                .eq(status != null, Announcement::getStatus, status)
                .orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);
        return Result.success(announcementService.page(page, wrapper));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取公告详情")
    public Result<Announcement> getById(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增公告")
    public Result<Boolean> add(@RequestBody Announcement announcement) {
        Long userId = SecurityUtils.getCurrentUserId();
        announcement.setCreateBy(userId);
        announcement.setStatus(0); // 草稿
        announcement.setViewCount(0);
        return Result.success(announcementService.save(announcement));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新公告")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        Long userId = SecurityUtils.getCurrentUserId();
        announcement.setId(id);
        announcement.setUpdateBy(userId);
        return Result.success(announcementService.updateById(announcement));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除公告")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(announcementService.removeById(id));
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "发布公告")
    public Result<Boolean> publish(@PathVariable Long id,
            @RequestBody(required = false) Map<String, List<Long>> body) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> targetUserIds = body != null ? body.get("targetUserIds") : null;
        return Result.success(announcementService.publish(id, userId, targetUserIds));
    }

    @PutMapping("/{id}/withdraw")
    @Operation(summary = "撤回公告")
    public Result<Boolean> withdraw(@PathVariable Long id) {
        return Result.success(announcementService.withdraw(id));
    }
}
