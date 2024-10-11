package com.billing_software.billing_software.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.billing_software.billing_software.utils.middleware.AuthcodeValidationInterceptor;
import com.billing_software.billing_software.utils.middleware.TokenValidationInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenValidationInterceptor tokenValidationInterceptor;
    private final AuthcodeValidationInterceptor authcodeValidationInterceptor;

    public WebConfig(TokenValidationInterceptor tokenValidationInterceptor,
            AuthcodeValidationInterceptor authcodeValidationInterceptor) {
        this.tokenValidationInterceptor = tokenValidationInterceptor;
        this.authcodeValidationInterceptor = authcodeValidationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor)
                .addPathPatterns("/**") // Apply to all paths
                .excludePathPatterns("/user/*/login", "/org/upsert", "/user/upsert", "/subscription/**",
                        "/org/*/minimal", "/health-check", "/user/send/reset-password/link", "/user/reset-password");

        registry.addInterceptor(authcodeValidationInterceptor)
                .addPathPatterns("/org/upsert", "/user/upsert", "/subscription/**", "/org/*/minimal")
                .excludePathPatterns("/health-check", "/user/send/reset-password/link", "/user/reset-password");
    }

}
