package com.uiineed.todo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uiineed.todo.entity.Todo;
import com.uiineed.todo.mapper.TodoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 待办事项服务类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Slf4j
@Service
@Transactional
public class TodoService {

    @Autowired
    private TodoMapper todoMapper;

    /**
     * 根据用户ID获取待办事项列表
     *
     * @param userId 用户ID
     * @param status 状态过滤（可选）
     * @param page 分页参数
     * @return 待办事项分页列表
     */
    public IPage<Todo> getTodosByUserId(Long userId, Integer status, Page<Todo> page) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("deleted", 0)
                   .eq("is_deleted", 0)
                   .orderByAsc("sort_order")
                   .orderByDesc("created_at");

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        return todoMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID和用户ID获取待办事项
     *
     * @param id 待办事项ID
     * @param userId 用户ID
     * @return 待办事项
     */
    public Todo getTodoByIdAndUserId(Long id, Long userId) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                   .eq("user_id", userId)
                   .eq("deleted", 0)
                   .eq("is_deleted", 0);

        return todoMapper.selectOne(queryWrapper);
    }

    /**
     * 获取已删除的待办事项
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 已删除的待办事项分页列表
     */
    public IPage<Todo> getDeletedTodosByUserId(Long userId, Page<Todo> page) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("deleted", 1)
                   .orderByDesc("deleted_at");

        return todoMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID和用户ID获取已删除的待办事项
     *
     * @param id 待办事项ID
     * @param userId 用户ID
     * @return 已删除的待办事项
     */
    public Todo getDeletedTodoByIdAndUserId(Long id, Long userId) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                   .eq("user_id", userId)
                   .eq("deleted", 1);

        return todoMapper.selectOne(queryWrapper);
    }

    /**
     * 创建待办事项
     *
     * @param todo 待办事项
     * @return 创建的待办事项
     */
    public Todo createTodo(Todo todo) {
        // 设置排序序号（如果有其他待办事项，则排在最后）
        if (todo.getSortOrder() == null || todo.getSortOrder() == 0) {
            QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", todo.getUserId())
                       .eq("deleted", 0)
                       .select("MAX(sort_order) as sort_order");

            Todo maxSortTodo = todoMapper.selectOne(queryWrapper);
            todo.setSortOrder(maxSortTodo != null ? maxSortTodo.getSortOrder() + 1 : 1);
        }

        todoMapper.insert(todo);
        log.info("创建待办事项成功: id={}, userId={}, title={}", todo.getId(), todo.getUserId(), todo.getTitle());
        return todo;
    }

    /**
     * 更新待办事项
     *
     * @param todo 待办事项
     * @return 更新后的待办事项
     */
    public Todo updateTodo(Todo todo) {
        todoMapper.updateById(todo);
        log.info("更新待办事项成功: id={}, userId={}, title={}", todo.getId(), todo.getUserId(), todo.getTitle());

        // 返回更新后的数据
        return todoMapper.selectById(todo.getId());
    }

    /**
     * 标记待办事项为完成
     *
     * @param id 待办事项ID
     */
    public void markAsCompleted(Long id) {
        Todo updateTodo = new Todo();
        updateTodo.setId(id);
        updateTodo.setStatus(Todo.Status.COMPLETED);
        updateTodo.setCompletedAt(LocalDateTime.now());

        todoMapper.updateById(updateTodo);
        log.info("标记待办事项完成: id={}", id);
    }

    /**
     * 标记待办事项为未完成
     *
     * @param id 待办事项ID
     */
    public void markAsUncompleted(Long id) {
        Todo updateTodo = new Todo();
        updateTodo.setId(id);
        updateTodo.setStatus(Todo.Status.TODO);
        updateTodo.setCompletedAt(null);

        todoMapper.updateById(updateTodo);
        log.info("标记待办事项未完成: id={}", id);
    }

    /**
     * 软删除待办事项
     *
     * @param id 待办事项ID
     */
    public void softDeleteTodo(Long id) {
        Todo updateTodo = new Todo();
        updateTodo.setId(id);
        updateTodo.setIsDeleted(1);
        updateTodo.setDeletedAt(LocalDateTime.now());
        updateTodo.setDeleted(1);

        todoMapper.updateById(updateTodo);
        log.info("软删除待办事项: id={}", id);
    }

    /**
     * 恢复已删除的待办事项
     *
     * @param id 待办事项ID
     * @return 恢复后的待办事项
     */
    public Todo restoreTodo(Long id) {
        Todo updateTodo = new Todo();
        updateTodo.setId(id);
        updateTodo.setIsDeleted(0);
        updateTodo.setDeletedAt(null);
        updateTodo.setDeleted(0);

        todoMapper.updateById(updateTodo);
        log.info("恢复待办事项: id={}", id);

        return todoMapper.selectById(id);
    }

    /**
     * 批量标记为完成
     *
     * @param ids 待办事项ID列表
     * @param userId 用户ID
     */
    public void batchMarkAsCompleted(List<Long> ids, Long userId) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                   .eq("user_id", userId)
                   .eq("deleted", 0)
                   .eq("is_deleted", 0);

        Todo updateTodo = new Todo();
        updateTodo.setStatus(Todo.Status.COMPLETED);
        updateTodo.setCompletedAt(LocalDateTime.now());

        todoMapper.update(updateTodo, queryWrapper);
        log.info("批量标记待办事项完成: ids={}, userId={}", ids, userId);
    }

    /**
     * 批量标记为未完成
     *
     * @param ids 待办事项ID列表
     * @param userId 用户ID
     */
    public void batchMarkAsUncompleted(List<Long> ids, Long userId) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                   .eq("user_id", userId)
                   .eq("deleted", 0)
                   .eq("is_deleted", 0);

        Todo updateTodo = new Todo();
        updateTodo.setStatus(Todo.Status.TODO);
        updateTodo.setCompletedAt(null);

        todoMapper.update(updateTodo, queryWrapper);
        log.info("批量标记待办事项未完成: ids={}, userId={}", ids, userId);
    }

    /**
     * 批量软删除
     *
     * @param ids 待办事项ID列表
     * @param userId 用户ID
     */
    public void batchSoftDelete(List<Long> ids, Long userId) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                   .eq("user_id", userId)
                   .eq("deleted", 0)
                   .eq("is_deleted", 0);

        Todo updateTodo = new Todo();
        updateTodo.setIsDeleted(1);
        updateTodo.setDeletedAt(LocalDateTime.now());
        updateTodo.setDeleted(1);

        todoMapper.update(updateTodo, queryWrapper);
        log.info("批量软删除待办事项: ids={}, userId={}", ids, userId);
    }

    /**
     * 清空回收站（永久删除用户的已删除待办事项）
     *
     * @param userId 用户ID
     */
    public void emptyTrash(Long userId) {
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("deleted", 1);

        todoMapper.delete(queryWrapper);
        log.info("清空回收站: userId={}", userId);
    }
}