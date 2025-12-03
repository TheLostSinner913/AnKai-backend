package com.ankai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ankai.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 *
 * @author AnKai
 * @since 2024-01-01
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户（包含已删除的用户）
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectByUsernameWithDeleted(String username);
}
