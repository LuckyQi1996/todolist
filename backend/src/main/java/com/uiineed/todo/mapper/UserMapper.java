package com.uiineed.todo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uiineed.todo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}