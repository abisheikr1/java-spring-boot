package com.billing_software.billing_software.payloads.requests;

import com.billing_software.billing_software.models.customer.CustomerType;

public class CustomerUpsertRequest {

    public String id;
    public String orgId;
    public String customerCode;
    public String name;
    public String phoneNumber;
    public String emailAddress;
    public String billingAddress;
    public String shippingAddress;
    public String gstin;
    public Double creditLimit;
    public String paymentTerms;
    public CustomerType customerType;
    public String preferredCurrency;
    public String outletId;

}
