package com.billing_software.billing_software.services;

import org.springframework.stereotype.Service;
import com.billing_software.billing_software.dbInterface.FinanceDocumentInterface;
import com.billing_software.billing_software.models.financialDocument.DocumentItem;
import com.billing_software.billing_software.models.financialDocument.DocumentStatus;
import com.billing_software.billing_software.models.financialDocument.FinancialDocument;
import com.billing_software.billing_software.payloads.response.reports.CancledSalesDetail;
import com.billing_software.billing_software.payloads.response.reports.CustomerSalesDetails;
import com.billing_software.billing_software.payloads.response.reports.PendingSalesDetail;
import com.billing_software.billing_software.payloads.response.reports.ProductSalesDetails;
import com.billing_software.billing_software.payloads.response.reports.ReportResponse;
import com.billing_software.billing_software.payloads.response.reports.TotalSalesDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final FinanceDocumentInterface financeDocumentInterface;

    // Constructor-based dependency injection for FinanceDocumentInterface
    public ReportService(FinanceDocumentInterface financeDocumentInterface) {
        this.financeDocumentInterface = financeDocumentInterface;
    }

    /**
     * Generates a report based on the given date range (startDate and endDate).
     *
     * @param startDate The start date of the report (as a String).
     * @param endDate   The end date of the report (as a String).
     * @return ReportResponse containing the report data for the given date range.
     */
    public ReportResponse generateReport(String startDate, String endDate, String reportType) {
        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setFromDate(startDate);
        reportResponse.setToDate(endDate);

        // Fetch financial documents within the date range from the database
        List<FinancialDocument> financeDocList = financeDocumentInterface.getByDateRange(
                convertStringToDate(startDate), convertStringToDate(endDate), DocumentStatus.paid);
        reportResponse.setFinanceDocList(financeDocList);

        reportResponse.setTotalSalesDetails(generateTotalSalesReport(financeDocList));

        if (reportType.equals("SalesSummaryReport")) {
            reportResponse.setProductSalesDetails(this.generateProductSalesReport(financeDocList));
            reportResponse.setCustomerSalesDetails(this.generateCustomerSalesReport(financeDocList));
        }

        List<FinancialDocument> cancelledFinanceDocList = financeDocumentInterface.getByDateRange(
                convertStringToDate(startDate), convertStringToDate(endDate), DocumentStatus.cancelled);
        List<FinancialDocument> pendingFinanceDocList = financeDocumentInterface.getByDateRange(
                convertStringToDate(startDate), convertStringToDate(endDate), DocumentStatus.pending);

        reportResponse.setCancledSalesDetail(generateTotalCancelledReport(cancelledFinanceDocList));
        reportResponse.setPendingSalesDetail(generateTotalPendingReport(pendingFinanceDocList));

        return reportResponse;
    }

    private List<ProductSalesDetails> generateProductSalesReport(List<FinancialDocument> financeDocList) {
        // Map to hold aggregated product sales details
        Map<String, ProductSalesDetails> productSalesMap = new HashMap<>();

        // Iterate over each financial document to gather product details
        for (FinancialDocument document : financeDocList) {
            if (document.getItems() != null) {
                for (DocumentItem item : document.getItems()) {
                    String productId = item.getItemId(); // Assuming DocumentItem has a method getProductId()
                    Double itemRevenue = item.getTotalPrice(); // Assuming DocumentItem has methods
                                                               // getQuantity() and getPrice()
                    Double itemTax = item.getTotalTax(); // Assuming DocumentItem has a method getTax()

                    // If the product is not already in the map, initialize it
                    ProductSalesDetails salesDetails = productSalesMap.getOrDefault(productId,
                            new ProductSalesDetails());
                    salesDetails.setId(productId);
                    salesDetails.setName(item.getItemName()); // Assuming DocumentItem has a method getProductName()

                    // Update aggregated values
                    salesDetails.setNumberOfUnitsSold(salesDetails.getNumberOfUnitsSold() + item.getQuantity());
                    salesDetails.setTotalRevenueMade(salesDetails.getTotalRevenueMade() + itemRevenue);
                    salesDetails.setTotalTaxCollected(salesDetails.getTotalTaxCollected() + itemTax);

                    // Calculate actual revenue made
                    salesDetails.setActualRevenueMade(salesDetails.getTotalRevenueMade()
                            - salesDetails.getTotalDiscountGiven() - salesDetails.getTotalTaxCollected());

                    // Put the updated sales details back in the map
                    productSalesMap.put(productId, salesDetails);
                }
            }
        }

        // Convert map values to a list
        List<ProductSalesDetails> productSalesDetails = new ArrayList<>(productSalesMap.values());

        return productSalesDetails;
    }

    private List<CustomerSalesDetails> generateCustomerSalesReport(List<FinancialDocument> financeDocList) {
        // Map to hold aggregated customer sales details
        Map<String, CustomerSalesDetails> customerSalesMap = new HashMap<>();

        // Iterate over each financial document
        for (FinancialDocument document : financeDocList) {
            String customerId = document.getCustomerId(); // Assuming FinancialDocument has a method getCustomerId()
            Double totalRevenue = document.getTotalAmount(); // Assuming FinancialDocument has a method getTotal()
            Double totalTax = document.getTotalTax() != null ? document.getTotalTax() : 0.0; // Assuming
                                                                                             // FinancialDocument has a
                                                                                             // method getTotalTax()
            Double totalDiscount = (document.getTotalDiscount() != null) ? document.getTotalDiscount() : 0.0;

            // If the customer is not already in the map, initialize it
            CustomerSalesDetails salesDetails = customerSalesMap.getOrDefault(customerId,
                    new CustomerSalesDetails());

            salesDetails.setNoOfOrdersMade(salesDetails.getNoOfOrdersMade() + 1);
            salesDetails.setId(customerId);
            // Assuming Customer object exists - retrieve name from related customer object
            salesDetails.setName(document.getCustomerName());

            // Update aggregated values
            salesDetails.setTotalRevenueMade(salesDetails.getTotalRevenueMade() + totalRevenue);
            salesDetails.setTotalTaxCollected(salesDetails.getTotalTaxCollected() + totalTax);
            salesDetails.setTotalDiscountGiven(salesDetails.getTotalDiscountGiven() + totalDiscount);

            // Calculate actual revenue made
            salesDetails.setActualRevenueMade(salesDetails.getTotalRevenueMade()
                    - salesDetails.getTotalDiscountGiven() - salesDetails.getTotalTaxCollected());

            // Put the updated sales details back in the map
            customerSalesMap.put(customerId, salesDetails);
        }

        // Convert map values to a list
        List<CustomerSalesDetails> customerSalesDetails = new ArrayList<>(customerSalesMap.values());

        return customerSalesDetails;
    }

    private PendingSalesDetail generateTotalPendingReport(List<FinancialDocument> financeDocList) {
        PendingSalesDetail pendingSalesDetail = new PendingSalesDetail();
        pendingSalesDetail.setNoOfOrdersPending(0);
        pendingSalesDetail.setTotalTaxPending(0.0);
        pendingSalesDetail.setTotalDiscountGiven(0.0);
        pendingSalesDetail.setActualRevenuePending(0.0);

        for (FinancialDocument document : financeDocList) {
            pendingSalesDetail.setNoOfOrdersPending(pendingSalesDetail.getNoOfOrdersPending() + 1);
            pendingSalesDetail.setTotalTaxPending(pendingSalesDetail.getTotalTaxPending()
                    + (document.getTotalTax() != null ? document.getTotalTax() : 0.0));
            pendingSalesDetail
                    .setTotalDiscountGiven(pendingSalesDetail.getTotalDiscountGiven()
                            + (document.getTotalDiscount() != null ? document.getTotalDiscount() : 0.0));
            pendingSalesDetail
                    .setActualRevenuePending(pendingSalesDetail.getActualRevenuePending() + document.getTotalAmount());
        }

        return pendingSalesDetail;
    }

    private CancledSalesDetail generateTotalCancelledReport(List<FinancialDocument> financeDocList) {
        CancledSalesDetail totalCancelledDetails = new CancledSalesDetail();
        totalCancelledDetails.setNoOfOrdersCancelled(0);
        totalCancelledDetails.setTotalTaxCancelled(0.0);
        totalCancelledDetails.setTotalDiscountGiven(0.0);
        totalCancelledDetails.setActualRevenueCancelled(0.0);

        for (FinancialDocument document : financeDocList) {
            totalCancelledDetails.setNoOfOrdersCancelled(totalCancelledDetails.getNoOfOrdersCancelled() + 1);
            totalCancelledDetails.setTotalTaxCancelled(totalCancelledDetails.getTotalTaxCancelled()
                    + (document.getTotalTax() != null ? document.getTotalTax() : 0.0));
            totalCancelledDetails
                    .setTotalDiscountGiven(totalCancelledDetails.getTotalDiscountGiven()
                            + (document.getTotalDiscount() != null ? document.getTotalDiscount() : 0.0));
            totalCancelledDetails.setActualRevenueCancelled(
                    totalCancelledDetails.getActualRevenueCancelled() + document.getTotalAmount());
        }

        return totalCancelledDetails;
    }

    private TotalSalesDetail generateTotalSalesReport(List<FinancialDocument> financeDocList) {
        TotalSalesDetail totalSalesdetail = new TotalSalesDetail();
        totalSalesdetail.setNoOfOrdersMade(0);
        totalSalesdetail.setTotalTaxCollected(0.0);
        totalSalesdetail.setTotalDiscountGiven(0.0);
        totalSalesdetail.setActualRevenueMade(0.0);

        for (FinancialDocument document : financeDocList) {
            totalSalesdetail.setNoOfOrdersMade(totalSalesdetail.getNoOfOrdersMade() + 1);
            totalSalesdetail.setTotalTaxCollected(totalSalesdetail.getTotalTaxCollected()
                    + (document.getTotalTax() != null ? document.getTotalTax() : 0.0));
            totalSalesdetail
                    .setTotalDiscountGiven(totalSalesdetail.getTotalDiscountGiven()
                            + (document.getTotalDiscount() != null ? document.getTotalDiscount() : 0.0));
            totalSalesdetail.setActualRevenueMade(totalSalesdetail.getActualRevenueMade() + document.getTotalAmount());
        }

        return totalSalesdetail;
    }

    /**
     * Converts a date string in "dd/MM/yyyy" format into a Date object.
     *
     * @param dateString The date string to be converted.
     * @return The Date object parsed from the string.
     */
    public Date convertStringToDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.parse(dateString);
        } catch (Exception ex) {
            // RuntimeException is thrown if the date string cannot be parsed
            throw new RuntimeException("Error parsing date: " + ex.getMessage());
        }
    }
}
