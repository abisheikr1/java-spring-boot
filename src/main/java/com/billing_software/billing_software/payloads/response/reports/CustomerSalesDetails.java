package com.billing_software.billing_software.payloads.response.reports;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSalesDetails {

    private String id; // Unique identifier for the customer
    private String name; // Name of the customer as it appears to customers
    private Integer noOfOrdersMade; // Total number of units sold during the reporting period
    private Double totalRevenueMade; // Total revenue generated from sales of this customer
    private Double totalTaxCollected; // Total tax collected on sales of this customer
    private Double totalDiscountGiven; // Total amount of discounts applied to this customer's sales
    private Double actualRevenueMade; // Net revenue after deducting discounts and taxes

    public CustomerSalesDetails() {
        this.noOfOrdersMade = 0;
        this.totalTaxCollected = 0.0;
        this.totalRevenueMade = 0.0;
        this.totalDiscountGiven = 0.0;
        this.actualRevenueMade = 0.0;
    }

}
