package com.uiineed.todo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信配置类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.open")
public class WeChatConfig {

    /**
     * 微信开放平台AppID
     */
    private String appId;

    /**
     * 微信开放平台AppSecret
     */
    private String appSecret;

    /**
     * 授权回调地址
     */
    private String redirectUri;

    /**
     * 授权作用域
     */
    private String scope;

    /**
     * 微信授权地址
     */
    public static final String WECHAT_AUTH_URL = "https://open.weixin.qq.com/connect/qrconnect";

    /**
     * 微信获取Access Token地址
     */
    public static final String WECHAT_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 微信获取用户信息地址
     */
    public static final String WECHAT_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 获取微信授权URL
     *
     * @param state 状态参数
     * @return 授权URL
     */
    public String getAuthUrl(String state) {
        return String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                WECHAT_AUTH_URL,
                appId,
                redirectUri,
                scope,
                state);
    }
}