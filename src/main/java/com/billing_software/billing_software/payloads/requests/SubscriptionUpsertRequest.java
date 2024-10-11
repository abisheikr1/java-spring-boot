package com.billing_software.billing_software.payloads.requests;

import java.util.Map;

import com.billing_software.billing_software.models.subscription.ConfigItem;

public class SubscriptionUpsertRequest {

    public String id;
    public String name;
    public String planDescription;
    public Double price;
    public Double discountPrice;
    public String taxId;
    public Map<String, ConfigItem> configuration;

}
