package com.uiineed.todo.common;

/**
 * 统一响应状态码
 *
 * @author Uiineed
 * @version 1.0.0
 */
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 参数错误
     */
    VALIDATE_FAILED(400, "参数校验失败"),
    PARAM_MISSING(401, "缺少必要参数"),
    PARAM_FORMAT_ERROR(402, "参数格式错误"),

    /**
     * 认证相关
     */
    UNAUTHORIZED(401, "未认证或token已过期"),
    TOKEN_INVALID(402, "无效的token"),
    TOKEN_EXPIRED(403, "token已过期"),
    FORBIDDEN(403, "无权限访问"),

    /**
     * 业务相关
     */
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    TODO_NOT_FOUND(2001, "待办事项不存在"),
    CATEGORY_NOT_FOUND(2002, "分类不存在"),
    CATEGORY_HAS_TODOS(2003, "分类下有待办事项，无法删除"),

    /**
     * 微信相关
     */
    WECHAT_AUTH_FAILED(3001, "微信授权失败"),
    WECHAT_GET_ACCESS_TOKEN_FAILED(3002, "获取微信Access Token失败"),
    WECHAT_GET_USER_INFO_FAILED(3003, "获取微信用户信息失败"),
    WECHAT_CODE_INVALID(3004, "微信授权码无效或已过期"),

    /**
     * 系统错误
     */
    SYSTEM_ERROR(500, "系统异常"),
    DATABASE_ERROR(5001, "数据库操作异常"),
    NETWORK_ERROR(5002, "网络异常"),
    FILE_TOO_LARGE(5003, "上传文件过大"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据code获取ResultCode
     *
     * @param code 状态码
     * @return ResultCode
     */
    public static ResultCode getByCode(Integer code) {
        for (ResultCode resultCode : values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode;
            }
        }
        return SYSTEM_ERROR;
    }
}