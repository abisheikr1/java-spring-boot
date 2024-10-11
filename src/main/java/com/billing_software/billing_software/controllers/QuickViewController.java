package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.services.QuickViewService;

@RestController
@RequestMapping("/quickviews")
public class QuickViewController {

        @Autowired
        private QuickViewService quickViewService; // Use private access modifier for better encapsulation

        /**
         * Retrieve quick view details for a specific organization.
         *
         * @param orgId                 the ID of the organization
         * @param isGetProduct          flag to indicate if product details should be
         *                              retrieved
         * @param isGetAdditionalCharge flag to indicate if additional charge details
         *                              should be retrieved
         * @param isGetTaxDetail        flag to indicate if tax detail should be
         *                              retrieved
         * @param productGroupBy        the parameter to group products
         * @return a ResponseEntity containing the quick view details
         */
        @GetMapping("/get") // Use a GET request to retrieve a resource by ID
        public ResponseEntity<DataResponse> getQuickViewDetails(
                        @RequestParam Boolean isGetProduct,
                        @RequestParam Boolean isGetAdditionalCharge,
                        @RequestParam Boolean isGetTaxDetail,
                        @RequestParam(required = false) String productGroupBy) {

                // Call the service to get quick view response with the provided parameters
                DataResponse response = DataResponse.builder()
                                .result(quickViewService.getQuickViewResponse(isGetProduct, isGetAdditionalCharge,
                                                isGetTaxDetail,
                                                productGroupBy))
                                .build();

                return new ResponseEntity<>(response, HttpStatus.OK); // Return response with HTTP status OK
        }
}
