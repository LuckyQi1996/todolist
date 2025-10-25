package com.uiineed.todo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 二维码响应VO
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QrCodeResponse {

    /**
     * 二维码图片（Base64）
     */
    private String qrCode;

    /**
     * 状态参数
     */
    private String state;

    /**
     * 授权URL
     */
    private String authUrl;

    /**
     * 提示信息
     */
    private String message;
}