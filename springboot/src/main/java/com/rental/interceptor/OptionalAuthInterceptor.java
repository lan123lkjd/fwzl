package com.rental.interceptor;

import com.rental.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 可选认证拦截器
 * 有token时解析用户信息，没有token时也不报错
 */
@Component
public class OptionalAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token != null && !token.isEmpty()) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (jwtUtil.validateToken(token)) {
                request.setAttribute("userId", jwtUtil.getUserId(token));
                request.setAttribute("username", jwtUtil.getUsername(token));
                request.setAttribute("role", jwtUtil.getRole(token));
            }
        }

        return true;
    }
}
