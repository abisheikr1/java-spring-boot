package com.billing_software.billing_software.payloads.requests;

import java.util.Date;

import com.billing_software.billing_software.models.item.BatchDetails;

public class ItemUpsertRequest {

    public String id;
    public String orgId;
    public String outletId;
    public String itemCode;
    public String name;
    public String type;
    public String description;
    public String hsnCode;
    public String sacCode;
    public Double mrp;
    public Double msp;
    public String unitOfMeasure;
    public Double unitPrice;
    public Integer availableStock;
    public String currency;
    public BatchDetails batchDetails;
    public Date expiryDate;
    public String taxId;
    public String brandId;
}
