package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.payloads.requests.AdditionalChargeUpsertRequest;
import com.billing_software.billing_software.services.AdditionalChargeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/charge")
public class AdditionalChargeController {

    @Autowired
    AdditionalChargeService additionalChargeService;

    @PostMapping(value = "/upsert")
    public ResponseEntity<?> createNewAdditionalCharge(
            @Valid @RequestBody AdditionalChargeUpsertRequest additionalChargeUpsertRequest) {
        return new ResponseEntity<>(
                DataResponse.builder().result(additionalChargeService
                        .createOrUpdateAdditionalCharge(additionalChargeUpsertRequest)).build(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/{chargeId}")
    public ResponseEntity<?> getAdditionalChargeData(@PathVariable String chargeId) {
        return new ResponseEntity<>(
                DataResponse.builder().result(additionalChargeService.getAdditionalChargeById(chargeId))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/get/all")
    public ResponseEntity<?> getAllAdditionalCharge(@RequestParam String searchText) {
        return new ResponseEntity<>(
                DataResponse.builder()
                        .result(additionalChargeService
                                .getAllAdditionalChargeWithSearch(searchText))
                        .build(),
                HttpStatus.OK);
    }

}
