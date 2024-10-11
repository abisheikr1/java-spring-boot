package com.billing_software.billing_software.dbInterface;

import java.util.List;

import com.billing_software.billing_software.models.additionalCharge.AdditionalCharge;

public interface AdditionalChargeInterface {

    public String create(AdditionalCharge additionalChargeData);

    public String update(AdditionalCharge additionalChargeData);

    public AdditionalCharge get(String additionalChargeId);

    public List<AdditionalCharge> getAll();

    public List<AdditionalCharge> getAllWithSearchText(String searchText);

}
