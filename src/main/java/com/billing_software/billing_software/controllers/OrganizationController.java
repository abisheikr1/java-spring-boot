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
import com.billing_software.billing_software.payloads.requests.OrganizationUpsertRequest;
import com.billing_software.billing_software.payloads.requests.OutletUpdateRequest;
import com.billing_software.billing_software.services.OrganizationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/org")
public class OrganizationController {

        @Autowired
        private OrganizationService organizationService;

        @PostMapping(value = "/upsert")
        public ResponseEntity<?> createNewOrganization(
                        @Valid @RequestBody OrganizationUpsertRequest organizationUpsertRequest) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(organizationService
                                                                .createOrUpdateOrganization(organizationUpsertRequest))
                                                .build(),
                                HttpStatus.OK);
        }

        @PostMapping(value = "/{orgId}/upsert/outlet")
        public ResponseEntity<?> updateOutletToOrg(@PathVariable String orgId,
                        @Valid @RequestBody OutletUpdateRequest outletUpdateRequest) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(organizationService.createOrUpdateOrgOutlet(outletUpdateRequest,
                                                                orgId))
                                                .build(),
                                HttpStatus.OK);
        }

        @GetMapping(value = "/{orgId}")
        public ResponseEntity<?> getOrgData(@PathVariable String orgId) {
                return new ResponseEntity<>(
                                DataResponse.builder().result(organizationService.getOrganizationById(orgId))
                                                .build(),
                                HttpStatus.OK);
        }

        @GetMapping(value = "/{orgId}/get/{outletId}")
        public ResponseEntity<?> getOrgOutlet(@PathVariable String orgId, @PathVariable String outletId) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(organizationService.getOrganizationOutlet(orgId, outletId))
                                                .build(),
                                HttpStatus.OK);
        }

        @GetMapping(value = "/{orgId}/minimal")
        public ResponseEntity<?> getOrgDataMinimal(@PathVariable String orgId) {
                return new ResponseEntity<>(
                                DataResponse.builder().result(organizationService.getOrganizationMinimalById(orgId))
                                                .build(),
                                HttpStatus.OK);
        }

        @GetMapping(value = "/get/all/{searchText}")
        public ResponseEntity<?> getAllOrg(@PathVariable String searchText) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(organizationService.getAllOrganizationWithSearch(searchText))
                                                .build(),
                                HttpStatus.OK);
        }

        @PutMapping(value = "{orgId}/update/subscription/{subscriptionId}")
        public ResponseEntity<?> updateSubscriptionForOrg(@PathVariable String orgId,
                        @PathVariable String subscriptionId) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(organizationService.updateSubscriptionToOrg(orgId,
                                                                subscriptionId))
                                                .build(),
                                HttpStatus.OK);
        }

        @PutMapping(value = "{orgId}/upload/logo")
        public ResponseEntity<?> uploadLogoForOrg(@PathVariable String orgId, @RequestParam("file") MultipartFile file,
                        @RequestParam String logoType) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(organizationService.uploadOrgLogo(file, orgId, logoType)).build(),
                                HttpStatus.OK);
        }

        @DeleteMapping(value = "{orgId}/delete/logo")
        public ResponseEntity<?> deleteLogoForOrg(@PathVariable String orgId,
                        @RequestParam String logoType, @RequestParam String publicUrl) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(organizationService.deleteLogo(orgId, publicUrl, logoType)).build(),
                                HttpStatus.OK);
        }

        @PutMapping(value = "{orgId}/upload/{outletId}/profile")
        public ResponseEntity<?> uploadProfileForOutlet(@PathVariable String orgId, @PathVariable String outletId,
                        @RequestParam("file") MultipartFile file) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(organizationService.uploadOutletProfile(file, orgId, outletId)).build(),
                                HttpStatus.OK);
        }

        @DeleteMapping(value = "{orgId}/delete/{outletId}/profile")
        public ResponseEntity<?> deleteProfilrForOutlet(@PathVariable String orgId, @PathVariable String outletId,
                        @RequestParam String publicUrl) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(organizationService.deleteOutletProfile(orgId, publicUrl, outletId)).build(),
                                HttpStatus.OK);
        }

}