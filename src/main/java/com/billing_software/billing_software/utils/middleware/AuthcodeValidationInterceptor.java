package com.billing_software.billing_software.utils.middleware;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.billing_software.billing_software.models.authentication.TokenDetails;
import com.billing_software.billing_software.utils.exception.CustomException;
import com.billing_software.billing_software.utils.jwtToken.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

@Component
public class AuthcodeValidationInterceptor implements HandlerInterceptor {

    @Value("${authCode}")
    private String authCode;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String headerAuthcde = request.getHeader("authCode");
        if (headerAuthcde == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "AuthCode is missing");
        } else if (!headerAuthcde.equals(authCode)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid AuthCode");
        }
        return true;
    }

}
