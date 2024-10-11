package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.payloads.requests.SubscriptionUpsertRequest;
import com.billing_software.billing_software.services.SubscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping(value = "/upsert")
    public ResponseEntity<?> createNewSubscription(
            @Valid @RequestBody SubscriptionUpsertRequest subscriptionUpsertRequest) {
        return new ResponseEntity<>(
                DataResponse.builder()
                        .result(subscriptionService
                                .createOrUpdateSubscription(subscriptionUpsertRequest))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/{subscriptionId}")
    public ResponseEntity<?> getSubscriptionData(@PathVariable String subscriptionId) {
        return new ResponseEntity<>(
                DataResponse.builder().result(subscriptionService.getSubscriptionId(subscriptionId))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/get/all")
    public ResponseEntity<?> getAllSubscription() {
        return new ResponseEntity<>(
                DataResponse.builder().result(subscriptionService.getAllSubscription())
                        .build(),
                HttpStatus.OK);
    }

}
