package com.billing_software.billing_software.models.authentication;

import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDetails {
    private String tokenId;
    private String tokenType;
    private String userId;
    private String orgId;
    private String username;
    private String platform;
    private String deviceId;
    private String clientId;
    private String ipAddress;

    public TokenDetails(Claims claims) {
        if (claims.get("tokenId") != null)
            this.tokenId = claims.get("tokenId").toString();
        if (claims.get("userId") != null)
            this.userId = claims.get("userId").toString();
        if (claims.get("orgId") != null)
            this.orgId = claims.get("orgId").toString();
        if (claims.get("username") != null)
            this.username = claims.get("username").toString();
        if (claims.get("platform") != null)
            this.platform = claims.get("platform").toString();
        if (claims.get("deviceId") != null)
            this.deviceId = claims.get("deviceId").toString();
        if (claims.get("clientId") != null)
            this.clientId = claims.get("clientId").toString();
        if (claims.get("ipAddress") != null)
            this.ipAddress = claims.get("ipAddress").toString();
        if (claims.get("tokenType") != null)
            this.tokenType = claims.get("tokenType").toString();
    }

}
