package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.additionalCharge.AdditionalCharge;

public interface AdditionalChargeRepository extends ReactiveMongoRepository<AdditionalCharge, String> {

}
