package com.ankai.controller;

import com.ankai.common.Result;
import com.ankai.dto.RoleCreateRequest;
import com.ankai.dto.RoleEditRequest;
import com.ankai.dto.RolePermissionRequest;
import com.ankai.entity.Role;
import com.ankai.service.RoleService;
import com.ankai.utils.LogUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理Controller
 *
 * @author AnKai
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/role")
@Tag(name = "角色管理", description = "角色的增删改查、权限分配等接口")
public class RoleController {

    private static final Logger logger = LogUtil.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询角色列表")
    public Result<IPage<Role>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) Integer status) {
        LogUtil.info(logger, "分页查询角色列表: current={}, size={}", current, size);
        IPage<Role> page = roleService.getRolePage(current, size, roleName, roleCode, status);
        return Result.success(page);
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询角色")
    public Result<Role> getById(@PathVariable Long id) {
        LogUtil.info(logger, "查询角色: id={}", id);
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @Operation(summary = "新增角色")
    public Result<Boolean> create(@Valid @RequestBody RoleCreateRequest request) {
        LogUtil.info(logger, "新增角色: {}", request.getRoleName());

        Role role = new Role();
        BeanUtils.copyProperties(request, role);

        boolean result = roleService.createRole(role);
        return result ? Result.success(result) : Result.error("新增失败");
    }

    /**
     * 更新角色
     */
    @PutMapping
    @Operation(summary = "更新角色")
    public Result<Boolean> update(@Valid @RequestBody RoleEditRequest request) {
        LogUtil.info(logger, "更新角色: id={}", request.getId());

        Role role = new Role();
        BeanUtils.copyProperties(request, role);

        boolean result = roleService.updateRole(role);
        return result ? Result.success(result) : Result.error("更新失败");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public Result<Boolean> delete(@PathVariable Long id) {
        LogUtil.info(logger, "删除角色: id={}", id);
        boolean result = roleService.deleteRole(id);
        return result ? Result.success(result) : Result.error("删除失败");
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除角色")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        LogUtil.info(logger, "批量删除角色: ids={}", ids);
        boolean result = roleService.batchDeleteRole(ids);
        return result ? Result.success(result) : Result.error("批量删除失败");
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/assign-permissions")
    @Operation(summary = "为角色分配权限")
    public Result<Boolean> assignPermissions(@Valid @RequestBody RolePermissionRequest request) {
        LogUtil.info(logger, "为角色分配权限: roleId={}, permissionIds={}", 
                request.getRoleId(), request.getPermissionIds());
        boolean result = roleService.assignPermissions(request.getRoleId(), request.getPermissionIds());
        return result ? Result.success(result) : Result.error("分配权限失败");
    }

    /**
     * 获取角色的权限ID列表
     */
    @GetMapping("/{id}/permissions")
    @Operation(summary = "获取角色的权限ID列表")
    public Result<List<Long>> getRolePermissions(@PathVariable Long id) {
        LogUtil.info(logger, "获取角色权限: roleId={}", id);
        List<Long> permissionIds = roleService.getRolePermissionIds(id);
        return Result.success(permissionIds);
    }

    /**
     * 查询所有角色
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有角色")
    public Result<List<Role>> list() {
        LogUtil.info(logger, "查询所有角色");
        List<Role> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
}

