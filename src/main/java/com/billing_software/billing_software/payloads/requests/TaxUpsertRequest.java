package com.billing_software.billing_software.payloads.requests;

import java.util.Map;

public class TaxUpsertRequest {

    public String id;
    public String name;
    public String description;
    public Map<String, Double> taxSlabs;
    public String orgId;

}
