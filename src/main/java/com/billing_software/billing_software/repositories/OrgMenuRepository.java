package com.billing_software.billing_software.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.menu.OrgMenu;

public interface OrgMenuRepository extends ReactiveMongoRepository<OrgMenu, String> {

}
