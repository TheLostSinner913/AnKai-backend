package com.ankai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ankai.common.PageRequest;
import com.ankai.common.Result;
import com.ankai.dto.UserCreateRequest;
import com.ankai.dto.UserEditRequest;
import com.ankai.dto.UserResponse;
import com.ankai.dto.UserRoleRequest;
import com.ankai.dto.UserWithRolesDTO;
import com.ankai.entity.User;
import com.ankai.utils.JwtUtil;
import com.ankai.service.OnlineUserService;
import com.ankai.service.UserService;
import com.ankai.utils.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户Controller
 *
 * @author AnKai
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private static final Logger logger = LogUtil.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    public Result<Page<User>> page(@Valid @RequestBody PageRequest pageRequest) {
        Page<User> page = userService.page(pageRequest);
        return Result.success(page);
    }

    /**
     * 分页查询用户（包含角色信息）
     */
    @PostMapping("/page-with-roles")
    @Operation(summary = "分页查询用户（包含角色信息）")
    public Result<Page<UserWithRolesDTO>> pageWithRoles(@Valid @RequestBody PageRequest pageRequest) {
        Page<UserWithRolesDTO> page = userService.pageWithRoles(pageRequest);
        return Result.success(page);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    public Result<UserResponse> getById(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 转换为响应DTO
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        response.setHasPassword(StringUtils.hasText(user.getPassword()));

        return Result.success(response);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @Operation(summary = "新增用户")
    public Result<Boolean> save(@Valid @RequestBody UserCreateRequest request) {
        LogUtil.info(logger, "新增用户请求: {}", request.getUsername());

        try {
            // 检查用户名是否已存在（只检查未删除的用户，MyBatis Plus 会自动添加 deleted=0 条件）
            User existingUser = userService.lambdaQuery()
                    .eq(User::getUsername, request.getUsername())
                    .one();
            if (existingUser != null) {
                return Result.error("用户名 '" + request.getUsername() + "' 已存在");
            }

            // 转换DTO到实体
            User user = new User();
            BeanUtils.copyProperties(request, user);

            boolean result = userService.save(user);
            LogUtil.info(logger, "新增用户 {} {}", request.getUsername(), result ? "成功" : "失败");
            return result ? Result.success(result) : Result.error("新增失败");
        } catch (Exception e) {
            LogUtil.error(logger, "新增用户失败: {}", e.getMessage());
            // 处理重复键异常
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                return Result.error("用户名已存在，请使用其他用户名");
            }
            return Result.error("新增用户失败，请稍后重试");
        }
    }

    /**
     * 更新用户
     */
    @PutMapping
    @Operation(summary = "更新用户")
    public Result<Boolean> update(@Valid @RequestBody UserEditRequest request) {
        LogUtil.info(logger, "更新用户请求: ID={}, 用户名={}", request.getId(), request.getUsername());

        try {
            // 转换DTO到实体
            User user = new User();
            BeanUtils.copyProperties(request, user);

            boolean result = userService.updateById(user);
            LogUtil.info(logger, "更新用户 {} {}", request.getUsername(), result ? "成功" : "失败");
            return result ? Result.success(result) : Result.error("更新失败");
        } catch (Exception e) {
            LogUtil.error(logger, "更新用户失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Boolean> delete(@Parameter(description = "用户ID") @PathVariable Long id) {
        boolean result = userService.removeById(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户")
    public Result<Boolean> deleteBatch(@RequestBody List<Long> ids) {
        boolean result = userService.removeByIds(ids);
        return result ? Result.success(result) : Result.error("批量删除失败");
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有用户")
    public Result<List<User>> list() {
        List<User> list = userService.list();
        return Result.success(list);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assign-roles")
    @Operation(summary = "为用户分配角色")
    public Result<Boolean> assignRoles(@Valid @RequestBody UserRoleRequest request) {
        LogUtil.info(logger, "为用户分配角色: userId={}, roleIds={}",
                request.getUserId(), request.getRoleIds());
        boolean result = userService.assignRoles(request.getUserId(), request.getRoleIds());
        return result ? Result.success(result) : Result.error("分配角色失败");
    }

    /**
     * 获取用户的角色ID列表
     */
    @GetMapping("/{id}/roles")
    @Operation(summary = "获取用户的角色ID列表")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        LogUtil.info(logger, "获取用户角色: userId={}", id);
        List<Long> roleIds = userService.getUserRoleIds(id);
        return Result.success(roleIds);
    }

    /**
     * 获取在线用户ID列表
     */
    @GetMapping("/online")
    @Operation(summary = "获取在线用户ID列表")
    public Result<Set<Long>> getOnlineUsers() {
        Set<Long> onlineUserIds = onlineUserService.getOnlineUserIds();
        return Result.success(onlineUserIds);
    }

    /**
     * 批量检查用户在线状态
     */
    @PostMapping("/online/check")
    @Operation(summary = "批量检查用户在线状态")
    public Result<Map<Long, Boolean>> checkOnlineStatus(@RequestBody List<Long> userIds) {
        Map<Long, Boolean> onlineStatus = onlineUserService.batchCheckOnline(userIds);
        return Result.success(onlineStatus);
    }

    /**
     * 获取在线用户数量
     */
    @GetMapping("/online/count")
    @Operation(summary = "获取在线用户数量")
    public Result<Long> getOnlineCount() {
        long count = onlineUserService.getOnlineCount();
        return Result.success(count);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Boolean> updatePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> request) {

        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return Result.error("密码不能为空");
        }

        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("当前密码错误");
        }

        // 更新密码
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        boolean result = userService.updateById(updateUser);

        return result ? Result.success(true) : Result.error("密码修改失败");
    }
}
