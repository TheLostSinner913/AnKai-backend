package com.ankai.service;

import com.ankai.entity.Todo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * 待办事项Service接口
 *
 * @author AnKai
 */
public interface TodoService extends IService<Todo> {

    /**
     * 获取用户某日的待办列表
     */
    List<Todo> getTodosByDate(Long userId, LocalDate date);

    /**
     * 获取用户某月有待办的日期列表
     */
    List<LocalDate> getTodoDatesInMonth(Long userId, int year, int month);

    /**
     * 统计用户待办数量
     */
    int countPendingTodos(Long userId);

    /**
     * 完成待办
     */
    boolean completeTodo(Long todoId, Long userId);

    /**
     * 取消待办
     */
    boolean cancelTodo(Long todoId, Long userId);

    /**
     * 忽略待办
     */
    boolean ignoreTodo(Long todoId, Long userId);

    /**
     * 系统分配待办（会触发SSE推送）
     * 
     * @param userId 目标用户ID
     * @param todo   待办信息
     * @return 是否成功
     */
    boolean assignTodo(Long userId, Todo todo);

    /**
     * 批量系统分配待办（会触发SSE推送）
     * 
     * @param userIds 目标用户ID列表
     * @param todo    待办信息（会为每个用户创建一份）
     * @return 是否成功
     */
    boolean assignTodoToUsers(List<Long> userIds, Todo todo);
}
