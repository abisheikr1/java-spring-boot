package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.authentication.ServerToken;

public interface ServerTokenRepository extends ReactiveMongoRepository<ServerToken, String> {

}
