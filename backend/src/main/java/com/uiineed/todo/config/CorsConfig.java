package com.uiineed.todo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS跨域配置类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080,https://ricocc.com}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private List<String> allowedMethods;

    @Value("${cors.allowed-headers:Authorization,Content-Type,X-Requested-With,Accept,Origin}")
    private List<String> allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private Boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private Long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("初始化CORS配置，允许的源：{}", allowedOrigins);

        CorsConfiguration configuration = new CorsConfiguration();

        // 设置允许的源
        configuration.setAllowedOrigins(allowedOrigins);

        // 设置允许的HTTP方法
        configuration.setAllowedMethods(allowedMethods);

        // 设置允许的请求头
        configuration.setAllowedHeaders(allowedHeaders);

        // 是否允许携带认证信息
        configuration.setAllowCredentials(allowCredentials);

        // 预检请求的缓存时间
        configuration.setMaxAge(maxAge);

        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}