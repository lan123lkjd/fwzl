package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.common.PageResult;
import com.rental.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 分页查询用户
     */
    PageResult<User> listPage(Integer page, Integer size, String keyword);

    /**
     * 修改用户状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 修改密码
     */
    boolean updatePassword(Long id, String oldPassword, String newPassword);
}
