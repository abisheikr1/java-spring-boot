package com.billing_software.billing_software.models.tax;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.payloads.requests.TaxUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("taxes")
public class Tax extends CoreContext {

    @Transient
    private static String shortCode = "TAX";

    private String id; // Unique identifier for the tax
    private String name; // tax name
    private String description; // Description of the tax
    private String orgId; // Tax belongs to organization
    private Map<String, Double> taxSlabs; // Map to hold tax types and their rates

    public Tax(TaxUpsertRequest taxUpsertRequest) {
        if (taxUpsertRequest.id != null) {
            this.id = taxUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(Tax.shortCode);
            this.setCreatedDate(new Date());
        }

        this.name = taxUpsertRequest.name;
        this.description = taxUpsertRequest.description;
        this.taxSlabs = taxUpsertRequest.taxSlabs;
        this.orgId = taxUpsertRequest.orgId;
    }

}
