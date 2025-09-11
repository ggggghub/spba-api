package com.example.spba.config;

import com.example.spba.interceptor.SpbaInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer
{

    @Bean
    public SpbaInterceptor spbaInterceptor()
    {
        return new SpbaInterceptor();
    }
    @Value("${app.upload-root}")
    private String uploadRoot;

    /**
     * 拦截器
     * addPathPatterns 用于添加拦截规则
     * excludePathPatterns 用于排除拦截
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(spbaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/register");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 访问 /files/** 映射到本地磁盘
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadRoot + "/");
    }
}
