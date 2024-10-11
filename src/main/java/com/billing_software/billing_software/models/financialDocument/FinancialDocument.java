package com.billing_software.billing_software.models.financialDocument;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.models.organization.sequence.SequenceConfig;
import com.billing_software.billing_software.payloads.requests.FinancialDocUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("financial_documents")
public class FinancialDocument extends CoreContext {

    @Transient
    private static String shortCode = "FDOC";

    private String id; // Unique identifier for the transaction
    private String orgId; // Belongs to organization
    private DocumentType documentType; // Type of document (e.g., "Invoice", "Bill", "SalesOrder", "Quotation")
    private String documentNumber; // Unique number for the document
    private Date documentDate; // Date of the document
    private String customerId; // Customer associated with the transaction
    private String customerName; // Name of the customer
    private String customerNumber; // number of the customer
    private String customerEmail; // email of the customer
    private String billingAddress; // Billing address of the customer
    private String shippingAddress; // shipping address of the customer
    private List<DocumentItem> items; // List of items included in the transaction
    private List<AdditionalChargeItem> additionalChargeItems; // List of additional charge items included in the
                                                              // transaction
    private Double totalDiscount; // total discount applied for document
    private Double totalAmount; // Total amount of the transaction
    private Double totalTax;
    private Double amountPaid; // Amount paid (if applicable)
    private Double outstandingAmount; // Amount remaining to be paid (if applicable)
    private DocumentStatus status; // Status of the transaction (e.g., "Pending", "Paid", "Cancelled")
    private String paymentTerms; // Payment terms (e.g., "Net 30")
    private String outletId; // belongs to outlet

    private Map<String, List<String>> terms; // Terms and conditions

    private String invoiceId; // Invoice id for finance Doc

    private DiscountDetail discountDetails;

    private PaymentMode paymentMode; // paymentMode


    public FinancialDocument(FinancialDocUpsertRequest finaclialDocUpsertRequest, SequenceConfig seqConfig) {
        if (finaclialDocUpsertRequest.id != null) {
            this.id = finaclialDocUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(FinancialDocument.shortCode);
            this.setCreatedDate(new Date());
        }

        this.orgId = finaclialDocUpsertRequest.orgId;
        this.documentType = finaclialDocUpsertRequest.documentType;
        this.documentNumber = finaclialDocUpsertRequest.documentNumber;
        this.documentDate = finaclialDocUpsertRequest.documentDate;
        this.customerId = finaclialDocUpsertRequest.customerId;
        this.customerName = finaclialDocUpsertRequest.customerName;
        this.customerNumber = finaclialDocUpsertRequest.customerNumber;
        this.customerEmail = finaclialDocUpsertRequest.customerEmail;
        this.billingAddress = finaclialDocUpsertRequest.billingAddress;
        this.shippingAddress = finaclialDocUpsertRequest.shippingAddress;
        this.items = finaclialDocUpsertRequest.items;
        this.additionalChargeItems = finaclialDocUpsertRequest.additionalChargeItems;
        this.totalAmount = finaclialDocUpsertRequest.totalAmount;
        this.totalTax = finaclialDocUpsertRequest.totalTax;
        this.totalDiscount = finaclialDocUpsertRequest.totalDiscount;
        this.amountPaid = finaclialDocUpsertRequest.amountPaid;
        this.outstandingAmount = finaclialDocUpsertRequest.outstandingAmount;
        this.status = DocumentStatus.pending;
        this.paymentTerms = finaclialDocUpsertRequest.paymentTerms;
        this.outletId = finaclialDocUpsertRequest.outletId;
        this.terms = finaclialDocUpsertRequest.terms;
        this.discountDetails = finaclialDocUpsertRequest.discountDetails;

        this.generateInvoiceId(seqConfig);
    }

    private void generateInvoiceId(SequenceConfig seqConfig) {
        StringBuilder invoiceId = new StringBuilder(seqConfig.getSequencePrefix());

        String dateFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());
        invoiceId.append("-").append(dateFormat);

        String formattedSequence = String.format("%0" + seqConfig.getSequenceLength() + "d",
                seqConfig.getSequenceNumber());
        invoiceId.append("-").append(formattedSequence);

        seqConfig.setSequenceNumber(seqConfig.getSequenceNumber() + 1);

        this.invoiceId = invoiceId.toString();
    }

}
