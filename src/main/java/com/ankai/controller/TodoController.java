package com.ankai.controller;

import com.ankai.common.Result;
import com.ankai.entity.Todo;
import com.ankai.service.TodoService;
import com.ankai.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 待办事项控制器
 *
 * @author AnKai
 */
@RestController
@RequestMapping("/todo")
@RequiredArgsConstructor
@Tag(name = "待办事项管理")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/date/{date}")
    @Operation(summary = "获取某日的待办列表")
    public Result<List<Todo>> getTodosByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(todoService.getTodosByDate(userId, date));
    }

    @GetMapping("/month")
    @Operation(summary = "获取某月有待办的日期列表")
    public Result<List<LocalDate>> getTodoDatesInMonth(
            @RequestParam int year,
            @RequestParam int month) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(todoService.getTodoDatesInMonth(userId, year, month));
    }

    @GetMapping("/count")
    @Operation(summary = "获取待办数量统计")
    public Result<Map<String, Integer>> getTodoCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        Map<String, Integer> count = new HashMap<>();
        count.put("pending", todoService.countPendingTodos(userId));
        return Result.success(count);
    }

    @PostMapping
    @Operation(summary = "新增待办")
    public Result<Boolean> addTodo(@RequestBody Todo todo) {
        Long userId = SecurityUtils.getCurrentUserId();
        todo.setUserId(userId);
        todo.setTodoType(1); // 个人添加
        todo.setStatus(0); // 待办状态
        if (todo.getPriority() == null) {
            todo.setPriority(2); // 默认中优先级
        }
        if (todo.getColor() == null) {
            todo.setColor("#1890ff"); // 默认蓝色
        }
        return Result.success(todoService.save(todo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新待办")
    public Result<Boolean> updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        Long userId = SecurityUtils.getCurrentUserId();
        // 验证是否是自己的待办
        Todo existing = todoService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.error("无权操作此待办");
        }
        todo.setId(id);
        return Result.success(todoService.updateById(todo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除待办")
    public Result<Boolean> deleteTodo(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Todo existing = todoService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.error("无权操作此待办");
        }
        return Result.success(todoService.removeById(id));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "完成待办")
    public Result<Boolean> completeTodo(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(todoService.completeTodo(id, userId));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消待办")
    public Result<Boolean> cancelTodo(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(todoService.cancelTodo(id, userId));
    }

    @PutMapping("/{id}/ignore")
    @Operation(summary = "忽略待办")
    public Result<Boolean> ignoreTodo(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(todoService.ignoreTodo(id, userId));
    }
}
