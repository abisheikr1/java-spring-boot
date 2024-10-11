package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.payloads.requests.CustomerUpsertRequest;
import com.billing_software.billing_software.services.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/customer")
public class CustomerController {

        @Autowired
        CustomerService customerService;

        @PostMapping(value = "/upsert")
        public ResponseEntity<?> createNewCustomer(
                        @Valid @RequestBody CustomerUpsertRequest customerUpsertRequest) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(customerService.createOrUpdateCustomer(customerUpsertRequest))
                                                .build(),
                                HttpStatus.OK);
        }

        @GetMapping(value = "/{customerId}")
        public ResponseEntity<?> getCustomerData(@PathVariable String customerId) {
                return new ResponseEntity<>(
                                DataResponse.builder().result(customerService.getIteamById(customerId))
                                                .build(),
                                HttpStatus.OK);
        }

        @GetMapping(value = "/get/all/{pageNumber}/{pageSize}")
        public ResponseEntity<?> getAllCustomer(@RequestParam String searchText, @RequestParam String outletId,
                        @PathVariable int pageNumber,
                        @PathVariable int pageSize) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(customerService.getAllCustomerWithSearch(searchText, pageNumber,
                                                                pageSize, outletId))
                                                .build(),
                                HttpStatus.OK);
        }

        @PutMapping(value = "{customerId}/upload/profile")
        public ResponseEntity<?> uploadProfileFroCustomer(@PathVariable String customerId,
                        @RequestParam("file") MultipartFile file) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(customerService.uploadProfile(file, customerId)).build(),
                                HttpStatus.OK);
        }

        @DeleteMapping(value = "{customerId}/delete/profile")
        public ResponseEntity<?> deleteProfileForCustomer(@PathVariable String customerId,
                        @RequestParam String publicUrl) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(customerService.deleteProfile(customerId, publicUrl)).build(),
                                HttpStatus.OK);
        }

}
