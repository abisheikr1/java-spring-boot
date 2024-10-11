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
import com.billing_software.billing_software.payloads.requests.TaxUpsertRequest;
import com.billing_software.billing_software.services.TaxService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/tax")
public class TaxController {

    @Autowired
    TaxService taxService;

    @PostMapping(value = "/upsert")
    public ResponseEntity<?> createNewTax(
            @Valid @RequestBody TaxUpsertRequest taxUpsertRequest) {
        return new ResponseEntity<>(
                DataResponse.builder().result(taxService.createOrUpdateTax(taxUpsertRequest)).build(), HttpStatus.OK);
    }

    @GetMapping(value = "/{taxId}")
    public ResponseEntity<?> getTaxData(@PathVariable String taxId) {
        return new ResponseEntity<>(
                DataResponse.builder().result(taxService.getTaxById(taxId))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/get/all")
    public ResponseEntity<?> getAllTax(@RequestParam String searchText) {
        return new ResponseEntity<>(
                DataResponse.builder().result(taxService.getAllTaxWithSearch(searchText))
                        .build(),
                HttpStatus.OK);
    }

}
