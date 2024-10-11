package com.billing_software.billing_software.payloads.response.reports;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancledSalesDetail {
    private Integer noOfOrdersCancelled;
    private Double totalRevenueCancelled;
    private Double totalTaxCancelled;
    private Double totalDiscountGiven;
    private Double actualRevenueCancelled;
}
