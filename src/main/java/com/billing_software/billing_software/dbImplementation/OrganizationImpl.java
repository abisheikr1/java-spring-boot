package com.billing_software.billing_software.dbImplementation;

import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.repositories.OrganizationRepository;

@Service
public class OrganizationImpl implements OrganizationInterface {

    private OrganizationRepository organizationRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public OrganizationImpl(OrganizationRepository organizationRepository,
            ReactiveMongoTemplate reactiveMongoTemplate) {
        this.organizationRepository = organizationRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(Organization orgData) {
        organizationRepository.save(orgData).subscribe();
        return orgData.getId();
    }

    @Override
    public String update(Organization orgData) {
        Query query = Query.query(Criteria.where("_id").is(orgData.getId()));

        Update update = new Update();
        if (orgData.getName() != null)
            update.set("name", orgData.getName());
        if (orgData.getPhoneNumber() != null)
            update.set("phoneNumber", orgData.getPhoneNumber());
        if (orgData.getEmail() != null)
            update.set("email", orgData.getEmail());
        if (orgData.getBillingAddress() != null)
            update.set("billingAddress", orgData.getBillingAddress());
        if (orgData.getShippingAddress() != null)
            update.set("shippingAddress", orgData.getShippingAddress());
        if (orgData.getWebsite() != null)
            update.set("website", orgData.getWebsite());
        if (orgData.getIndustryType() != null)
            update.set("industryType", orgData.getIndustryType());
        if (orgData.getOutlets() != null)
            update.set("outlets", orgData.getOutlets());
        if (orgData.getSubscription() != null)
            update.set("subscription", orgData.getSubscription());
        if (orgData.getTheme() != null)
            update.set("theme", orgData.getTheme());
        if (orgData.getLogo() != null)
            update.set("logo", orgData.getLogo());
        if (orgData.getGstin() != null)
            update.set("gstin", orgData.getGstin());
        if (orgData.getOrgSettings() != null)
            update.set("orgSettings", orgData.getOrgSettings());
        if (orgData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", orgData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, Organization.class).subscribe();
        return orgData.getId();
    }

    @Override
    public Organization get(String orgId) {
        return organizationRepository.findByOrgIdOrShortId(orgId).blockOptional().orElse(null);
    }

    @Override
    public Organization getMinimal(String orgId) {
        return organizationRepository.findByOrgIdOrShortIdMinimal(orgId).blockOptional().orElse(null);
    }

    @Override
    public List<Organization> getAllWithSearch(String searchText) {
        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");
        Criteria shortIdCriteria = Criteria.where("shortId").regex(".*" + searchText + ".*", "i");
        Criteria phoneNumberCriteria = Criteria.where("phoneNumber").regex(".*" + searchText + ".*", "i");
        Criteria emailCriteria = Criteria.where("email").regex(".*" + searchText + ".*", "i");
        Criteria industryTypeCriteria = Criteria.where("industryType").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                shortIdCriteria,
                phoneNumberCriteria,
                emailCriteria,
                industryTypeCriteria);
        query.addCriteria(orCriteria);

        return reactiveMongoTemplate.find(query, Organization.class).collectList().blockOptional().orElse(null);
    }

}
