package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.tax.Tax;

public interface TaxRepository extends ReactiveMongoRepository<Tax, String> {

}
