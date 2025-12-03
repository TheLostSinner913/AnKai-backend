package com.ankai.mapper;

import com.ankai.entity.Announcement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统公告Mapper
 *
 * @author AnKai
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 查询用户可见的公告列表（已发布、未过期、全员或指定用户）
     */
    @Select("SELECT a.*, u.real_name as create_by_name FROM sys_announcement a " +
            "LEFT JOIN sys_announcement_user au ON a.id = au.announcement_id AND au.user_id = #{userId} " +
            "LEFT JOIN sys_user u ON a.create_by = u.id " +
            "WHERE a.deleted = 0 AND a.status = 1 " +
            "AND (a.expire_time IS NULL OR a.expire_time > NOW()) " +
            "AND (a.target_type = 1 OR (a.target_type = 2 AND au.user_id IS NOT NULL)) " +
            "ORDER BY a.is_top DESC, a.top_order DESC, a.publish_time DESC " +
            "LIMIT #{limit}")
    List<Announcement> selectVisibleAnnouncements(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE sys_announcement SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
}
