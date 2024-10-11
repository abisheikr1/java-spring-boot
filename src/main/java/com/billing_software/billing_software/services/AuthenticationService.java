package com.billing_software.billing_software.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.dbInterface.ServerTokenInterface;
import com.billing_software.billing_software.dbInterface.UserInterface;
import com.billing_software.billing_software.models.authentication.ServerToken;
import com.billing_software.billing_software.models.authentication.TokenDetails;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.models.user.User;
import com.billing_software.billing_software.payloads.requests.LoginRequest;
import com.billing_software.billing_software.payloads.response.LoginResponse;
import com.billing_software.billing_software.utils.crypto.CryptoService;
import com.billing_software.billing_software.utils.jwtToken.JwtService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;

@Service
public class AuthenticationService {

    private UserInterface userInterface;
    private JwtService jwtService;
    private ServerTokenInterface serverTokenInterface;
    private OrganizationInterface organizationInterface;

    public AuthenticationService(UserInterface userInterface, JwtService jwtService,
            ServerTokenInterface serverTokenInterface, OrganizationInterface organizationInterface) {
        this.userInterface = userInterface;
        this.jwtService = jwtService;
        this.serverTokenInterface = serverTokenInterface;
        this.organizationInterface = organizationInterface;
    }

    public LoginResponse userAuthentication(LoginRequest loginRequest, String orgId) {
        User userData = userInterface.getByUserName(loginRequest.userName);
        if (userData != null && userData.getOrgId() != null && userData.getOrgId().equals(orgId)) {
            String deycryptedPassword = "";
            try {
                deycryptedPassword = CryptoService.decrypt(userData.getPassword(), userData.getSecret());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (deycryptedPassword.equals(loginRequest.password)) {
                return this.login(userData, orgId);
            } else {
                throw new RuntimeException("Invalid password");
            }
        } else {
            throw new RuntimeException("Invalid user/orgId missing");
        }
    }

    private LoginResponse login(User userData, String orgId) {
        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("id", userData.getId());
        additionalClaims.put("tokenType", "serverToken");
        additionalClaims.put("orgId", orgId);
        additionalClaims.put("userId", userData.getId());

        Date serverExpiryTime = addHoursToDate(new Date(), 24.0);
        String token = this.jwtService.generateToken(userData.getUsername(), additionalClaims, serverExpiryTime);
        ServerToken serverTokenData = new ServerToken(token, serverExpiryTime);
        serverTokenInterface.create(serverTokenData);

        additionalClaims.put("tokenId", serverTokenData.getId());

        Date accessExpTime = addHoursToDate(new Date(), 4.0);
        Date refreshExpTime = addHoursToDate(new Date(), 12.0);

        Organization orgDetails = organizationInterface.get(userData.getOrgId());

        LoginResponse loginRes = new LoginResponse();
        loginRes.userId = userData.getId();
        loginRes.loggedInTime = new Date();
        loginRes.orgId = orgDetails.getId();
        loginRes.shortId = orgDetails.getShortId();
        loginRes.accessExpireTime = accessExpTime;
        loginRes.refreshExpireTime = refreshExpTime;
        additionalClaims.put("tokenType", "accessToken");
        loginRes.accessToken = jwtService.generateToken(userData.getUsername(), additionalClaims, accessExpTime);
        additionalClaims.put("tokenType", "refreshToken");
        loginRes.refreshToken = jwtService.generateToken(userData.getUsername(), additionalClaims, refreshExpTime);

        return loginRes;
    }

    public Date addHoursToDate(Date date, Double hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Double seconds = hours * 60 * 60;
        calendar.add(Calendar.SECOND, seconds.intValue());
        return calendar.getTime();
    }

    public LoginResponse refreshToken(String token, String orgId) {
        if (token != null && !token.isEmpty()) {
            TokenDetails tokenDetails = jwtService.getTokenDetails(token);
            if (tokenDetails.getOrgId().equals(orgId) && tokenDetails.getTokenType().equals("refreshToken")) {
                if (serverTokenInterface.isTokenPresent((String) tokenDetails.getTokenId())) {

                    Map<String, Object> additionalClaims = new HashMap<>();
                    additionalClaims.put("id", tokenDetails.getUserId());
                    additionalClaims.put("tokenType", "accessToken");
                    additionalClaims.put("orgId", orgId);

                    LoginResponse loginRes = new LoginResponse();
                    loginRes.userId = (String) tokenDetails.getUserId();
                    Date accessExpTime = addHoursToDate(new Date(), 4.0);
                    loginRes.accessToken = jwtService.generateToken((String) tokenDetails.getUsername(),
                            additionalClaims, accessExpTime);
                    loginRes.accessExpireTime = accessExpTime;
                    return loginRes;
                } else {
                    throw new RuntimeException("invalid token - login again");
                }
            } else {
                throw new RuntimeException("tenantId mismatch/not a refresh token");
            }
        } else {
            throw new RuntimeException("token cannot be null");
        }
    }

    public String userLogOut() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        String accessToken = request.getHeader("Authorization");

        TokenDetails tokenDetails = jwtService.getTokenDetails(accessToken);
        if (tokenDetails.getTokenId() != null) {
            serverTokenInterface.delete(tokenDetails.getTokenId());
        } else {
            throw new RuntimeException("Invalid token to logout");
        }

        return "Logged out successfully";
    }
}
