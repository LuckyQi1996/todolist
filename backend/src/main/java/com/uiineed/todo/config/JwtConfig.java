package com.uiineed.todo.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * JWT配置类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * 密钥 - 生产环境必须通过环境变量设置
     */
    private String secret;

    /**
     * 过期时间（毫秒）
     */
    private Long expiration = 604800000L; // 7天

    /**
     * Token前缀
     */
    private String prefix = "Bearer";

    /**
     * Header名称
     */
    private String header = "Authorization";

    /**
     * 刷新Token过期时间（毫秒）
     */
    private Long refreshExpiration = 1209600000L; // 14天

    /**
     * 记住我功能过期时间（毫秒）
     */
    private Long rememberMeExpiration = 2592000000L; // 30天

    /**
     * 密钥最小长度要求
     */
    private static final int MIN_SECRET_LENGTH = 64;

    @PostConstruct
    public void init() {
        // 如果没有设置密钥，生成一个随机密钥并警告
        if (!StringUtils.hasText(secret)) {
            log.warn("JWT密钥未配置，正在生成随机密钥。请在生产环境中设置JWT_SECRET环境变量！");
            this.secret = generateSecureSecret();
        }

        // 检查密钥长度
        if (secret.length() < MIN_SECRET_LENGTH) {
            log.error("JWT密钥长度不足！当前长度：{}，最小要求：{} 字符", secret.length(), MIN_SECRET_LENGTH);
            throw new IllegalArgumentException("JWT密钥长度不足，请设置至少" + MIN_SECRET_LENGTH + "字符的密钥");
        }

        log.info("JWT配置初始化完成，过期时间：{} 天", expiration / (24 * 60 * 60 * 1000));
    }

    /**
     * 生成安全的随机密钥
     */
    private String generateSecureSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[64]; // 512位密钥
        random.nextBytes(bytes);
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 获取密钥对象
     *
     * @return SecretKey
     */
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 验证密钥强度
     */
    public boolean isSecretStrong() {
        return StringUtils.hasText(secret) &&
               secret.length() >= MIN_SECRET_LENGTH &&
               !secret.equals("uiineed-todo-jwt-secret-key-2023") &&
               !secret.equals("uiineed-todo-jwt-secret-key-2023-change-in-production");
    }
}