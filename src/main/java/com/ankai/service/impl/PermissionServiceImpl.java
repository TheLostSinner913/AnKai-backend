package com.ankai.service.impl;

import com.ankai.dto.PermissionTreeNode;
import com.ankai.entity.Permission;
import com.ankai.exception.BusinessException;
import com.ankai.mapper.PermissionMapper;
import com.ankai.service.PermissionService;
import com.ankai.utils.LogUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private static final Logger logger = LogUtil.getLogger(PermissionServiceImpl.class);

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<PermissionTreeNode> getPermissionTree() {
        // 查询所有权限
        List<Permission> allPermissions = getAllPermissions();

        // 转换为树节点
        List<PermissionTreeNode> allNodes = allPermissions.stream()
                .map(this::convertToTreeNode)
                .collect(Collectors.toList());

        // 构建树形结构
        return buildTree(allNodes, 0L);
    }

    /**
     * 构建树形结构
     */
    private List<PermissionTreeNode> buildTree(List<PermissionTreeNode> allNodes, Long parentId) {
        List<PermissionTreeNode> tree = new ArrayList<>();

        for (PermissionTreeNode node : allNodes) {
            if (node.getParentId().equals(parentId)) {
                // 递归查找子节点
                List<PermissionTreeNode> children = buildTree(allNodes, node.getId());
                node.setChildren(children);
                tree.add(node);
            }
        }

        // 按排序字段排序
        tree.sort((a, b) -> {
            if (a.getSortOrder() == null) return 1;
            if (b.getSortOrder() == null) return -1;
            return a.getSortOrder().compareTo(b.getSortOrder());
        });

        return tree;
    }

    /**
     * 转换为树节点
     */
    private PermissionTreeNode convertToTreeNode(Permission permission) {
        PermissionTreeNode node = new PermissionTreeNode();
        BeanUtils.copyProperties(permission, node);
        return node;
    }

    @Override
    public Permission getPermissionById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPermission(Permission permission) {
        // 检查权限编码是否已存在
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permission.getPermissionCode());
        Permission existPermission = permissionMapper.selectOne(wrapper);
        if (existPermission != null) {
            throw BusinessException.of(400, "权限编码已存在");
        }

        LogUtil.info(logger, "创建权限: {}", permission.getPermissionName());
        return permissionMapper.insert(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(Permission permission) {
        // 检查权限是否存在
        Permission existPermission = getPermissionById(permission.getId());
        if (existPermission == null) {
            throw BusinessException.of(404, "权限不存在");
        }

        // 如果修改了权限编码，检查新编码是否已被使用
        if (!existPermission.getPermissionCode().equals(permission.getPermissionCode())) {
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Permission::getPermissionCode, permission.getPermissionCode());
            Permission codeExistPermission = permissionMapper.selectOne(wrapper);
            if (codeExistPermission != null && !codeExistPermission.getId().equals(permission.getId())) {
                throw BusinessException.of(400, "权限编码已存在");
            }
        }

        LogUtil.info(logger, "更新权限: {}", permission.getPermissionName());
        return permissionMapper.updateById(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long id) {
        // 检查权限是否存在
        Permission permission = getPermissionById(id);
        if (permission == null) {
            throw BusinessException.of(404, "权限不存在");
        }

        // 检查是否有子权限
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, id);
        long count = permissionMapper.selectCount(wrapper);
        if (count > 0) {
            throw BusinessException.of(400, "该权限下有子权限，无法删除");
        }

        LogUtil.info(logger, "删除权限: {}", permission.getPermissionName());
        return permissionMapper.deleteById(id) > 0;
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return permissionMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<Permission> getAllPermissions() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSortOrder);
        return permissionMapper.selectList(wrapper);
    }
}

