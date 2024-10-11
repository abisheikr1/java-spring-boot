package com.billing_software.billing_software.models.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.payloads.requests.UserUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;
import com.billing_software.billing_software.utils.crypto.CryptoService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("users")
public class User extends CoreContext {

    @Transient
    private static String shortCode = "USR";

    private String id; // Unique identifier for the user
    private String orgId; // Belongs to organization
    private String name; // Full name of the user
    private String username; // Username for login
    private String password; // User's password (should be hashed)
    private String secret; // Secret for encypriting password
    private String email; // Email address of the user
    private String phoneNumber; // Contact phone number
    private Boolean isActive; // Check if user is active or blocked
    private String activeDesc; // reason for isActive false

    private String resetToken; // reset token for paswword

    private Map<String, Object> profile; // user Logo

    public User(UserUpsertRequest userUpsertRequest) {
        if (userUpsertRequest.id != null) {
            this.id = userUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(User.shortCode);
            this.secret = CryptoService.getRandomSecretKey();
            this.isActive = true;
            this.activeDesc = "active - user created newly";
            try {
                this.password = CryptoService.encrypt(userUpsertRequest.password, this.secret);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.setCreatedDate(new Date());
        }

        this.orgId = userUpsertRequest.orgId;
        this.name = userUpsertRequest.name;
        this.username = userUpsertRequest.username;
        this.email = userUpsertRequest.email;
        this.phoneNumber = userUpsertRequest.phoneNumber;
    }

    public void setProfile(String imageUrl, String imageType) {
        if (this.profile == null) {
            this.profile = new HashMap<>();
        }
        this.profile.put(imageType, imageUrl);
    }
}
