package com.ankai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ankai.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
