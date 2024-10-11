package com.billing_software.billing_software.services;

import org.springframework.stereotype.Service;
import java.util.List;

import com.billing_software.billing_software.dbInterface.SubscriptionInterface;
import com.billing_software.billing_software.dbInterface.TaxInterface;
import com.billing_software.billing_software.models.subscription.Subscription;
import com.billing_software.billing_software.payloads.requests.SubscriptionUpsertRequest;

@Service
public class SubscriptionService {

    private SubscriptionInterface subscriptionInterface;
    private TaxInterface taxInterface;

    public SubscriptionService(SubscriptionInterface subscriptionInterface, TaxInterface taxInterface) {
        this.subscriptionInterface = subscriptionInterface;
        this.taxInterface = taxInterface;
    }

    public String createOrUpdateSubscription(SubscriptionUpsertRequest subscriptionUpsertRequest) {
        Subscription subscriptionData = new Subscription(subscriptionUpsertRequest);

        if (subscriptionUpsertRequest.taxId != null) {
            if(taxInterface.get(subscriptionUpsertRequest.taxId) == null){
                throw new RuntimeException("Invalid tax Id");
            }
        }

        if (subscriptionUpsertRequest.id != null) {
            if (subscriptionInterface.get(subscriptionUpsertRequest.id) != null) {
                return subscriptionInterface.update(subscriptionData);
            } else {
                throw new RuntimeException("Invalid subscription Id");
            }
        } else {
            return subscriptionInterface.create(subscriptionData);
        }
    }

    public Subscription getSubscriptionId(String subscriptionId) {
        return subscriptionInterface.get(subscriptionId);
    }

    public List<Subscription> getAllSubscription() {
        return subscriptionInterface.getAll();
    }

}
