package com.uiineed.todo.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.uiineed.todo.config.WeChatConfig;
import com.uiineed.todo.entity.User;
import com.uiineed.todo.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信服务类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Slf4j
@Service
public class WeChatService {

    @Autowired
    private WeChatConfig weChatConfig;

    /**
     * 微信用户信息响应对象
     */
    @Data
    public static class WeChatUserInfo {
        private String openid;
        private String unionid;
        private String nickname;
        private String headimgurl;
        private Integer sex;
        private String country;
        private String province;
        private String city;
        private String language;
    }

    /**
     * 微信Access Token响应对象
     */
    @Data
    public static class WeChatAccessToken {
        private String access_token;
        private Integer expires_in;
        private String refresh_token;
        private String openid;
        private String scope;
        private String unionid;
    }

    /**
     * 获取微信授权URL
     *
     * @param state 状态参数
     * @return 授权URL
     */
    public String getAuthUrl(String state) {
        try {
            String encodedRedirectUri = URLEncoder.encode(weChatConfig.getRedirectUri(), StandardCharsets.UTF_8.toString());
            return String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect",
                    WeChatConfig.WECHAT_AUTH_URL,
                    weChatConfig.getAppId(),
                    encodedRedirectUri,
                    weChatConfig.getScope(),
                    state);
        } catch (Exception e) {
            log.error("生成微信授权URL失败", e);
            throw new RuntimeException("生成微信授权URL失败", e);
        }
    }

    /**
     * 根据授权码获取Access Token
     *
     * @param code 授权码
     * @return Access Token信息
     */
    public WeChatAccessToken getAccessToken(String code) {
        try {
            String url = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    WeChatConfig.WECHAT_ACCESS_TOKEN_URL,
                    weChatConfig.getAppId(),
                    weChatConfig.getAppSecret(),
                    code);

            String response = executeHttpGet(url);
            JSONObject jsonObject = JSON.parseObject(response);

            // 检查是否有错误
            if (jsonObject.containsKey("errcode")) {
                Integer errcode = jsonObject.getInteger("errcode");
                String errmsg = jsonObject.getString("errmsg");
                log.error("获取微信Access Token失败: errcode={}, errmsg={}", errcode, errmsg);
                throw new RuntimeException("获取微信Access Token失败: " + errmsg);
            }

            return JSON.parseObject(response, WeChatAccessToken.class);
        } catch (Exception e) {
            log.error("获取微信Access Token异常", e);
            throw new RuntimeException("获取微信Access Token失败", e);
        }
    }

    /**
     * 获取微信用户信息
     *
     * @param accessToken Access Token
     * @param openId 用户OpenID
     * @return 用户信息
     */
    public WeChatUserInfo getUserInfo(String accessToken, String openId) {
        try {
            String url = String.format("%s?access_token=%s&openid=%s",
                    WeChatConfig.WECHAT_USER_INFO_URL,
                    accessToken,
                    openId);

            String response = executeHttpGet(url);
            JSONObject jsonObject = JSON.parseObject(response);

            // 检查是否有错误
            if (jsonObject.containsKey("errcode")) {
                Integer errcode = jsonObject.getInteger("errcode");
                String errmsg = jsonObject.getString("errmsg");
                log.error("获取微信用户信息失败: errcode={}, errmsg={}", errcode, errmsg);
                throw new RuntimeException("获取微信用户信息失败: " + errmsg);
            }

            return JSON.parseObject(response, WeChatUserInfo.class);
        } catch (Exception e) {
            log.error("获取微信用户信息异常", e);
            throw new RuntimeException("获取微信用户信息失败", e);
        }
    }

    /**
     * 执行HTTP GET请求
     *
     * @param url 请求URL
     * @return 响应内容
     */
    private String executeHttpGet(String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpUriRequest request = new HttpGet(url);
            request.addHeader("Content-Type", "application/json; charset=utf-8");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            }
        }
    }

    /**
     * 验证微信回调状态
     *
     * @param state 状态参数
     * @return 是否有效
     */
    public boolean validateState(String state) {
        // TODO: 实现CSRF保护，使用Redis存储状态值
        return state != null && !state.isEmpty();
    }

    /**
     * 转换微信用户信息为系统用户
     *
     * @param weChatUserInfo 微信用户信息
     * @return 系统用户
     */
    public User convertToUser(WeChatUserInfo weChatUserInfo) {
        User user = new User();
        user.setWechatOpenId(weChatUserInfo.getOpenid());
        user.setWechatUnionId(weChatUserInfo.getUnionid());
        user.setNickname(weChatUserInfo.getNickname());
        user.setAvatarUrl(weChatUserInfo.getHeadimgurl());
        user.setGender(weChatUserInfo.getSex());
        user.setCountry(weChatUserInfo.getCountry());
        user.setProvince(weChatUserInfo.getProvince());
        user.setCity(weChatUserInfo.getCity());
        user.setLanguage(weChatUserInfo.getLanguage());
        user.setStatus(User.Status.ACTIVE);
        user.setDeleted(0);
        return user;
    }
}