package com.uiineed.todo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uiineed.todo.common.ApiResult;
import com.uiineed.todo.common.ResultCode;
import com.uiineed.todo.entity.Todo;
import com.uiineed.todo.service.TodoService;
import com.uiineed.todo.vo.TodoCreateRequest;
import com.uiineed.todo.vo.TodoUpdateRequest;
import com.uiineed.todo.vo.TodoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 待办事项控制器
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/todos")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class TodoController {

    @Autowired
    private TodoService todoService;

    /**
     * 获取待办事项列表
     *
     * @param status 状态过滤
     * @param page 页码
     * @param size 每页大小
     * @return 待办事项列表
     */
    @GetMapping
    public ApiResult<IPage<TodoResponse>> getTodos(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {

        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();

            // 创建分页对象
            Page<Todo> pageParam = new Page<>(page, size);

            // 查询待办事项
            IPage<Todo> todoPage = todoService.getTodosByUserId(userId, status, pageParam);

            // 转换为响应对象
            IPage<TodoResponse> responsePage = todoPage.convert(this::convertToResponse);

            return ApiResult.success(responsePage);
        } catch (Exception e) {
            log.error("获取待办事项列表失败", e);
            return ApiResult.error("获取待办事项列表失败");
        }
    }

    /**
     * 获取单个待办事项
     *
     * @param id 待办事项ID
     * @return 待办事项详情
     */
    @GetMapping("/{id}")
    public ApiResult<TodoResponse> getTodo(@PathVariable @NotNull Long id) {
        try {
            Long userId = getCurrentUserId();
            Todo todo = todoService.getTodoByIdAndUserId(id, userId);

            if (todo == null) {
                return ApiResult.failed(ResultCode.TODO_NOT_FOUND);
            }

            return ApiResult.success(convertToResponse(todo));
        } catch (Exception e) {
            log.error("获取待办事项失败: id={}", id, e);
            return ApiResult.error("获取待办事项失败");
        }
    }

    /**
     * 创建待办事项
     *
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping
    public ApiResult<TodoResponse> createTodo(@Valid @RequestBody TodoCreateRequest request) {
        try {
            Long userId = getCurrentUserId();

            Todo todo = new Todo();
            todo.setUserId(userId);
            todo.setTitle(request.getTitle());
            todo.setDescription(request.getDescription());
            todo.setPriority(request.getPriority());
            todo.setDueDate(request.getDueDate());
            todo.setReminderTime(request.getReminderTime());
            todo.setCategoryId(request.getCategoryId());
            todo.setStatus(Todo.Status.TODO);
            todo.setIsDeleted(0);
            todo.setDeleted(0);

            Todo createdTodo = todoService.createTodo(todo);
            return ApiResult.success("创建成功", convertToResponse(createdTodo));
        } catch (Exception e) {
            log.error("创建待办事项失败", e);
            return ApiResult.error("创建待办事项失败");
        }
    }

    /**
     * 更新待办事项
     *
     * @param id 待办事项ID
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<TodoResponse> updateTodo(
            @PathVariable @NotNull Long id,
            @Valid @RequestBody TodoUpdateRequest request) {

        try {
            Long userId = getCurrentUserId();

            // 检查待办事项是否存在且属于当前用户
            Todo existTodo = todoService.getTodoByIdAndUserId(id, userId);
            if (existTodo == null) {
                return ApiResult.failed(ResultCode.TODO_NOT_FOUND);
            }

            // 更新待办事项
            Todo updateTodo = new Todo();
            updateTodo.setId(id);
            updateTodo.setTitle(request.getTitle());
            updateTodo.setDescription(request.getDescription());
            updateTodo.setPriority(request.getPriority());
            updateTodo.setDueDate(request.getDueDate());
            updateTodo.setReminderTime(request.getReminderTime());
            updateTodo.setCategoryId(request.getCategoryId());
            updateTodo.setStatus(request.getStatus());

            Todo updatedTodo = todoService.updateTodo(updateTodo);
            return ApiResult.success("更新成功", convertToResponse(updatedTodo));
        } catch (Exception e) {
            log.error("更新待办事项失败: id={}", id, e);
            return ApiResult.error("更新待办事项失败");
        }
    }

    /**
     * 标记待办事项为完成
     *
     * @param id 待办事项ID
     * @return 操作结果
     */
    @PutMapping("/{id}/complete")
    public ApiResult<Void> completeTodo(@PathVariable @NotNull Long id) {
        try {
            Long userId = getCurrentUserId();

            Todo todo = todoService.getTodoByIdAndUserId(id, userId);
            if (todo == null) {
                return ApiResult.failed(ResultCode.TODO_NOT_FOUND);
            }

            todoService.markAsCompleted(id);
            return ApiResult.success("标记完成成功");
        } catch (Exception e) {
            log.error("标记待办事项完成失败: id={}", id, e);
            return ApiResult.error("标记完成失败");
        }
    }

    /**
     * 标记待办事项为未完成
     *
     * @param id 待办事项ID
     * @return 操作结果
     */
    @PutMapping("/{id}/uncomplete")
    public ApiResult<Void> uncompleteTodo(@PathVariable @NotNull Long id) {
        try {
            Long userId = getCurrentUserId();

            Todo todo = todoService.getTodoByIdAndUserId(id, userId);
            if (todo == null) {
                return ApiResult.failed(ResultCode.TODO_NOT_FOUND);
            }

            todoService.markAsUncompleted(id);
            return ApiResult.success("标记未完成成功");
        } catch (Exception e) {
            log.error("标记待办事项未完成失败: id={}", id, e);
            return ApiResult.error("标记未完成失败");
        }
    }

    /**
     * 删除待办事项（软删除）
     *
     * @param id 待办事项ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteTodo(@PathVariable @NotNull Long id) {
        try {
            Long userId = getCurrentUserId();

            Todo todo = todoService.getTodoByIdAndUserId(id, userId);
            if (todo == null) {
                return ApiResult.failed(ResultCode.TODO_NOT_FOUND);
            }

            todoService.softDeleteTodo(id);
            return ApiResult.success("删除成功");
        } catch (Exception e) {
            log.error("删除待办事项失败: id={}", id, e);
            return ApiResult.error("删除失败");
        }
    }

    /**
     * 恢复已删除的待办事项
     *
     * @param id 待办事项ID
     * @return 操作结果
     */
    @PutMapping("/{id}/restore")
    public ApiResult<TodoResponse> restoreTodo(@PathVariable @NotNull Long id) {
        try {
            Long userId = getCurrentUserId();

            Todo todo = todoService.getDeletedTodoByIdAndUserId(id, userId);
            if (todo == null) {
                return ApiResult.failed(ResultCode.TODO_NOT_FOUND);
            }

            Todo restoredTodo = todoService.restoreTodo(id);
            return ApiResult.success("恢复成功", convertToResponse(restoredTodo));
        } catch (Exception e) {
            log.error("恢复待办事项失败: id={}", id, e);
            return ApiResult.error("恢复失败");
        }
    }

    /**
     * 批量操作待办事项
     *
     * @param action 操作类型
     * @param ids 待办事项ID列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    public ApiResult<Void> batchOperation(
            @RequestParam String action,
            @RequestParam List<Long> ids) {

        try {
            Long userId = getCurrentUserId();

            switch (action) {
                case "complete":
                    todoService.batchMarkAsCompleted(ids, userId);
                    return ApiResult.success("批量标记完成成功");
                case "uncomplete":
                    todoService.batchMarkAsUncompleted(ids, userId);
                    return ApiResult.success("批量标记未完成成功");
                case "delete":
                    todoService.batchSoftDelete(ids, userId);
                    return ApiResult.success("批量删除成功");
                default:
                    return ApiResult.validateFailed("不支持的操作类型");
            }
        } catch (Exception e) {
            log.error("批量操作待办事项失败: action={}, ids={}", action, ids, e);
            return ApiResult.error("批量操作失败");
        }
    }

    /**
     * 获取回收站待办事项
     *
     * @param page 页码
     * @param size 每页大小
     * @return 回收站待办事项列表
     */
    @GetMapping("/trash")
    public ApiResult<IPage<TodoResponse>> getTrashTodos(
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) Integer size) {

        try {
            Long userId = getCurrentUserId();
            Page<Todo> pageParam = new Page<>(page, size);
            IPage<Todo> todoPage = todoService.getDeletedTodosByUserId(userId, pageParam);
            IPage<TodoResponse> responsePage = todoPage.convert(this::convertToResponse);
            return ApiResult.success(responsePage);
        } catch (Exception e) {
            log.error("获取回收站待办事项失败", e);
            return ApiResult.error("获取回收站待办事项失败");
        }
    }

    /**
     * 转换为响应对象
     *
     * @param todo 待办事项实体
     * @return 响应对象
     */
    private TodoResponse convertToResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        BeanUtils.copyProperties(todo, response);
        return response;
    }

    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
}