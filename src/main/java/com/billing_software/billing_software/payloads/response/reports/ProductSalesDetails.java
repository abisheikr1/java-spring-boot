package com.billing_software.billing_software.payloads.response.reports;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSalesDetails {

    private String id; // Unique identifier for the product, typically a SKU or database ID
    private String name; // Name of the product as it appears to customers
    private Integer numberOfUnitsSold; // Total number of units sold during the reporting period
    private Double totalRevenueMade; // Total revenue generated from sales of this product
    private Double totalTaxCollected; // Total tax collected on sales of this product
    private Double totalDiscountGiven; // Total amount of discounts applied to this product's sales
    private Double actualRevenueMade; // Net revenue after deducting discounts and taxes

    public ProductSalesDetails() {
        this.numberOfUnitsSold = 0;
        this.totalTaxCollected = 0.0;
        this.totalRevenueMade = 0.0;
        this.totalDiscountGiven = 0.0;
        this.actualRevenueMade = 0.0;
    }
}
