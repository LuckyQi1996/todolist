package com.uiineed.todo.exception;

import com.uiineed.todo.common.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final ResultCode resultCode;

    /**
     * 错误信息
     */
    private final String message;

    public BusinessException(ResultCode resultCode) {
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
    }

    public BusinessException(ResultCode resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public BusinessException(String message) {
        this.resultCode = ResultCode.SYSTEM_ERROR;
        this.message = message;
    }
}