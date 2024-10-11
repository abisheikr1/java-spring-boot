package com.billing_software.billing_software.dbImplementation;

import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.SubscriptionInterface;
import com.billing_software.billing_software.models.subscription.Subscription;
import com.billing_software.billing_software.models.tax.Tax;
import com.billing_software.billing_software.repositories.SubscriptionRepository;
import com.billing_software.billing_software.repositories.TaxRepository;

@Service
public class SubscriptionImpl implements SubscriptionInterface {

    private SubscriptionRepository subscriptionRepository;
    private TaxRepository taxRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public SubscriptionImpl(SubscriptionRepository subscriptionRepository,
            ReactiveMongoTemplate reactiveMongoTemplate, TaxRepository taxRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.taxRepository = taxRepository;
    }

    @Override
    public String create(Subscription subscriptionData) {
        subscriptionRepository.save(subscriptionData).subscribe();
        return subscriptionData.getId();
    }

    @Override
    public String update(Subscription subscriptionData) {
        Query query = Query.query(Criteria.where("_id").is(subscriptionData.getId()));

        Update update = new Update();
        if (subscriptionData.getName() != null)
            update.set("name", subscriptionData.getName());
        if (subscriptionData.getPlanDescription() != null)
            update.set("planDescription", subscriptionData.getPlanDescription());
        if (subscriptionData.getPrice() != null)
            update.set("price", subscriptionData.getPrice());
        if (subscriptionData.getTaxId() != null)
            update.set("taxId", subscriptionData.getTaxId());
        if (subscriptionData.getConfiguration() != null)
            update.set("configuration", subscriptionData.getConfiguration());
        if (subscriptionData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", subscriptionData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, Subscription.class).subscribe();
        return subscriptionData.getId();
    }

    @Override
    public Subscription get(String subscriptionId) {
        Subscription subscriptionData = subscriptionRepository.findById(subscriptionId).blockOptional().orElse(null);
        if (subscriptionData.getTaxId() != null) {
            Tax taxData = taxRepository.findById(subscriptionData.getTaxId()).blockOptional().orElse(null);
            subscriptionData.setTaxDetail(taxData);
        }
        return subscriptionData;
    }

    @Override
    public List<Subscription> getAll() {
        return subscriptionRepository.findAll().collectList().blockOptional().orElse(null);
    }

}
