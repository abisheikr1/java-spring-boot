package com.billing_software.billing_software.payloads.response.reports;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalSalesDetail {

    private Integer noOfOrdersMade; // Total number of units sold during the reporting period
    private Double totalRevenueMade; // Total revenue generated from sales of this customer
    private Double totalTaxCollected; // Total tax collected on sales of this customer
    private Double totalDiscountGiven; // Total amount of discounts applied to this customer's sales
    private Double actualRevenueMade; // Net revenue after deducting discounts and taxes

    public TotalSalesDetail(){

    }
    
}
