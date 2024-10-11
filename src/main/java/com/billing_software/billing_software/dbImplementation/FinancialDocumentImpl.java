package com.billing_software.billing_software.dbImplementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.billing_software.billing_software.dbInterface.FinanceDocumentInterface;
import com.billing_software.billing_software.models.financialDocument.DocumentStatus;
import com.billing_software.billing_software.models.financialDocument.DocumentType;
import com.billing_software.billing_software.models.financialDocument.FinancialDocument;
import com.billing_software.billing_software.repositories.FinancialDocumentRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class FinancialDocumentImpl implements FinanceDocumentInterface {

    private FinancialDocumentRepository financialDocumentRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public FinancialDocumentImpl(FinancialDocumentRepository financialDocumentRepository,
            ReactiveMongoTemplate reactiveMongoTemplate) {
        this.financialDocumentRepository = financialDocumentRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(FinancialDocument documentData) {
        financialDocumentRepository.save(documentData).subscribe();
        return documentData.getId();
    }

    @Override
    public String update(FinancialDocument documentData) {
        Query query = Query.query(Criteria.where("_id").is(documentData.getId()));

        Update update = new Update();
        if (documentData.getOrgId() != null)
            update.set("orgId", documentData.getOrgId());
        if (documentData.getDocumentType() != null)
            update.set("documentType", documentData.getDocumentType());
        if (documentData.getDocumentNumber() != null)
            update.set("documentNumber", documentData.getDocumentNumber());
        if (documentData.getDocumentDate() != null)
            update.set("documentDate", documentData.getDocumentDate());
        if (documentData.getCustomerId() != null)
            update.set("customerId", documentData.getCustomerId());
        if (documentData.getCustomerName() != null)
            update.set("customerName", documentData.getCustomerName());
        if (documentData.getBillingAddress() != null)
            update.set("billingAddress", documentData.getBillingAddress());
        if (documentData.getItems() != null)
            update.set("items", documentData.getItems());
        if (documentData.getTotalAmount() != null)
            update.set("totalAmount", documentData.getTotalAmount());
        if (documentData.getAmountPaid() != null)
            update.set("amountPaid", documentData.getAmountPaid());
        if (documentData.getOutstandingAmount() != null)
            update.set("outstandingAmount", documentData.getOutstandingAmount());
        if (documentData.getStatus() != null)
            update.set("status", documentData.getStatus());
        if (documentData.getPaymentMode() != null)
            update.set("paymentMode", documentData.getPaymentMode());
        if (documentData.getPaymentTerms() != null)
            update.set("paymentTerms", documentData.getPaymentTerms());
        if (documentData.getShippingAddress() != null)
            update.set("shippingAddress", documentData.getShippingAddress());
        if (documentData.getCustomerNumber() != null)
            update.set("customerNumber", documentData.getCustomerNumber());
        if (documentData.getCustomerEmail() != null)
            update.set("customerEmail", documentData.getCustomerEmail());
        if (documentData.getTerms() != null)
            update.set("terms", documentData.getTerms());
        if (documentData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", documentData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, FinancialDocument.class).subscribe();
        return documentData.getId();
    }

    @Override
    public FinancialDocument get(String documentId) {
        return financialDocumentRepository.findById(documentId).blockOptional().orElse(null);
    }

    @Override
    public List<FinancialDocument> getAllWithSearch(String searchText, int pageNumber, int pageSize, String outletId,
            DocumentType docType, DocumentStatus status) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("customerName").regex(".*" + searchText + ".*", "i");
        Criteria customerCodeCriteria = Criteria.where("customerId").regex(".*" + searchText + ".*", "i");
        Criteria phoneNumberCriteria = Criteria.where("documentNumber").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                customerCodeCriteria,
                phoneNumberCriteria);
        query.addCriteria(orCriteria);

        query.addCriteria(Criteria.where("orgId").is(orgId));
        query.addCriteria(Criteria.where("outletId").is(outletId));
        query.addCriteria(Criteria.where("documentType").is(docType));
        query.addCriteria(Criteria.where("status").is(status));
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        query.skip(pageable.getOffset());
        query.limit(pageable.getPageSize());

        return reactiveMongoTemplate.find(query, FinancialDocument.class).collectList().blockOptional().orElse(null);
    }

    @Override
    public Long getCount(String searchText, String outletId, DocumentType docType, DocumentStatus status) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        Criteria nameCriteria = Criteria.where("customerName").regex(".*" + searchText + ".*", "i");
        Criteria customerCodeCriteria = Criteria.where("customerId").regex(".*" + searchText + ".*", "i");
        Criteria phoneNumberCriteria = Criteria.where("documentNumber").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                customerCodeCriteria,
                phoneNumberCriteria);
        query.addCriteria(orCriteria);

        query.addCriteria(Criteria.where("orgId").is(orgId));
        query.addCriteria(Criteria.where("outletId").is(outletId));
        query.addCriteria(Criteria.where("documentType").is(docType));
        query.addCriteria(Criteria.where("status").is(status));

        return reactiveMongoTemplate.count(query, FinancialDocument.class).blockOptional().orElse(0L);
    }

    @Override
    public FinancialDocument getByInvoiceId(String invoiceId) {
        return financialDocumentRepository.findAllByInvoiceId(invoiceId).blockOptional().orElse(null);
    }

    @Override
    public FinancialDocument getByDocNumber(String docNumber) {
        return financialDocumentRepository.findByDocNumber(docNumber).blockOptional().orElse(null);

    }

    @Override
    public List<FinancialDocument> getByDateRange(Date startDate, Date endDate, DocumentStatus status) {
        List<Criteria> andCriterias = new ArrayList<>();
        andCriterias.add(Criteria.where("documentDate").gte(startDate).lte(endDate));
        Criteria criteria = new Criteria().andOperator(andCriterias.toArray(new Criteria[andCriterias.size()]));

        Query query = new Query().addCriteria(criteria);
        query.addCriteria(Criteria.where("status").is(status));

        return reactiveMongoTemplate.find(query, FinancialDocument.class).collectList().blockOptional()
                .orElse(Collections.emptyList());
    }

}
