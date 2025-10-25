package com.uiineed.todo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
@TableName("users")
public class User {

    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 微信OpenID
     */
    @NotBlank(message = "微信OpenID不能为空")
    @Column(name = "wechat_openid", unique = true, nullable = false, length = 100)
    private String wechatOpenId;

    /**
     * 微信UnionID
     */
    @Column(name = "wechat_unionid", length = 100)
    private String wechatUnionId;

    /**
     * 微信昵称
     */
    @Column(name = "nickname", length = 50)
    private String nickname;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @Column(name = "gender")
    private Integer gender;

    /**
     * 国家
     */
    @Column(name = "country", length = 50)
    private String country;

    /**
     * 省份
     */
    @Column(name = "province", length = 50)
    private String province;

    /**
     * 城市
     */
    @Column(name = "city", length = 50)
    private String city;

    /**
     * 语言偏好
     */
    @Column(name = "language", length = 20)
    private String language;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 登录次数
     */
    @Column(name = "login_count")
    private Integer loginCount;

    /**
     * 状态：0-禁用，1-正常
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableLogic
    @Column(name = "deleted")
    private Integer deleted;

    /**
     * 预定义性别常量
     */
    public static class Gender {
        public static final int UNKNOWN = 0;
        public static final int MALE = 1;
        public static final int FEMALE = 2;
    }

    /**
     * 预定义状态常量
     */
    public static class Status {
        public static final int DISABLED = 0;
        public static final int ACTIVE = 1;
    }

    /**
     * 用户信息简版（用于API响应）
     */
    @Data
    public static class UserInfo {
        /**
         * 用户ID
         */
        private Long id;

        /**
         * 微信昵称
         */
        private String nickname;

        /**
         * 头像URL
         */
        private String avatarUrl;

        /**
         * 语言偏好
         */
        private String language;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(this.id);
        userInfo.setNickname(this.nickname);
        userInfo.setAvatarUrl(this.avatarUrl);
        userInfo.setLanguage(this.language);
        return userInfo;
    }
}