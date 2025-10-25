package com.uiineed.todo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 待办事项实体类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "todos")
@TableName("todos")
public class Todo {

    /**
     * 待办事项ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 分类ID
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * 待办事项标题
     */
    @NotBlank(message = "待办事项标题不能为空")
    @Size(max = 500, message = "标题长度不能超过500个字符")
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    /**
     * 详细描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 优先级：1-低，2-中，3-高
     */
    @Column(name = "priority")
    private Integer priority;

    /**
     * 状态：0-待办，1-进行中，2-已完成，3-已取消
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * 提醒时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "reminder_time")
    private LocalDateTime reminderTime;

    /**
     * 排序序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 是否删除：0-未删除，1-已删除（保持与前端兼容）
     */
    @Column(name = "is_deleted")
    private Integer isDeleted;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableLogic
    @Column(name = "deleted")
    private Integer deleted;

    /**
     * 预定义优先级常量
     */
    public static class Priority {
        public static final int LOW = 1;
        public static final int MEDIUM = 2;
        public static final int HIGH = 3;
    }

    /**
     * 预定义状态常量
     */
    public static class Status {
        public static final int TODO = 0;        // 待办
        public static final int IN_PROGRESS = 1;  // 进行中
        public static final int COMPLETED = 2;    // 已完成
        public static final int CANCELLED = 3;    // 已取消
    }

    /**
     * 兼容前端的完成状态
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return Status.COMPLETED == this.status;
    }

    /**
     * 兼容前端的删除状态
     * @return 是否已删除
     */
    public boolean isRemoved() {
        return (Integer.valueOf(1).equals(this.isDeleted) || Integer.valueOf(1).equals(this.deleted));
    }
}