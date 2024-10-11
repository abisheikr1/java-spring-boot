package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.organization.Organization;

import reactor.core.publisher.Mono;

public interface OrganizationRepository extends ReactiveMongoRepository<Organization, String> {

    @Query("{$or: [{ 'id': ?0 }, { 'shortId': ?0 }]}")
    Mono<Organization> findByOrgIdOrShortId(String id);

    @Query(value = "{$or: [{ 'id': ?0 }, { 'shortId': ?0 }]}", fields = "{ 'id': 1, 'shortId': 1, 'name': 1, 'website': 1, 'industryType': 1, 'theme': 1, 'logo': 1 }")
    Mono<Organization> findByOrgIdOrShortIdMinimal(String id);

}
