package com.billing_software.billing_software.payloads.response.reports;

import java.util.List;

import com.billing_software.billing_software.models.financialDocument.FinancialDocument;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponse {

    private String fromDate;
    private String toDate;
    private List<FinancialDocument> financeDocList;

    // Sales Report
    private TotalSalesDetail totalSalesDetails;

    private PendingSalesDetail pendingSalesDetail;
    private CancledSalesDetail CancledSalesDetail;


    private List<ProductSalesDetails> productSalesDetails;
    private List<CustomerSalesDetails> customerSalesDetails;

}
