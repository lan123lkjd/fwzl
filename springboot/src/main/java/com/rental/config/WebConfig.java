package com.rental.config;

import com.rental.interceptor.AuthInterceptor;
import com.rental.interceptor.OptionalAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private OptionalAuthInterceptor optionalAuthInterceptor;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(optionalAuthInterceptor)
                .addPathPatterns("/api/house/detail/**", "/api/house/recommend", "/api/house/hot");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/house/list",
                        "/api/house/detail/**",
                        "/api/house/recommend",
                        "/api/house/hot",
                        "/api/news/list",
                        "/api/news/detail/**",
                        "/api/notice/list",
                        "/api/area/**",
                        "/api/community/**",
                        "/api/evaluations/list/**",
                        "/api/upload/**",
                        "/api/ai/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
