package com.rental.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.User;
import com.rental.mapper.UserMapper;
import com.rental.service.FlowIndexService;
import com.rental.service.UserService;
import com.rental.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FlowIndexService flowIndexService;

    @Override
    public String login(String username, String password) {
        User user = getByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        String md5Password = DigestUtil.md5Hex(password);
        if (!md5Password.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否存在
        if (getByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 加密密码
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        user.setRole(1); // 默认普通用户
        user.setStatus(1); // 默认正常状态
        boolean result = save(user);
        if (result) {
            flowIndexService.recordNewUser();
        }
        return result;
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    @Override
    public PageResult<User> listPage(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = page(pageParam, wrapper);

        // 隐藏密码
        result.getRecords().forEach(u -> u.setPassword(null));

        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        return updateById(user);
    }

    @Override
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String md5OldPassword = DigestUtil.md5Hex(oldPassword);
        if (!md5OldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(DigestUtil.md5Hex(newPassword));
        return updateById(user);
    }
}
