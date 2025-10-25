package com.uiineed.todo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 统一API响应结果
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(ResultCode.SUCCESS.getMessage());
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /**
     * 成功响应（自定义消息）
     *
     * @param message 自定义消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> ApiResult<T> success(String message, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 失败响应
     *
     * @param resultCode 错误码
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ApiResult<T> failed(ResultCode resultCode) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 失败响应（自定义错误码和消息）
     *
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> ApiResult<T> failed(Integer code, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    /**
     * 失败响应（系统异常）
     *
     * @param <T> 数据类型
     * @return 系统异常响应结果
     */
    public static <T> ApiResult<T> error() {
        return failed(ResultCode.SYSTEM_ERROR);
    }

    /**
     * 失败响应（自定义异常消息）
     *
     * @param message 异常消息
     * @param <T> 数据类型
     * @return 异常响应结果
     */
    public static <T> ApiResult<T> error(String message) {
        return failed(ResultCode.SYSTEM_ERROR.getCode(), message);
    }

    /**
     * 参数校验失败响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 参数校验失败响应结果
     */
    public static <T> ApiResult<T> validateFailed(String message) {
        return failed(ResultCode.VALIDATE_FAILED.getCode(), message);
    }
}