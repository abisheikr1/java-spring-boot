package com.billing_software.billing_software.payloads.requests;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.billing_software.billing_software.models.financialDocument.AdditionalChargeItem;
import com.billing_software.billing_software.models.financialDocument.DiscountDetail;
import com.billing_software.billing_software.models.financialDocument.DocumentStatus;
import com.billing_software.billing_software.models.financialDocument.DocumentItem;
import com.billing_software.billing_software.models.financialDocument.DocumentType;

public class FinancialDocUpsertRequest  {

    public String id;
    public String orgId;
    public DocumentType documentType;
    public String documentNumber;
    public Date documentDate;
    public String customerId;
    public String customerName;
    public String customerNumber;
    public String customerEmail;
    public String billingAddress;
    public String shippingAddress;
    public List<DocumentItem> items;
    public List<AdditionalChargeItem> additionalChargeItems;
    public Double totalDiscount;
    public Double totalAmount;
    public Double totalTax;
    public Double acutualAmount;
    public Double amountPaid;
    public Double outstandingAmount;
    public DocumentStatus status;
    public String paymentTerms;
    public String outletId;
    public Map<String, List<String>> terms;
    public DiscountDetail discountDetails;

}
