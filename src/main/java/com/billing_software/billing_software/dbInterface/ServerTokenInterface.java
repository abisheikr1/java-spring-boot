package com.billing_software.billing_software.dbInterface;

import com.billing_software.billing_software.models.authentication.ServerToken;

public interface ServerTokenInterface {

    public String create(ServerToken serverTokenData);

    public ServerToken get(String serverTokenId);
    
    public void delete(String serverTokenId);

    public boolean isTokenPresent(String id);

}
