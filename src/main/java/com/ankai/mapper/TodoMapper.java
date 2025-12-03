package com.ankai.mapper;

import com.ankai.entity.Todo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 待办事项Mapper
 *
 * @author AnKai
 */
@Mapper
public interface TodoMapper extends BaseMapper<Todo> {

    /**
     * 查询用户某月的待办日期列表（用于日历标记）
     */
    @Select("SELECT DISTINCT todo_date FROM sys_todo " +
            "WHERE user_id = #{userId} AND deleted = 0 " +
            "AND todo_date >= #{startDate} AND todo_date <= #{endDate}")
    List<LocalDate> selectTodoDatesByMonth(@Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 统计用户待办数量（只统计待办和进行中，不统计已完成、已取消、已忽略）
     */
    @Select("SELECT COUNT(*) FROM sys_todo WHERE user_id = #{userId} AND status IN (0, 1) AND deleted = 0")
    int countPendingTodos(@Param("userId") Long userId);
}
