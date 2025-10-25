package com.uiineed.todo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.uiineed.todo.entity.User;
import com.uiineed.todo.mapper.UserMapper;
import com.uiineed.todo.service.WeChatService.WeChatUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务类
 *
 * @author Uiineed
 * @version 1.0.0
 */
@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据微信OpenID查找或创建用户
     *
     * @param weChatUserInfo 微信用户信息
     * @return 用户信息
     */
    public User findOrCreateByWeChatOpenId(WeChatUserInfo weChatUserInfo) {
        // 先查找是否已存在该用户
        User existUser = findByWeChatOpenId(weChatUserInfo.getOpenid());

        if (existUser != null) {
            // 更新用户信息
            updateUserFromWechat(existUser, weChatUserInfo);
            return existUser;
        } else {
            // 创建新用户
            return createUserFromWechat(weChatUserInfo);
        }
    }

    /**
     * 根据微信OpenID查找用户
     *
     * @param openId 微信OpenID
     * @return 用户信息
     */
    public User findByWeChatOpenId(String openId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wechat_openid", openId);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     */
    public void updateLastLoginTime(Long userId) {
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setLastLoginTime(LocalDateTime.now());
        // 增加登录次数
        User currentUser = findById(userId);
        if (currentUser != null) {
            updateUser.setLoginCount((currentUser.getLoginCount() == null ? 0 : currentUser.getLoginCount()) + 1);
        }

        userMapper.updateById(updateUser);
    }

    /**
     * 根据微信用户信息创建新用户
     *
     * @param weChatUserInfo 微信用户信息
     * @return 新用户信息
     */
    private User createUserFromWechat(WeChatUserInfo weChatUserInfo) {
        User newUser = new User();
        newUser.setWechatOpenId(weChatUserInfo.getOpenid());
        newUser.setWechatUnionId(weChatUserInfo.getUnionid());
        newUser.setNickname(weChatUserInfo.getNickname());
        newUser.setAvatarUrl(weChatUserInfo.getHeadimgurl());
        newUser.setGender(weChatUserInfo.getSex());
        newUser.setCountry(weChatUserInfo.getCountry());
        newUser.setProvince(weChatUserInfo.getProvince());
        newUser.setCity(weChatUserInfo.getCity());
        newUser.setLanguage(weChatUserInfo.getLanguage());
        newUser.setLastLoginTime(LocalDateTime.now());
        newUser.setLoginCount(1);
        newUser.setStatus(User.Status.ACTIVE);

        userMapper.insert(newUser);
        log.info("创建新用户: openId={}, nickname={}", weChatUserInfo.getOpenid(), weChatUserInfo.getNickname());

        return newUser;
    }

    /**
     * 根据微信用户信息更新现有用户
     *
     * @param existUser 现有用户
     * @param weChatUserInfo 微信用户信息
     */
    private void updateUserFromWechat(User existUser, WeChatUserInfo weChatUserInfo) {
        // 只更新可能变化的字段
        existUser.setWechatUnionId(weChatUserInfo.getUnionid());
        existUser.setNickname(weChatUserInfo.getNickname());
        existUser.setAvatarUrl(weChatUserInfo.getHeadimgurl());
        existUser.setGender(weChatUserInfo.getSex());
        existUser.setCountry(weChatUserInfo.getCountry());
        existUser.setProvince(weChatUserInfo.getProvince());
        existUser.setCity(weChatUserInfo.getCity());
        existUser.setLanguage(weChatUserInfo.getLanguage());

        userMapper.updateById(existUser);
        log.info("更新用户信息: openId={}, nickname={}", weChatUserInfo.getOpenid(), weChatUserInfo.getNickname());
    }

    /**
     * 检查用户是否有效
     *
     * @param userId 用户ID
     * @return 是否有效
     */
    public boolean isUserValid(Long userId) {
        User user = findById(userId);
        return user != null && User.Status.ACTIVE.equals(user.getStatus());
    }
}