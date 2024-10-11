package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.user.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    @Query("{username:?0}")
    Mono<User> findByUserName(String userName);

    @Query("{email:?0}")
    Mono<User> findByEmail(String email);

}
