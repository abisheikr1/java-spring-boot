package com.billing_software.billing_software.dbInterface;

import java.util.List;

import com.billing_software.billing_software.models.organization.Organization;

public interface OrganizationInterface {

    public String create(Organization orgData);

    public String update(Organization orgData);

    public Organization get(String orgId);

    public Organization getMinimal(String orgId);

    public List<Organization> getAllWithSearch(String searchText);

}
