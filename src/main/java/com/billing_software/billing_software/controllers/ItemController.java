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
import com.billing_software.billing_software.payloads.requests.ItemUpsertRequest;
import com.billing_software.billing_software.services.ItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/item")
public class ItemController {

        @Autowired
        ItemService itemService;

        @PostMapping(value = "/upsert")
        public ResponseEntity<?> createNewItem(
                        @Valid @RequestBody ItemUpsertRequest itemUpsertrequest) {
                return new ResponseEntity<>(
                                DataResponse.builder().result(itemService.createOrUpdateItem(itemUpsertrequest))
                                                .build(),
                                HttpStatus.OK);
        }

        @GetMapping(value = "/{itemId}")
        public ResponseEntity<?> getItemData(@PathVariable String itemId) {
                return new ResponseEntity<>(
                                DataResponse.builder().result(itemService.getIteamById(itemId))
                                                .build(),
                                HttpStatus.OK);
        }

        @GetMapping(value = "/get/all/{pageNumber}/{pageSize}")
        public ResponseEntity<?> getAllItem(@RequestParam String searchText, @RequestParam String itemType,
                        @RequestParam String outletId,
                        @PathVariable int pageNumber,
                        @PathVariable int pageSize) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(itemService.getAllItemWithSearch(searchText, pageNumber,
                                                                pageSize, itemType, outletId))
                                                .build(),
                                HttpStatus.OK);
        }

        @PutMapping(value = "{itemId}/upload/profile")
        public ResponseEntity<?> uploadProfileFroItem(@PathVariable String itemId,
                        @RequestParam("file") MultipartFile file) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(itemService.uploadProfile(file, itemId)).build(),
                                HttpStatus.OK);
        }

        @DeleteMapping(value = "{itemId}/delete/profile")
        public ResponseEntity<?> deleteProfileForItem(@PathVariable String itemId,
                        @RequestParam String publicUrl) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(itemService.deleteProfile(itemId, publicUrl)).build(),
                                HttpStatus.OK);
        }

}
