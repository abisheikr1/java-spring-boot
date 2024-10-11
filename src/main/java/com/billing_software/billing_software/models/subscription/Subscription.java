package com.billing_software.billing_software.models.subscription;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.models.tax.Tax;
import com.billing_software.billing_software.payloads.requests.SubscriptionUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("subscriptions")
public class Subscription extends CoreContext {

    @Transient
    private static String shortCode = "SUB";

    private String id; // Unique identifier for the subscription
    private String name; // Name of the subscription plan
    private String planDescription; // Description of the subscription plan
    private Double price; // Original price of the subscription plan
    private Double discountPrice; // Original price of the subscription plan
    private String taxId; // Identifier for the applicable tax on the subscription
    private Tax taxDetail; // tax detail
    private Map<String, ConfigItem> configuration; // Configuration details specific to the subscription plan

    public Subscription(SubscriptionUpsertRequest subscriptionUpsertRequest) {
        if (subscriptionUpsertRequest.id != null) {
            this.id = subscriptionUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(Subscription.shortCode);
            this.setCreatedDate(new Date());
        }

        this.name = subscriptionUpsertRequest.name;
        this.planDescription = subscriptionUpsertRequest.planDescription;
        this.price = subscriptionUpsertRequest.price;
        this.taxId = subscriptionUpsertRequest.taxId;
        this.configuration = subscriptionUpsertRequest.configuration;
        this.discountPrice = subscriptionUpsertRequest.discountPrice;
    }

}