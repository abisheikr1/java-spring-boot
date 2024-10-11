package com.billing_software.billing_software.dbImplementation;

import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.billing_software.billing_software.dbInterface.AdditionalChargeInterface;
import com.billing_software.billing_software.models.additionalCharge.AdditionalCharge;
import com.billing_software.billing_software.repositories.AdditionalChargeRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class AdditionalChargeImpl implements AdditionalChargeInterface {

    private AdditionalChargeRepository additionalChargeRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public AdditionalChargeImpl(AdditionalChargeRepository additionalChargeRepository,
            ReactiveMongoTemplate reactiveMongoTemplate) {
        this.additionalChargeRepository = additionalChargeRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(AdditionalCharge additionalChargeData) {
        additionalChargeRepository.save(additionalChargeData).subscribe();
        return additionalChargeData.getId();
    }

    @Override
    public String update(AdditionalCharge additionalChargeData) {
        Query query = Query.query(Criteria.where("_id").is(additionalChargeData.getId()));

        Update update = new Update();
        if (additionalChargeData.getName() != null)
            update.set("name", additionalChargeData.getName());
        if (additionalChargeData.getAmount() != null)
            update.set("amount", additionalChargeData.getAmount());
        if (additionalChargeData.getDescription() != null)
            update.set("description", additionalChargeData.getDescription());
        if (additionalChargeData.getOrgId() != null)
            update.set("orgId", additionalChargeData.getOrgId());
        if (additionalChargeData.getCurrency() != null)
            update.set("currency", additionalChargeData.getCurrency());
        if (additionalChargeData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", additionalChargeData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, AdditionalCharge.class).subscribe();
        return additionalChargeData.getId();
    }

    @Override
    public AdditionalCharge get(String additionalChargeId) {
        return additionalChargeRepository.findById(additionalChargeId).blockOptional().orElse(null);

    }

    @Override
    public List<AdditionalCharge> getAllWithSearchText(String searchText) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(nameCriteria);
        query.addCriteria(Criteria.where("orgId").is(orgId));

        query.addCriteria(orCriteria);

        return reactiveMongoTemplate.find(query, AdditionalCharge.class).collectList().blockOptional().orElse(null);
    }

    @Override
    public List<AdditionalCharge> getAll() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();
        query.addCriteria(Criteria.where("orgId").is(orgId));

        return reactiveMongoTemplate.find(query, AdditionalCharge.class).collectList().blockOptional().orElse(null);
    }

}
