package com.billing_software.billing_software.payloads.response;

import java.util.Date;

public class LoginResponse {

    public String userId;
    public String orgId;
    public String shortId;
    public String tokenId;
    public String accessToken;
    public String refreshToken;
    public Date accessExpireTime;
    public Date refreshExpireTime;
    public Date loggedInTime;

}
