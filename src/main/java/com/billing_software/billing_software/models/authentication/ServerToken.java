package com.billing_software.billing_software.models.authentication;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("serverTokens")
public class ServerToken {

    @Transient
    private static final String shortCode = "TOK";

    private String id;
    private String serverToken;
    @Indexed(expireAfterSeconds = 0)
    private Date expiryDate;

    public ServerToken(String token, Date expiryDate) {
        this.id = GenerateIdService.generateId(ServerToken.shortCode);
        this.serverToken = token;
        this.expiryDate = expiryDate;
    }

}
