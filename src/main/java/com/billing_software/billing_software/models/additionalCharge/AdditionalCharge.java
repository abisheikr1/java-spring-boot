package com.billing_software.billing_software.models.additionalCharge;

import java.util.Date;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.authentication.ServerToken;
import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.models.customer.Customer;
import com.billing_software.billing_software.payloads.requests.AdditionalChargeUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("additionalCharges")
public class AdditionalCharge extends CoreContext {

    @Transient
    private static final String shortCode = "ADC";

    private String id;
    private String name; // Name of the charge (e.g., "Packing Charge", "Delivery Charge")
    private Double amount; // Amount for the charge
    private String description; // Optional description for the charge
    private String orgId; // Tax belongs to organization
    private String currency; // Currency type for the pricing

    public AdditionalCharge(AdditionalChargeUpsertRequest additionalChargeUpsertRequest) {
        if (additionalChargeUpsertRequest.id != null) {
            this.id = additionalChargeUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(AdditionalCharge.shortCode);
            this.setCreatedDate(new Date());
        }

        this.name = additionalChargeUpsertRequest.name;
        this.amount = additionalChargeUpsertRequest.amount;
        this.description = additionalChargeUpsertRequest.description;
        this.orgId = additionalChargeUpsertRequest.orgId;
        this.currency = additionalChargeUpsertRequest.currency;
    }

}
