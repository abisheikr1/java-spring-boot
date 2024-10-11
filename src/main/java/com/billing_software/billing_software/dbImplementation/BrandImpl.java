package com.billing_software.billing_software.dbImplementation;

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

import com.billing_software.billing_software.dbInterface.BrandInterface;
import com.billing_software.billing_software.models.brand.Brand;
import com.billing_software.billing_software.repositories.BrandRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class BrandImpl implements BrandInterface {

    private BrandRepository brandRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public BrandImpl(BrandRepository brandRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.brandRepository = brandRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(Brand brandData) {
        brandRepository.save(brandData).subscribe();
        return brandData.getId();
    }

    @Override
    public String update(Brand brandData) {
        Query query = Query.query(Criteria.where("_id").is(brandData.getId()));

        Update update = new Update();
        if (brandData.getName() != null)
            update.set("name", brandData.getName());
        if (brandData.getDescription() != null)
            update.set("description", brandData.getDescription());
        if (brandData.getOrgId() != null)
            update.set("orgId", brandData.getOrgId());
        if (brandData.getProfile() != null)
            update.set("profile", brandData.getProfile());
        if (brandData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", brandData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, Brand.class).subscribe();
        return brandData.getId();
    }

    @Override
    public Brand get(String brandId) {
        return brandRepository.findById(brandId).blockOptional().orElse(null);
    }

    @Override
    public List<Brand> getAllWithSearch(String searchText, int pageNumber, int pageSize) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");
        Criteria descriptionCriteria = Criteria.where("description").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                descriptionCriteria);
        query.addCriteria(orCriteria);

        query.addCriteria(Criteria.where("orgId").is(orgId));
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        query.skip(pageable.getOffset());
        query.limit(pageable.getPageSize());

        return reactiveMongoTemplate.find(query, Brand.class).collectList().blockOptional().orElse(null);
    }

    @Override
    public Long getCount(String searchText) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");
        Criteria descriptionCriteria = Criteria.where("description").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                descriptionCriteria);
        query.addCriteria(orCriteria);

        query.addCriteria(Criteria.where("orgId").is(orgId));
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));

        return reactiveMongoTemplate.count(query, Brand.class).blockOptional().orElse(0L);
    }

}
