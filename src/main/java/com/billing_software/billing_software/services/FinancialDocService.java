package com.billing_software.billing_software.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.billing_software.billing_software.dbInterface.AdditionalChargeInterface;
import com.billing_software.billing_software.dbInterface.CustomerInterface;
import com.billing_software.billing_software.dbInterface.FinanceDocumentInterface;
import com.billing_software.billing_software.dbInterface.ItemInterface;
import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.dbInterface.TaxInterface;
import com.billing_software.billing_software.models.additionalCharge.AdditionalCharge;
import com.billing_software.billing_software.models.customer.Customer;
import com.billing_software.billing_software.models.financialDocument.AdditionalChargeItem;
import com.billing_software.billing_software.models.financialDocument.DocumentItem;
import com.billing_software.billing_software.models.financialDocument.DocumentStatus;
import com.billing_software.billing_software.models.financialDocument.DocumentType;
import com.billing_software.billing_software.models.financialDocument.FinancialDocument;
import com.billing_software.billing_software.models.financialDocument.PaymentMode;
import com.billing_software.billing_software.models.item.Item;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.models.organization.Outlet;
import com.billing_software.billing_software.models.tax.Tax;
import com.billing_software.billing_software.payloads.requests.CustomerUpsertRequest;
import com.billing_software.billing_software.payloads.requests.FinancialDocUpsertRequest;
import com.billing_software.billing_software.utils.commons.EmailService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FinancialDocService {

    // Dependencies injected via constructor
    private final FinanceDocumentInterface financeDocumentInterface;
    private final OrganizationInterface organizationInterface;
    private final CustomerInterface customerInterface;
    private final ItemInterface itemInterface;
    private final AdditionalChargeInterface additionalChargeInterface;
    private final TaxInterface taxInterface;
    private final EmailService emailService;
    private final CustomerService customerService;

    // Constructor injection for interfaces
    public FinancialDocService(FinanceDocumentInterface financeDocumentInterface,
            OrganizationInterface organizationInterface,
            CustomerInterface customerInterface,
            ItemInterface itemInterface,
            TaxInterface taxInterface, EmailService emailService, AdditionalChargeInterface additionalChargeInterface,
            CustomerService customerService) {
        this.financeDocumentInterface = financeDocumentInterface;
        this.organizationInterface = organizationInterface;
        this.customerInterface = customerInterface;
        this.itemInterface = itemInterface;
        this.taxInterface = taxInterface;
        this.emailService = emailService;
        this.additionalChargeInterface = additionalChargeInterface;
        this.customerService = customerService;
    }

    /**
     * Creates or updates a financial document based on the incoming request.
     * Throws exceptions if customer, outlet, or org IDs are invalid.
     * Ensures that no duplicate invoice IDs exist.
     */
    public FinancialDocUpsertRequest createOrUpdateFinanceDoc(FinancialDocUpsertRequest financeRequest,
            Boolean isCreateFinanceDoc) {

        if (financeRequest.documentNumber != null) {
            FinancialDocument docDetail = financeDocumentInterface.getByDocNumber(financeRequest.documentNumber);
            if (financeRequest.id != null) {
                if (!docDetail.getId().equals(financeRequest.id)) {
                    throw new RuntimeException("Document Number alredy used");
                }
            } else {
                if (docDetail != null) {
                    throw new RuntimeException("Document Number alredy used");
                }
            }
        }

        // Validate customer ID
        if (financeRequest.customerId != null
                && customerInterface.get(financeRequest.customerId) == null) {
            throw new RuntimeException("Invalid customer Id");
        }

        // Validate outlet ID
        if (financeRequest.outletId == null) {
            throw new RuntimeException("Invalid outlet Id");
        }

        // Validate organization ID
        Organization orgDetails = organizationInterface.get(financeRequest.orgId);
        if (financeRequest.orgId != null && orgDetails == null) {
            throw new RuntimeException("Invalid org Id");
        }

        // Find outlet by outlet ID in the organization
        Outlet outletFound = orgDetails.getOutlets().stream()
                .filter(o -> o.getOutletId().equals(financeRequest.outletId))
                .findFirst()
                .orElse(null);

        // Process the request with calculated totals
        processFinanceDocRequest(financeRequest, isCreateFinanceDoc, orgDetails);

        if (isCreateFinanceDoc) {
            // Create a new FinancialDocument instance
            FinancialDocument financialDocData = new FinancialDocument(
                    financeRequest, outletFound.getSequenceConfigMap().get("invoice"));

            // Check for duplicate invoice ID
            FinancialDocument existingInvoice = financeDocumentInterface
                    .getByInvoiceId(financialDocData.getInvoiceId());
            if (existingInvoice != null) {
                throw new RuntimeException("Invoice Id duplication is not allowed");
            }

            // Update organization details
            organizationInterface.update(orgDetails);

            // If financial document ID is present, update the document
            if (financeRequest.id != null) {
                if (financeDocumentInterface.get(financeRequest.id) != null) {
                    financeDocumentInterface.update(financialDocData);
                    return financeRequest;
                } else {
                    throw new RuntimeException("Invalid financeDocument Id");
                }
            } else {
                // Otherwise, create a new document
                financeDocumentInterface.create(financialDocData);
                return financeRequest;
            }
        } else {
            return financeRequest;
        }

    }

    /**
     * Updates the status of a financial document by its ID.
     * Throws an exception if the document is not found.
     */
    public String updateDocStatus(String docId, DocumentStatus status, PaymentMode paymentMode) {
        // Fetch the document by ID (you may need to handle if the document is not
        // found)
        FinancialDocument document = getDocById(docId);

        if (document == null) {
            throw new RuntimeException("Document not found with ID: " + docId);
        }

        // Update the document status based on the input
        document.setStatus(status);

        if (status.equals(DocumentStatus.paid)) {
            if (paymentMode == null) {
                throw new RuntimeException("Payment mode not found");
            }
            document.setPaymentMode(paymentMode);
        }

        // Save the document after updating the status
        financeDocumentInterface.update(document); // Assuming you have a method for this

        return "Document status updated to: " + status;
    }

    /**
     * Fetches a financial document by ID.
     */
    public FinancialDocument getDocById(String docId) {
        return financeDocumentInterface.get(docId);
    }

    /**
     * Retrieves all financial documents with pagination and search filters.
     */
    public Map<String, Object> getAllFinaceDocWithSearch(String searchText, int pageNumber, int pageSize,
            String outletId, DocumentType docType, DocumentStatus status) {
        Map<String, Object> response = new HashMap<>();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        // Validate organization ID
        Organization orgDetails = organizationInterface.get(orgId);
        if (orgDetails == null) {
            throw new RuntimeException("Invalid org Id");
        }

        // Find outlet by outlet ID in the organization
        Outlet outletFound = orgDetails.getOutlets().stream()
                .filter(o -> o.getOutletId().equals(outletId))
                .findFirst()
                .orElse(null);

        // Validate outlet ID
        if (outletFound == null) {
            throw new RuntimeException("Invalid outlet Id");
        }

        response.put("financeDoc",
                financeDocumentInterface.getAllWithSearch(searchText, pageNumber, pageSize, outletId, docType, status));
        response.put("totalCount", financeDocumentInterface.getCount(searchText, outletId, docType, status));
        response.put("searchText", searchText);
        response.put("outletDetail", outletFound);

        return response;
    }

    /**
     * Processes the financial document request, calculating the totals.
     */
    private FinancialDocUpsertRequest processFinanceDocRequest(FinancialDocUpsertRequest financeDocReq,
            Boolean isCreateFinanceDoc, Organization orgDetails) {
        financeDocReq.acutualAmount = 0.00;
        financeDocReq.totalTax = 0.00;
        financeDocReq.totalDiscount = 0.00;

        validateCustomerIdOrCreate(financeDocReq);

        calculateItemTotals(financeDocReq, isCreateFinanceDoc, orgDetails);
        calculateAditionalChargeTotals(financeDocReq);

        financeDocReq.totalAmount = financeDocReq.acutualAmount + financeDocReq.totalTax;

        calculateDiscount(financeDocReq);
        return financeDocReq;
    }

    /**
     * Validates the customer ID if present.
     * Throws an exception if the customer is unavailable or invalid.
     * If there is no customer ID, attempts to set or create a new customer
     * based on the provided details. If a customer is found with the given
     * email or phone number, it assigns the ID to the request and updates the
     * customer details if needed.
     */
    private void validateCustomerIdOrCreate(FinancialDocUpsertRequest financeDocReq) {
        // If customer ID is present, validate the customer
        if (financeDocReq.customerId != null) {
            Customer customerDetail = customerInterface.get(financeDocReq.customerId);

            if (customerDetail == null) {
                throw new RuntimeException("Invalid Customer ID");
            }

            // Set customer details from the retrieved customer information
            financeDocReq.customerName = customerDetail.getName();
            financeDocReq.customerEmail = customerDetail.getEmailAddress();
            financeDocReq.customerNumber = customerDetail.getPhoneNumber();
            financeDocReq.billingAddress = customerDetail.getBillingAddress();
            financeDocReq.shippingAddress = customerDetail.getShippingAddress();
        }
        // If customer ID is not present, try to find or create a customer
        else {
            if (financeDocReq.customerEmail != null || financeDocReq.customerNumber != null) {

                List<Customer> customersFound = customerInterface.getByEmailOrPhoneNumber(
                        financeDocReq.customerEmail, financeDocReq.customerNumber);

                // If no customer is found, create a new one
                if (customersFound == null || customersFound.isEmpty()) {
                    CustomerUpsertRequest upsertRequest = new CustomerUpsertRequest();
                    upsertRequest.orgId = financeDocReq.orgId;
                    upsertRequest.outletId = financeDocReq.outletId;
                    upsertRequest.name = financeDocReq.customerName;
                    upsertRequest.emailAddress = financeDocReq.customerEmail;
                    upsertRequest.phoneNumber = financeDocReq.customerNumber;
                    upsertRequest.billingAddress = financeDocReq.billingAddress;
                    upsertRequest.shippingAddress = financeDocReq.shippingAddress;
                    String response = customerService.createOrUpdateCustomer(upsertRequest);
                    financeDocReq.customerId = response;
                }
                // If a customer is found, set their details to the request and update if
                // necessary
                else {
                    Customer foundCustomer = customersFound.get(0);
                    financeDocReq.customerId = foundCustomer.getId();

                    // Check if any fields in financeDocReq differ from foundCustomer, update
                    // customer
                    boolean needsUpdate = false;
                    CustomerUpsertRequest upsertRequest = new CustomerUpsertRequest();

                    if (financeDocReq.customerName != null
                            && !foundCustomer.getName().equals(financeDocReq.customerName)) {
                        upsertRequest.name = financeDocReq.customerName;
                        needsUpdate = true;
                    } else {
                        financeDocReq.customerName = foundCustomer.getName();
                    }
                    if (financeDocReq.customerEmail != null
                            && !foundCustomer.getEmailAddress().equals(financeDocReq.customerEmail)) {
                        upsertRequest.emailAddress = financeDocReq.customerEmail;
                        needsUpdate = true;
                    } else {
                        financeDocReq.customerEmail = foundCustomer.getEmailAddress();
                    }
                    if (financeDocReq.customerNumber != null
                            && !foundCustomer.getPhoneNumber().equals(financeDocReq.customerNumber)) {
                        upsertRequest.phoneNumber = financeDocReq.customerNumber;
                        needsUpdate = true;
                    } else {
                        financeDocReq.customerNumber = foundCustomer.getPhoneNumber();
                    }
                    if (financeDocReq.billingAddress != null
                            && !foundCustomer.getBillingAddress().equals(financeDocReq.billingAddress)) {
                        upsertRequest.billingAddress = financeDocReq.billingAddress;
                        needsUpdate = true;
                    } else {
                        financeDocReq.billingAddress = foundCustomer.getBillingAddress();
                    }
                    if (financeDocReq.shippingAddress != null
                            && !foundCustomer.getShippingAddress().equals(financeDocReq.shippingAddress)) {
                        upsertRequest.shippingAddress = financeDocReq.shippingAddress;
                        needsUpdate = true;
                    } else {
                        financeDocReq.shippingAddress = foundCustomer.getShippingAddress();
                    }

                    // If any update is needed, call the customer service to update the customer
                    if (needsUpdate) {
                        upsertRequest.id = foundCustomer.getId(); // Ensure the update is for the found customer
                        upsertRequest.orgId = financeDocReq.orgId;
                        upsertRequest.outletId = financeDocReq.outletId;
                        customerService.createOrUpdateCustomer(upsertRequest);
                    }
                }
            }
        }
    }

    /**
     * Calculates the total amount and taxes for the items in the request.
     * Throws exceptions if any item is unavailable or invalid.
     */
    private void calculateItemTotals(FinancialDocUpsertRequest financeDocReq, Boolean isCreateFinanceDoc,
            Organization orgDetails) {
        if (financeDocReq.items != null && !financeDocReq.items.isEmpty()) {
            for (DocumentItem product : financeDocReq.items) {
                Item itemDetail = itemInterface.get(product.getItemId());
                if (itemDetail != null) {
                    // Set item details in the product
                    product.setItemName(itemDetail.getName());
                    product.setDescription(itemDetail.getDescription());

                    product.setHsnCode(itemDetail.getHsnCode());
                    product.setSacCode(itemDetail.getSacCode());

                    // Check item availability and adjust stock
                    if (orgDetails != null && orgDetails.getOrgSettings() != null
                            && orgDetails.getOrgSettings().isInventoryEnabled()) {
                        if (product.getQuantity() > itemDetail.getAvailableStock()) {
                            throw new RuntimeException("Item quantity is unavailable");
                        } else {
                            itemDetail.setAvailableStock(itemDetail.getAvailableStock() - product.getQuantity());

                            if (isCreateFinanceDoc) {
                                itemInterface.update(itemDetail);
                            }
                        }
                    }

                    // Calculate item price
                    product.setActualPrice(itemDetail.getMsp() * product.getQuantity());

                    product.setTotalTax(0.00);
                    // Calculate taxes for the item
                    calculateTax(product, itemDetail);

                    product.setTotalPrice(product.getActualPrice() + product.getTotalTax());

                    // Add item price and taxes to total amounts
                    financeDocReq.acutualAmount += product.getActualPrice();
                    if (product.getTotalTax() != null) {
                        financeDocReq.totalTax += product.getTotalTax();
                    }
                } else {
                    // Handle unavailable item
                    throw new RuntimeException("Item not found");
                }
            }
        } else {
            throw new RuntimeException("Item list is empty");
        }
    }

    /**
     * Calculates the total amount for the additional charge items in the request.
     * Throws exceptions if any charge is unavailable or invalid.
     */
    private void calculateAditionalChargeTotals(FinancialDocUpsertRequest financeDocReq) {
        if (financeDocReq.additionalChargeItems != null && !financeDocReq.additionalChargeItems.isEmpty()) {
            for (AdditionalChargeItem addCharge : financeDocReq.additionalChargeItems) {
                AdditionalCharge chargeDetails = additionalChargeInterface.get(addCharge.getChargeId());
                if (chargeDetails != null) {
                    // Set item details in the product
                    addCharge.setChargeName(chargeDetails.getName());
                    addCharge.setDescription(chargeDetails.getDescription());

                    // Calculate item price
                    addCharge.setActualPrice(chargeDetails.getAmount() * addCharge.getQuantity());
                    addCharge.setTotalPrice(addCharge.getActualPrice());

                    // Add item price and taxes to total amounts
                    financeDocReq.acutualAmount += addCharge.getActualPrice();
                } else {
                    // Handle unavailable item
                    throw new RuntimeException("Charge not found");
                }
            }
        }
    }

    /**
     * Calculates the discount for the total amount.
     */
    private void calculateDiscount(FinancialDocUpsertRequest financeDocReq) {
        // This method applies discounts based on the specified discount amount and
        // percentage from the financeDocReq object.
        // It updates the total discount and total amount accordingly, ensuring that
        // both discounts are accounted for.
        if (financeDocReq.discountDetails != null) {
            if (financeDocReq.discountDetails.discountAmount != null
                    && financeDocReq.discountDetails.discountAmount > 0.00) {
                financeDocReq.totalDiscount = Math.round(financeDocReq.discountDetails.discountAmount * 100.0) / 100.0; // Round
                                                                                                                        // to
                                                                                                                        // 2
                                                                                                                        // decimal
                                                                                                                        // places
                financeDocReq.totalAmount -= financeDocReq.discountDetails.discountAmount;
                financeDocReq.totalAmount = Math.round(financeDocReq.totalAmount * 100.0) / 100.0; // Round to 2 decimal
                                                                                                   // places
            }
            if (financeDocReq.discountDetails.discountPercentage != null
                    && financeDocReq.discountDetails.discountPercentage > 0.00) {
                double percentageDiscount = (financeDocReq.totalAmount / 100)
                        * financeDocReq.discountDetails.discountPercentage;
                financeDocReq.totalDiscount += Math.round(percentageDiscount * 100.0) / 100.0; // Round to 2 decimal
                                                                                               // places
                financeDocReq.totalAmount -= percentageDiscount;
                financeDocReq.totalAmount = Math.round(financeDocReq.totalAmount * 100.0) / 100.0; // Round to 2 decimal
                                                                                                   // places
            }
        }
    }

    /**
     * Calculates the tax for a given product item based on tax slabs.
     */
    private void calculateTax(DocumentItem product, Item itemDetail) {
        if (itemDetail.getTaxId() != null) {
            Tax taxDetails = taxInterface.get(itemDetail.getTaxId());
            if (taxDetails != null) {
                double totalTax = 0.0;
                StringBuilder taxRateDesc = new StringBuilder();

                // Calculate tax for each slab
                for (Map.Entry<String, Double> entry : taxDetails.getTaxSlabs().entrySet()) {
                    double rate = entry.getValue();
                    double taxAmount = product.getActualPrice() * (rate / 100);
                    totalTax += taxAmount;

                    taxRateDesc.append(entry.getKey())
                            .append(" - ").append(rate).append("% (")
                            .append(taxAmount).append(") ");
                }

                // Set calculated tax details in the product
                product.setTaxRateDesc(taxRateDesc.toString());
                product.setTotalTax(totalTax);
            }
        }
    }

    /**
     * Sends a financial document to a specified email address using the outlet's
     * SMTP configuration.
     */
    public String sendFinanceDocInMail(String docId, String email, MultipartFile file) {
        // Retrieve the current HTTP request and extract the organization ID from the
        // request attributes
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        // Fetch the financial document based on the provided docId
        FinancialDocument financeDoc = financeDocumentInterface.get(docId);
        // Retrieve organization details using the orgId
        Organization orgDetails = organizationInterface.get(orgId);

        // Check if the financial document was found; if not, throw an exception
        if (financeDoc == null) {
            throw new RuntimeException("FinanceDoc not found");
        }

        // Find the outlet associated with the financial document within the
        // organization's outlets
        Outlet outletFound = orgDetails.getOutlets().stream()
                .filter(o -> o.getOutletId().equals(financeDoc.getOutletId()))
                .findFirst().orElse(null);

        // Send an email using the outlet's SMTP configuration
        emailService.sendEmail(
                outletFound.getSMTPConfig().get("username"),
                outletFound.getSMTPConfig().get("password"),
                (financeDoc.getCustomerEmail() != null) ? financeDoc.getCustomerEmail() : email, // Sending to the
                                                                                                 // specified email
                                                                                                 // parameter
                "Financial Document Submission", // Suggested subject
                "Please find the attached financial document.", file// Suggested body
        );

        // Return a success message after sending the email
        return "Mail sent successfully";
    }

}
