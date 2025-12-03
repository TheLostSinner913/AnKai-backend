package com.ankai.service.impl;

import com.ankai.entity.Todo;
import com.ankai.mapper.TodoMapper;
import com.ankai.service.TodoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * 待办事项Service实现
 *
 * @author AnKai
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {

    @Override
    public List<Todo> getTodosByDate(Long userId, LocalDate date) {
        return lambdaQuery()
                .eq(Todo::getUserId, userId)
                .eq(Todo::getTodoDate, date)
                .orderByDesc(Todo::getPriority)
                .orderByAsc(Todo::getStartTime)
                .list();
    }

    @Override
    public List<LocalDate> getTodoDatesInMonth(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return baseMapper.selectTodoDatesByMonth(userId, startDate, endDate);
    }

    @Override
    public int countPendingTodos(Long userId) {
        return baseMapper.countPendingTodos(userId);
    }

    @Override
    public boolean completeTodo(Long todoId, Long userId) {
        return lambdaUpdate()
                .eq(Todo::getId, todoId)
                .eq(Todo::getUserId, userId)
                .set(Todo::getStatus, 2) // 已完成
                .update();
    }

    @Override
    public boolean cancelTodo(Long todoId, Long userId) {
        return lambdaUpdate()
                .eq(Todo::getId, todoId)
                .eq(Todo::getUserId, userId)
                .set(Todo::getStatus, 3) // 已取消
                .update();
    }

    @Override
    public boolean ignoreTodo(Long todoId, Long userId) {
        return lambdaUpdate()
                .eq(Todo::getId, todoId)
                .eq(Todo::getUserId, userId)
                .set(Todo::getStatus, 4) // 已忽略
                .update();
    }
}
