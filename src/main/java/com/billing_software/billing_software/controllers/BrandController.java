package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.payloads.requests.BrandUpsertRequest;
import com.billing_software.billing_software.services.BrandService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/brand")
public class BrandController {

    @Autowired
    BrandService brandService;

    // Endpoint to create or update a Brand
    @PostMapping(value = "/upsert")
    public ResponseEntity<?> createOrUpdateBrand(
            @Valid @RequestBody BrandUpsertRequest brandUpsertRequest) {
        return new ResponseEntity<>(
                DataResponse.builder()
                        .result(brandService.createOrUpdateBrand(brandUpsertRequest))
                        .build(),
                HttpStatus.OK);
    }

    // Endpoint to get Brand by ID
    @GetMapping(value = "/{brandId}")
    public ResponseEntity<?> getBrandById(@PathVariable String brandId) {
        return new ResponseEntity<>(
                DataResponse.builder().result(brandService.getBrandById(brandId))
                        .build(),
                HttpStatus.OK);
    }

    // Endpoint to get all Brands with optional search
    @GetMapping(value = "/get/all/{pageNumber}/{pageSize}")
    public ResponseEntity<?> getAllBrands(
            @RequestParam String searchText,
            @PathVariable int pageNumber,
            @PathVariable int pageSize) {
        return new ResponseEntity<>(
                DataResponse.builder()
                        .result(brandService.getAllBrandsWithSearch(searchText, pageNumber, pageSize))
                        .build(),
                HttpStatus.OK);
    }

    // Endpoint to upload profile image for a Brand
    @PutMapping(value = "{brandId}/upload/logo")
    public ResponseEntity<?> uploadBrandLogo(@PathVariable String brandId,
            @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(DataResponse.builder()
                .result(brandService.uploadProfile(file, brandId)) // Placeholder implementation
                .build(),
                HttpStatus.OK);
    }

    // Endpoint to delete profile image for a Brand
    @DeleteMapping(value = "{brandId}/delete/logo")
    public ResponseEntity<?> deleteBrandLogo(@PathVariable String brandId,
            @RequestParam String publicUrl) {
        return new ResponseEntity<>(DataResponse.builder()
                .result(brandService.deleteProfile(brandId, publicUrl)) // Placeholder implementation
                .build(),
                HttpStatus.OK);
    }

}
