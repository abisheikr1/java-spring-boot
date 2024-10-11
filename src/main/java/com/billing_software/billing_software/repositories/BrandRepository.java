package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.brand.Brand;

import reactor.core.publisher.Flux;

public interface BrandRepository extends ReactiveMongoRepository<Brand, String> {

    @Query("{orgId:?0}")
    Flux<Brand> finAllByOrgId(String orgId);

}
