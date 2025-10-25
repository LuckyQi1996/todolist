package com.uiineed.todo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.uiineed.todo.entity.Todo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问层
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Mapper
public interface TodoMapper extends BaseMapper<Todo> {
}