package com.billing_software.billing_software.dbInterface;

import com.billing_software.billing_software.models.subscription.Subscription;

import java.util.List;

public interface SubscriptionInterface {

    public String create(Subscription subscriptionData);

    public String update(Subscription subscriptionData);

    public Subscription get(String subscriptionId);

    public List<Subscription> getAll();

}
