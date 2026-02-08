package com.rental.controller;

import com.rental.common.Result;
import com.rental.entity.User;
import com.rental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证接口")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        try {
            String token = userService.login(username, password);
            User user = userService.getByUsername(username);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            user.setPassword(null);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@RequestBody User user) {
        try {
            userService.register(user);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<User> getUserInfo(@RequestAttribute("userId") Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> updatePassword(@RequestAttribute("userId") Long userId,
            @RequestBody Map<String, String> params) {
        try {
            userService.updatePassword(userId, params.get("oldPassword"), params.get("newPassword"));
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    public Result<Void> updateProfile(@RequestAttribute("userId") Long userId, @RequestBody User user) {
        user.setId(userId);
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);
        userService.updateById(user);
        return Result.success();
    }
}
