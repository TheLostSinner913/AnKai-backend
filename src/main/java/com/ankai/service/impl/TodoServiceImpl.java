package com.ankai.service.impl;

import com.ankai.dto.SseEventData;
import com.ankai.entity.Todo;
import com.ankai.mapper.TodoMapper;
import com.ankai.service.SseService;
import com.ankai.service.TodoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * 待办事项Service实现
 *
 * @author AnKai
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {

    @Autowired
    private SseService sseService;

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

    @Override
    public boolean assignTodo(Long userId, Todo todo) {
        // 设置为系统分配
        todo.setUserId(userId);
        todo.setTodoType(2); // 系统分配
        todo.setStatus(0); // 待办状态
        if (todo.getPriority() == null) {
            todo.setPriority(2); // 默认中优先级
        }
        if (todo.getColor() == null) {
            todo.setColor("#fa541c"); // 系统分配用橙色
        }

        boolean saved = save(todo);

        // 推送给目标用户
        if (saved) {
            pushTodoToUser(userId, todo);
        }

        return saved;
    }

    @Override
    public boolean assignTodoToUsers(List<Long> userIds, Todo todoTemplate) {
        boolean allSuccess = true;
        for (Long userId : userIds) {
            Todo todo = new Todo();
            BeanUtils.copyProperties(todoTemplate, todo);
            todo.setId(null); // 确保是新建
            if (!assignTodo(userId, todo)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    /**
     * 推送待办给用户
     */
    private void pushTodoToUser(Long userId, Todo todo) {
        // 获取用户待办数量
        int pendingCount = countPendingTodos(userId);

        SseEventData eventData = new SseEventData();
        eventData.setType("new_todo");
        eventData.setUnreadCount(pendingCount);
        eventData.setMessage("您有一个新的待办事项");
        eventData.setData(Map.of(
                "todoId", todo.getId(),
                "title", todo.getTitle(),
                "priority", todo.getPriority(),
                "todoDate", todo.getTodoDate().toString(),
                "description", todo.getDescription() != null ? todo.getDescription() : ""));

        sseService.sendToUser(userId, "todo", eventData);
    }
}
