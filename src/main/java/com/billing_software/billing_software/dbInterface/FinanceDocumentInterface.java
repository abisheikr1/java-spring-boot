package com.billing_software.billing_software.dbInterface;

import java.util.Date;
import java.util.List;

import com.billing_software.billing_software.models.financialDocument.DocumentStatus;
import com.billing_software.billing_software.models.financialDocument.DocumentType;
import com.billing_software.billing_software.models.financialDocument.FinancialDocument;

public interface FinanceDocumentInterface {

    public String create(FinancialDocument documentData);

    public String update(FinancialDocument documentData);

    public FinancialDocument get(String documentId);

    public List<FinancialDocument> getAllWithSearch(String searchText, int pageNumber, int pageSize, String outletId,
            DocumentType docType, DocumentStatus status);

    public Long getCount(String searchText, String outletId, DocumentType docType, DocumentStatus status);

    public FinancialDocument getByInvoiceId(String invoiceId);

    public FinancialDocument getByDocNumber(String docNumber);

    public List<FinancialDocument> getByDateRange(Date startDate, Date endDate, DocumentStatus status);

}
