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
import com.billing_software.billing_software.payloads.requests.OrgMenuUpsertRequest;
import com.billing_software.billing_software.services.OrgMenuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/orgMenu")
public class OrgMenuController {

    @Autowired
    OrgMenuService orgMenuService;

    @PostMapping(value = "/upsert")
    public ResponseEntity<?> createNewOrgMenu(
            @Valid @RequestBody OrgMenuUpsertRequest orgMenuUpsertRequest) {
        return new ResponseEntity<>(
                DataResponse.builder()
                        .result(orgMenuService
                                .createOrUpdateOrgMenu(orgMenuUpsertRequest))
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping(value = "/{menuId}/upsert/subMenu")
    public ResponseEntity<?> updateSubMenuToOrgMenu(@PathVariable String menuId,
            @Valid @RequestBody OrgMenuUpsertRequest orgMenuUpsertRequest) {
        return new ResponseEntity<>(
                DataResponse.builder()
                        .result(orgMenuService.createOrUpdateOrgSubMenu(orgMenuUpsertRequest,
                                menuId))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/{orgMenuId}")
    public ResponseEntity<?> getOrgMenuData(@PathVariable String orgMenuId) {
        return new ResponseEntity<>(
                DataResponse.builder().result(orgMenuService.getOrgMenuById(orgMenuId))
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/get/all")
    public ResponseEntity<?> getAllOrgMenu() {
        return new ResponseEntity<>(
                DataResponse.builder().result(orgMenuService.getAllOrgMenu())
                        .build(),
                HttpStatus.OK);
    }
}
