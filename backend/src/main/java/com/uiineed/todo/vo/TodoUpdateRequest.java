package com.uiineed.todo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 更新待办事项请求VO
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
public class TodoUpdateRequest {

    /**
     * 待办事项标题
     */
    @Size(min = 1, max = 500, message = "标题长度必须在1-500个字符之间")
    private String title;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 优先级：1-低，2-中，3-高
     */
    @Min(value = 1, message = "优先级最小值为1")
    private Integer priority;

    /**
     * 状态：0-待办，1-进行中，2-已完成，3-已取消
     */
    private Integer status;

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
     * 分类ID
     */
    private Long categoryId;
}