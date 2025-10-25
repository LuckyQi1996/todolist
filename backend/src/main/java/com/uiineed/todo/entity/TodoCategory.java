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
 * 待办事项分类实体类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "todo_categories")
@TableName("todo_categories")
public class TodoCategory {

    /**
     * 分类ID
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
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50个字符")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 分类颜色（十六进制）
     */
    @Size(max = 7, message = "颜色值格式不正确")
    @Column(name = "color", length = 7)
    private String color;

    /**
     * 分类图标
     */
    @Size(max = 20, message = "图标名称长度不能超过20个字符")
    @Column(name = "icon", length = 20)
    private String icon;

    /**
     * 排序序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

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
     * 预定义颜色常量
     */
    public static class Color {
        public static final String DEFAULT = "#33322E";
        public static final String BLUE = "#8CD4CB";
        public static final String YELLOW = "#f8d966";
        public static final String RED = "#F6A89E";
        public static final String PINK = "#ffd6e9";
        public static final String GREEN = "#D0F4F0";
    }

    /**
     * 预定义图标常量
     */
    public static class Icon {
        public static final String FOLDER = "folder";
        public static final String USER = "user";
        public static final String BRIEFCASE = "briefcase";
        public static final String BOOK = "book";
        public static final String HOME = "home";
        public static final String STAR = "star";
        public static final String TAG = "tag";
    }
}