package com.uiineed.todo.exception;

import com.uiineed.todo.common.ApiResult;
import com.uiineed.todo.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private Environment environment;

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Object> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage());
        return ApiResult.failed(e.getResultCode());
    }

    /**
     * 处理参数校验异常 - @RequestBody参数校验
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("参数校验失败: {} - {}", request.getRequestURI(), errorMessage);

        return ApiResult.validateFailed(errorMessage);
    }

    /**
     * 处理参数校验异常（约束校验）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Object> handleValidationException(ConstraintViolationException e) {
        StringBuilder message = new StringBuilder("参数校验失败：");
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            message.append(violation.getPropertyPath().toString())
                  .append(" ")
                  .append(violation.getMessage())
                  .append("; ");
        }
        log.error("参数校验异常：{}", message.toString());
        return ApiResult.validateFailed(message.toString());
    }

    /**
     * 处理缺少参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult<Object> handleMissingParamsException(MissingServletRequestParameterException e) {
        log.error("缺少参数异常：{}", e.getMessage());
        return ApiResult.failed(ResultCode.PARAM_MISSING);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<Object> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("参数类型不匹配：{}", e.getMessage());
        return ApiResult.failed(ResultCode.PARAM_FORMAT_ERROR);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ApiResult<Object> handleBindException(BindException e) {
        log.error("参数绑定异常：{}", e.getMessage());
        return ApiResult.failed(ResultCode.PARAM_FORMAT_ERROR);
    }

    /**
     * 处理认证失败异常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResult<Object> handleAuthenticationException(AuthenticationException e) {
        log.error("认证失败：{}", e.getMessage());
        return ApiResult.failed(ResultCode.UNAUTHORIZED);
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResult<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限不足：{}", e.getMessage());
        return ApiResult.failed(ResultCode.FORBIDDEN);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<Object> handleNotFoundException(NoHandlerFoundException e) {
        log.error("404异常：{}", e.getMessage());
        return ApiResult.failed(ResultCode.NOT_FOUND);
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResult<Object> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持：{}", e.getMessage());
        return ApiResult.failed(ResultCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 处理文件上传大小超限异常
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("文件上传大小超限：{}", e.getMessage());
        return ApiResult.failed(ResultCode.FILE_TOO_LARGE);
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Object> handleException(Exception e, HttpServletRequest request) {
        // 记录异常日志，包含请求信息
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);

        // 生成错误追踪ID
        String traceId = generateTraceId();
        log.error("错误追踪ID: {}", traceId);

        // 发生异常时返回提示信息
        String message = "系统繁忙，请稍后再试";

        // 如果是开发环境，返回详细错误信息
        if (isDevEnvironment()) {
            message = String.format("系统异常: %s (追踪ID: %s)", e.getMessage(), traceId);
        } else {
            message = "系统繁忙，请稍后再试";
        }

        ApiResult<Object> result = ApiResult.error(message);
        // 在开发环境中，可以将追踪ID添加到响应中
        if (isDevEnvironment()) {
            result.setData("traceId", traceId);
        }

        return result;
    }

    /**
     * 判断是否为开发环境
     */
    private boolean isDevEnvironment() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equals(profile) || "test".equals(profile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成错误追踪ID
     */
    private String generateTraceId() {
        return System.currentTimeMillis() + "-" +
               Integer.toHexString((int) (Math.random() * 0xFFFF));
    }
}