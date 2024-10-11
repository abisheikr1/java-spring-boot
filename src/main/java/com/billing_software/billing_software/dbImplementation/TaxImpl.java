package com.billing_software.billing_software.dbImplementation;

import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.billing_software.billing_software.dbInterface.TaxInterface;
import com.billing_software.billing_software.models.tax.Tax;
import com.billing_software.billing_software.repositories.TaxRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TaxImpl implements TaxInterface {

    private TaxRepository taxRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public TaxImpl(TaxRepository taxRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.taxRepository = taxRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(Tax taxData) {
        taxRepository.save(taxData).subscribe();
        return taxData.getId();
    }

    @Override
    public String update(Tax taxData) {
        Query query = Query.query(Criteria.where("_id").is(taxData.getId()));

        Update update = new Update();
        if (taxData.getName() != null)
            update.set("name", taxData.getName());
        if (taxData.getDescription() != null)
            update.set("description", taxData.getDescription());
        if (taxData.getTaxSlabs() != null)
            update.set("taxSlabs", taxData.getTaxSlabs());
        if (taxData.getOrgId() != null)
            update.set("orgId", taxData.getOrgId());
        if (taxData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", taxData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, Tax.class).subscribe();
        return taxData.getId();
    }

    @Override
    public Tax get(String taxId) {
        return taxRepository.findById(taxId).blockOptional().orElse(null);
    }

    @Override
    public List<Tax> getAllWithSearchText(String searchText) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(nameCriteria);
        query.addCriteria(Criteria.where("orgId").is(orgId));

        query.addCriteria(orCriteria);

        return reactiveMongoTemplate.find(query, Tax.class).collectList().blockOptional().orElse(null);
    }

    @Override
    public List<Tax> getAll() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();
        query.addCriteria(Criteria.where("orgId").is(orgId));

        return reactiveMongoTemplate.find(query, Tax.class).collectList().blockOptional().orElse(null);
    }

}
