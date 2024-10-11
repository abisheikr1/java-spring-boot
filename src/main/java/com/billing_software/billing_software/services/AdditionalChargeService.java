package com.billing_software.billing_software.services;

import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.AdditionalChargeInterface;
import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.models.additionalCharge.AdditionalCharge;
import com.billing_software.billing_software.payloads.requests.AdditionalChargeUpsertRequest;
import java.util.List;

@Service
public class AdditionalChargeService {

    private AdditionalChargeInterface additionalChargeInterface;
    private OrganizationInterface organizationInterface;

    public AdditionalChargeService(AdditionalChargeInterface additionalChargeInterface,
            OrganizationInterface organizationInterface) {
        this.additionalChargeInterface = additionalChargeInterface;
        this.organizationInterface = organizationInterface;
    }

    public String createOrUpdateAdditionalCharge(AdditionalChargeUpsertRequest additionalChargeUpsertRequest) {
        AdditionalCharge additionalChargeData = new AdditionalCharge(additionalChargeUpsertRequest);

        if (additionalChargeUpsertRequest.orgId != null
                && organizationInterface.get(additionalChargeUpsertRequest.orgId) == null) {
            throw new RuntimeException("Invalid org Id");
        }

        if (additionalChargeUpsertRequest.id != null) {
            if (additionalChargeInterface.get(additionalChargeUpsertRequest.id) != null) {
                return additionalChargeInterface.update(additionalChargeData);
            } else {
                throw new RuntimeException("Invalid additionalCharge Id");
            }
        } else {
            return additionalChargeInterface.create(additionalChargeData);
        }
    }

    public AdditionalCharge getAdditionalChargeById(String additionalChargeId) {
        return additionalChargeInterface.get(additionalChargeId);
    }

    public List<AdditionalCharge> getAllAdditionalChargeWithSearch(String searchtext) {
        return additionalChargeInterface.getAllWithSearchText(searchtext);
    }

}
