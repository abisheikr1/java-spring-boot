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
import org.springframework.web.multipart.MultipartFile;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.models.financialDocument.DocumentStatus;
import com.billing_software.billing_software.models.financialDocument.DocumentType;
import com.billing_software.billing_software.models.financialDocument.PaymentMode;
import com.billing_software.billing_software.payloads.requests.FinancialDocUpsertRequest;
import com.billing_software.billing_software.services.FinancialDocService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/finance_doc")
public class FinancialDocController {

        // Injecting FinancialDocService using @Autowired
        @Autowired
        private FinancialDocService financialDocService;

        /**
         * API to process a financial document instantly (Instant Buy functionality)
         *
         * @param finaclialDocUpsertRequest the request object for the financial
         *                                  document
         * @return ResponseEntity with the result of the process
         */
        @PostMapping(value = "/process")
        public ResponseEntity<?> financedocInstantBuy(
                        @Valid @RequestBody FinancialDocUpsertRequest finaclialDocUpsertRequest) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(financialDocService.createOrUpdateFinanceDoc(
                                                                finaclialDocUpsertRequest, false))
                                                .build(),
                                HttpStatus.OK);
        }

        /**
         * API to create or update a financial document
         *
         * @param finaclialDocUpsertRequest the request object for upserting a financial
         *                                  document
         * @return ResponseEntity with the result of the create/update operation
         */
        @PostMapping(value = "/upsert")
        public ResponseEntity<?> createFinanceDoc(
                        @Valid @RequestBody FinancialDocUpsertRequest finaclialDocUpsertRequest) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(financialDocService.createOrUpdateFinanceDoc(
                                                                finaclialDocUpsertRequest, true))
                                                .build(),
                                HttpStatus.OK);
        }

        /**
         * API to mark a financial document as paid or canceled
         *
         * @param docId  the ID of the financial document
         * @param status the status to mark the document as (paid or canceled)
         * @return ResponseEntity with the result of the operation
         */
        @PostMapping(value = "/{docId}/status")
        public ResponseEntity<?> updateFinanceDocStatus(
                        @PathVariable String docId,
                        @RequestParam DocumentStatus status, @RequestParam(required = false) PaymentMode paymentMode) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(financialDocService.updateDocStatus(docId, status, paymentMode))
                                                .build(),
                                HttpStatus.OK);
        }

        /**
         * API to fetch a financial document by its ID
         *
         * @param docId the ID of the financial document
         * @return ResponseEntity with the financial document details
         */
        @GetMapping(value = "/{docId}")
        public ResponseEntity<?> getFinancedocById(@PathVariable String docId) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(financialDocService.getDocById(docId))
                                                .build(),
                                HttpStatus.OK);
        }

        /**
         * API to fetch all financial documents with search, pagination, and filtering
         *
         * @param searchText the text to search for in the documents
         * @param outletId   the ID of the outlet to filter by
         * @param pageNumber the page number for pagination
         * @param pageSize   the number of items per page
         * @param docType    the type of financial document (invoice, bill, etc.)
         * @return ResponseEntity with a list of financial documents matching the
         *         criteria
         */
        @GetMapping(value = "/get/all/{pageNumber}/{pageSize}")
        public ResponseEntity<?> getAllFinanceDoc(@RequestParam String searchText,
                        @RequestParam String outletId,
                        @PathVariable int pageNumber,
                        @PathVariable int pageSize,
                        @RequestParam DocumentType docType, @RequestParam DocumentStatus status) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(financialDocService.getAllFinaceDocWithSearch(searchText,
                                                                pageNumber, pageSize, outletId, docType, status))
                                                .build(),
                                HttpStatus.OK);
        }

        /**
         * API to send a financial document to a specified email address.
         *
         * @param docId The ID of the financial document.
         * @param email The email address to send the document to.
         * @return ResponseEntity indicating the success or failure of the email sending
         *         process.
         */
        @PostMapping(value = "/{docId}/send-email")
        public ResponseEntity<?> sendFinanceDocEmail(
                        @PathVariable String docId,
                        @RequestParam String email, @RequestParam("file") MultipartFile file) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(financialDocService.sendFinanceDocInMail(docId, email, file))
                                                .build(),
                                HttpStatus.OK);
        }
}