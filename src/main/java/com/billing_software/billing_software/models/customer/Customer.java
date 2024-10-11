package com.billing_software.billing_software.models.customer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.payloads.requests.CustomerUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("customers")
public class Customer extends CoreContext {

    @Transient
    private static String shortCode = "CUS";

    private String id; // Unique identifier for the customer
    private String orgId; // Belongs to organization
    private String customerCode; // Unique code for the customer
    private String name; // Full name of the customer
    private String phoneNumber; // Primary contact number for the customer
    private String emailAddress; // Email address for communication
    private String billingAddress; // Address where invoices should be sent
    private String shippingAddress; // Address where goods/services are delivered
    private String gstin; // Tax identification number (GSTIN or TIN)
    private Double creditLimit; // Maximum credit allowed to the customer
    private String paymentTerms; // Payment terms (e.g., "Net 30")
    private Double accountBalance; // Outstanding balance for the customer
    private CustomerType customerType; // Type of customer (e.g., Retailer, Wholesale)
    private String creditRating; // Credit rating of the customer
    private Integer loyaltyPoints; // Loyalty or reward points earned by the customer
    private String outletId; // belongs to outlet
    private Map<String, Object> profile; // user Logo

    public Customer(CustomerUpsertRequest customerUpsertRequest) {
        if (customerUpsertRequest.id != null) {
            this.id = customerUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(Customer.shortCode);
            this.setCreatedDate(new Date());
        }

        this.orgId = customerUpsertRequest.orgId;
        this.customerCode = customerUpsertRequest.customerCode;
        this.name = customerUpsertRequest.name;
        this.phoneNumber = customerUpsertRequest.phoneNumber;
        this.emailAddress = customerUpsertRequest.emailAddress;
        this.billingAddress = customerUpsertRequest.billingAddress;
        this.shippingAddress = customerUpsertRequest.shippingAddress;
        this.gstin = customerUpsertRequest.gstin;
        this.creditLimit = customerUpsertRequest.creditLimit;
        this.paymentTerms = customerUpsertRequest.paymentTerms;
        this.customerType = customerUpsertRequest.customerType;
        this.outletId = customerUpsertRequest.outletId;
    }

    public void setProfile(String imageUrl, String imageType) {
        if (this.profile == null) {
            this.profile = new HashMap<>();
        }
        this.profile.put(imageType, imageUrl);
    }

}
