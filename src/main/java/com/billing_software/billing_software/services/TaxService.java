package com.billing_software.billing_software.services;

import org.springframework.stereotype.Service;
import java.util.List;

import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.dbInterface.TaxInterface;
import com.billing_software.billing_software.models.tax.Tax;
import com.billing_software.billing_software.payloads.requests.TaxUpsertRequest;

@Service
public class TaxService {

    private TaxInterface taxInterface;
    private OrganizationInterface organizationInterface;

    public TaxService(TaxInterface taxInterface, OrganizationInterface organizationInterface) {
        this.taxInterface = taxInterface;
        this.organizationInterface = organizationInterface;
    }

    public String createOrUpdateTax(TaxUpsertRequest taxUpsertRequest) {
        Tax taxData = new Tax(taxUpsertRequest);

        if (taxUpsertRequest.orgId != null && organizationInterface.get(taxUpsertRequest.orgId) == null) {
            throw new RuntimeException("Invalid org Id");
        }

        if (taxUpsertRequest.id != null) {
            if (taxInterface.get(taxUpsertRequest.id) != null) {
                return taxInterface.update(taxData);
            } else {
                throw new RuntimeException("Invalid tax Id");
            }
        } else {
            return taxInterface.create(taxData);
        }
    }

    public Tax getTaxById(String taxId) {
        return taxInterface.get(taxId);
    }

    public List<Tax> getAllTaxWithSearch(String searchtext) {
        return taxInterface.getAllWithSearchText(searchtext);
    }

}
