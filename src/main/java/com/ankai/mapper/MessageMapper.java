package com.ankai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ankai.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 站内信Mapper
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 统计用户未读消息数量
     */
    @Select("SELECT COUNT(*) FROM sys_message WHERE receiver_id = #{userId} AND is_read = 0 AND deleted = 0")
    int countUnreadByUserId(@Param("userId") Long userId);
}

