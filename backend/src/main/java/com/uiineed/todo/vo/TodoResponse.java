package com.uiineed.todo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办事项响应VO
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoResponse {

    /**
     * 待办事项ID
     */
    private Long id;

    /**
     * 待办事项标题
     */
    private String title;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 优先级：1-低，2-中，3-高
     */
    private Integer priority;

    /**
     * 状态：0-待办，1-进行中，2-已完成，3-已取消
     */
    private Integer status;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    /**
     * 截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    /**
     * 提醒时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reminderTime;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 是否完成（兼容前端）
     */
    private Boolean completed;

    /**
     * 是否删除（兼容前端）
     */
    private Boolean removed;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}