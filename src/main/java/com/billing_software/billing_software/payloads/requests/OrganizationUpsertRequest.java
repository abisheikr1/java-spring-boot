package com.billing_software.billing_software.payloads.requests;

import java.util.List;
import java.util.Map;

import com.billing_software.billing_software.models.organization.OrgSettings;
import com.billing_software.billing_software.models.organization.Outlet;

public class OrganizationUpsertRequest {

    public String id;

    public String shortId;
    public String name;
    public String phoneNumber;
    public String email;
    public String billingAddress;
    public String shippingAddress;
    public String website;
    public String gstin;
    public String industryType;
    public Map<String, Object> theme;
    public OrgSettings orgSettings;

}
