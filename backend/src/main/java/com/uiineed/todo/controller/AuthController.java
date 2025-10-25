package com.uiineed.todo.controller;

import com.uiineed.todo.common.ApiResult;
import com.uiineed.todo.common.ResultCode;
import com.uiineed.todo.config.WeChatConfig;
import com.uiineed.todo.entity.User;
import com.uiineed.todo.service.UserService;
import com.uiineed.todo.service.WeChatService;
import com.uiineed.todo.util.JwtUtil;
import com.uiineed.todo.vo.LoginResponse;
import com.uiineed.todo.vo.QrCodeResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 认证控制器
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WeChatConfig weChatConfig;

    /**
     * 获取微信登录二维码
     *
     * @return 二维码响应
     */
    @GetMapping("/qrcode")
    public ApiResult<QrCodeResponse> getLoginQrCode() {
        try {
            // 生成唯一状态值
            String state = UUID.randomUUID().toString();

            // 生成微信授权URL
            String authUrl = weChatService.getAuthUrl(state);

            // 生成二维码
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(authUrl, BarcodeFormat.QR_CODE, 300, 300);

            // 将二维码转换为Base64
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            String qrCodeBase64 = java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());

            QrCodeResponse response = new QrCodeResponse();
            response.setQrCode("data:image/png;base64," + qrCodeBase64);
            response.setState(state);
            response.setAuthUrl(authUrl);
            response.setMessage("请使用微信扫描二维码登录");

            return ApiResult.success(response);
        } catch (Exception e) {
            log.error("生成登录二维码失败", e);
            return ApiResult.error("生成登录二维码失败");
        }
    }

    /**
     * 微信登录回调
     *
     * @param code 授权码
     * @param state 状态参数
     * @return 登录响应
     */
    @GetMapping("/wechat/callback")
    public ApiResult<LoginResponse> wechatCallback(
            @RequestParam String code,
            @RequestParam String state) {

        try {
            // 验证状态参数（防CSRF攻击）
            if (!weChatService.validateState(state)) {
                return ApiResult.failed(ResultCode.WECHAT_CODE_INVALID);
            }

            // 获取Access Token
            WeChatService.WeChatAccessToken accessToken = weChatService.getAccessToken(code);

            // 获取用户信息
            WeChatService.WeChatUserInfo userInfo = weChatService.getUserInfo(
                accessToken.getAccess_token(),
                accessToken.getOpenid()
            );

            // 查找或创建用户
            User user = userService.findOrCreateByWeChatOpenId(userInfo);

            // 更新最后登录时间
            userService.updateLastLoginTime(user.getId());

            // 生成JWT Token
            String token = jwtUtil.generateToken(user.getId(), user.getWechatOpenId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getWechatOpenId());

            // 构造响应
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setTokenType("Bearer");
            loginResponse.setExpiresIn(jwtUtil.getExpirationDateFromToken(token).getTime());

            User.UserInfo userInfoResponse = new User.UserInfo();
            userInfoResponse.setId(user.getId());
            userInfoResponse.setNickname(user.getNickname());
            userInfoResponse.setAvatarUrl(user.getAvatarUrl());
            userInfoResponse.setLanguage(user.getLanguage());
            loginResponse.setUser(userInfoResponse);

            return ApiResult.success("登录成功", loginResponse);

        } catch (Exception e) {
            log.error("微信登录失败", e);
            return ApiResult.failed(ResultCode.WECHAT_AUTH_FAILED);
        }
    }

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新Token
     * @return 新的Token
     */
    @PostMapping("/refresh")
    public ApiResult<Map<String, Object>> refreshToken(@RequestParam String refreshToken) {
        try {
            // 验证刷新Token
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                return ApiResult.failed(ResultCode.TOKEN_INVALID);
            }

            // 从刷新Token中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String openId = jwtUtil.getOpenIdFromToken(refreshToken);

            // 检查用户是否仍然有效
            User user = userService.findById(userId);
            if (user == null || user.getStatus() != User.Status.ACTIVE) {
                return ApiResult.failed(ResultCode.USER_NOT_FOUND);
            }

            // 生成新的Token
            String newToken = jwtUtil.generateToken(userId, openId);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, openId);

            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            response.put("refreshToken", newRefreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtUtil.getExpirationDateFromToken(newToken).getTime());

            return ApiResult.success("Token刷新成功", response);

        } catch (Exception e) {
            log.error("刷新Token失败", e);
            return ApiResult.failed(ResultCode.TOKEN_INVALID);
        }
    }

    /**
     * 用户登出
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        // TODO: 将Token加入黑名单
        return ApiResult.success("登出成功");
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/me")
    public ApiResult<User.UserInfo> getCurrentUser() {
        try {
            // TODO: 从Security上下文获取用户信息
            // User user = userService.getCurrentUser();
            // return ApiResult.success(user.getUserInfo());

            // 临时实现
            return ApiResult.success("获取用户信息成功", null);
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
            return ApiResult.failed(ResultCode.SYSTEM_ERROR);
        }
    }
}