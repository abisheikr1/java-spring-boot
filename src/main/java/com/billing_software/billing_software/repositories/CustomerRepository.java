package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.customer.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    @Query("{orgId:?0}")
    Flux<Customer> finAllByOrgId(String orgId);

    @Query("{customerCode:?0}")
    Mono<Customer> findByCustomerCode(String customerCode);

    @Query("{$or: [{emailAddress: ?0}, {phoneNumber: ?1}]}")
    Flux<Customer> findByEmailOrPhoneNum(String email, String phoneNumber);

}
