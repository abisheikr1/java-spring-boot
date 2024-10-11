package com.billing_software.billing_software.models.brand;

import java.util.Date;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.payloads.requests.BrandUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;
import java.util.Map;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("brands") // Marks this class as a MongoDB document
public class Brand extends CoreContext {

    @Transient
    private static String shortCode = "BRN";

    private String id; // Unique identifier for the brand
    private String name; // Name of the brand
    private String description; // Description of the brand
    private String orgId; // Description of the brand
    private Map<String, Object> profile; // user Logo

    public Brand(BrandUpsertRequest brandUpsertRequest) {
        if (brandUpsertRequest.id != null) {
            this.id = brandUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(Brand.shortCode);
            this.setCreatedDate(new Date());
        }

        this.name = brandUpsertRequest.name;
        this.description = brandUpsertRequest.description;
        this.orgId = brandUpsertRequest.orgId;
    }

    public void setProfile(String imageUrl, String imageType) {
        if (this.profile == null) {
            this.profile = new HashMap<>();
        }
        this.profile.put(imageType, imageUrl);
    }
}
