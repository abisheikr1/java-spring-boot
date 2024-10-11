package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.item.Item;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

    @Query("{orgId:?0}")
    Flux<Item> finAllByOrgId(String orgId);

    @Query("{itemCode:?0}")
    Mono<Item> finByItemCode(String itemCode);

}
