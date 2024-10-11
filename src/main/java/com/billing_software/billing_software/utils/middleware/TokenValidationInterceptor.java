package com.billing_software.billing_software.utils.middleware;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.billing_software.billing_software.dbInterface.ServerTokenInterface;
import com.billing_software.billing_software.dbInterface.UserInterface;
import com.billing_software.billing_software.models.authentication.ServerToken;
import com.billing_software.billing_software.models.authentication.TokenDetails;
import com.billing_software.billing_software.models.user.User;
import com.billing_software.billing_software.utils.exception.CustomException;
import com.billing_software.billing_software.utils.jwtToken.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    private JwtService jwtService;
    private ServerTokenInterface serverTokenInterface;
    private UserInterface userInterface;

    public TokenValidationInterceptor(JwtService jwtService, ServerTokenInterface serverTokenInterface,
            UserInterface userInterface) {
        this.jwtService = jwtService;
        this.serverTokenInterface = serverTokenInterface;
        this.userInterface = userInterface;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Access Token is missing");
        } else {
            TokenDetails tokenDetails = jwtService.getTokenDetails(accessToken);
            ServerToken serverToken = serverTokenInterface.get(tokenDetails.getTokenId());
            if (serverToken == null) {
                throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid access token or token not found");
            }
            User userDetails = userInterface.get(tokenDetails.getUserId());

            if (!userDetails.getOrgId().equals(tokenDetails.getOrgId())) {
                throw new CustomException(HttpStatus.UNAUTHORIZED, "User not belongs to organization");
            }

            request.setAttribute("orgId", tokenDetails.getOrgId());
            request.setAttribute("userId", tokenDetails.getUserId());
        }
        return true;
    }

}
