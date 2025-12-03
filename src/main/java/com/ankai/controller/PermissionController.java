package com.ankai.controller;

import com.ankai.common.Result;
import com.ankai.dto.PermissionTreeNode;
import com.ankai.entity.Permission;
import com.ankai.service.PermissionService;
import com.ankai.utils.LogUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理Controller
 *
 * @author AnKai
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/permission")
@Tag(name = "权限管理", description = "权限的增删改查、树形结构等接口")
public class PermissionController {

    private static final Logger logger = LogUtil.getLogger(PermissionController.class);

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取权限树")
    public Result<List<PermissionTreeNode>> tree() {
        LogUtil.info(logger, "获取权限树");
        List<PermissionTreeNode> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }

    /**
     * 查询所有权限
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有权限")
    public Result<List<Permission>> list() {
        LogUtil.info(logger, "查询所有权限");
        List<Permission> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }

    /**
     * 根据ID查询权限
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询权限")
    public Result<Permission> getById(@PathVariable Long id) {
        LogUtil.info(logger, "查询权限: id={}", id);
        Permission permission = permissionService.getPermissionById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    /**
     * 新增权限
     */
    @PostMapping
    @Operation(summary = "新增权限")
    public Result<Boolean> create(@Valid @RequestBody Permission permission) {
        LogUtil.info(logger, "新增权限: {}", permission.getPermissionName());
        boolean result = permissionService.createPermission(permission);
        return result ? Result.success(result) : Result.error("新增失败");
    }

    /**
     * 更新权限
     */
    @PutMapping
    @Operation(summary = "更新权限")
    public Result<Boolean> update(@Valid @RequestBody Permission permission) {
        LogUtil.info(logger, "更新权限: id={}", permission.getId());
        boolean result = permissionService.updatePermission(permission);
        return result ? Result.success(result) : Result.error("更新失败");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public Result<Boolean> delete(@PathVariable Long id) {
        LogUtil.info(logger, "删除权限: id={}", id);
        boolean result = permissionService.deletePermission(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }

    /**
     * 根据角色ID查询权限列表
     */
    @GetMapping("/role/{roleId}")
    @Operation(summary = "根据角色ID查询权限列表")
    public Result<List<Permission>> getByRoleId(@PathVariable Long roleId) {
        LogUtil.info(logger, "查询角色权限: roleId={}", roleId);
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }

    /**
     * 根据用户ID查询权限列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询权限列表")
    public Result<List<Permission>> getByUserId(@PathVariable Long userId) {
        LogUtil.info(logger, "查询用户权限: userId={}", userId);
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        return Result.success(permissions);
    }
}

