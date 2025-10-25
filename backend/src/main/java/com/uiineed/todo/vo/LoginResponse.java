package com.uiineed.todo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.uiineed.todo.entity.User;
import lombok.Data;

/**
 * 登录响应VO
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 刷新Token
     */
    private String refreshToken;

    /**
     * Token类型
     */
    private String tokenType;

    /**
     * 过期时间
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private User.UserInfo user;
}