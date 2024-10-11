package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.billing_software.billing_software.models.subscription.Subscription;

public interface SubscriptionRepository extends ReactiveMongoRepository<Subscription, String> {

}
