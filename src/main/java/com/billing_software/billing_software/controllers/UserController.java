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
import com.billing_software.billing_software.payloads.requests.UserUpsertRequest;
import com.billing_software.billing_software.services.UserService;

import jakarta.validation.Valid;

/**
 * This controller handles user-related requests.
 * 
 * It uses RESTful principles to define API endpoints that allow the creation,
 * fetching, updating, and deletion of user profiles. The responses from these
 * APIs are standardized into a `DataResponse` object, which encapsulates the
 * result.
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

        // The UserService is injected using Spring's Dependency Injection.
        @Autowired
        private UserService userService;

        /**
         * Create or update a user.
         * 
         * @param userUpsertRequest contains the user data to be created or updated.
         * @return a ResponseEntity containing the result of the operation wrapped
         *         in a DataResponse.
         */
        @PostMapping(value = "/upsert")
        public ResponseEntity<?> createNewUser(
                        @Valid @RequestBody UserUpsertRequest userUpsertRequest) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(userService
                                                                .createOrUpdateUser(userUpsertRequest))
                                                .build(),
                                HttpStatus.OK);
        }

        /**
         * Get a user's data by their unique identifier.
         * 
         * @param userId the unique ID of the user.
         * @return a ResponseEntity containing the user's data wrapped in a
         *         DataResponse.
         */
        @GetMapping(value = "/{userId}")
        public ResponseEntity<?> getUserData(@PathVariable String userId) {
                return new ResponseEntity<>(
                                DataResponse.builder().result(userService.getUserById(userId))
                                                .build(),
                                HttpStatus.OK);
        }

        /**
         * Retrieve all users that match a search text.
         * 
         * @param searchText the search query to find matching users.
         * @return a ResponseEntity containing the list of users matching the search
         *         criteria, wrapped in a DataResponse.
         */
        @GetMapping(value = "/get/all/{searchText}")
        public ResponseEntity<?> getAllUser(@PathVariable String searchText) {
                return new ResponseEntity<>(
                                DataResponse.builder()
                                                .result(userService.getAllUserWithSearch(searchText))
                                                .build(),
                                HttpStatus.OK);
        }

        /**
         * Upload a profile picture for the user.
         * 
         * @param userId the unique ID of the user.
         * @param file   the image file to be uploaded as the user's profile picture.
         * @return a ResponseEntity indicating the success of the operation wrapped in a
         *         DataResponse.
         */
        @PutMapping(value = "{userId}/upload/profile")
        public ResponseEntity<?> uploadProfileFroUser(@PathVariable String userId,
                        @RequestParam("file") MultipartFile file) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(userService.uploadProfile(file, userId)).build(),
                                HttpStatus.OK);
        }

        /**
         * Delete a user's profile picture.
         * 
         * @param userId    the unique ID of the user.
         * @param publicUrl the URL of the profile picture to be deleted.
         * @return a ResponseEntity indicating the success of the operation wrapped in a
         *         DataResponse.
         */
        @DeleteMapping(value = "{userId}/delete/profile")
        public ResponseEntity<?> deleteProfileForUser(@PathVariable String userId,
                        @RequestParam String publicUrl) {
                return new ResponseEntity<>(DataResponse.builder()
                                .result(userService.deleteProfile(userId, publicUrl)).build(),
                                HttpStatus.OK);
        }

        /**
         * Sends a reset password link to the user's email.
         */
        @PostMapping(value = "/send/reset-password/link")
        public ResponseEntity<?> sendResetPasswordLink(@RequestParam String email) {
                userService.sendResetPasswordLink(email);
                return new ResponseEntity<>(DataResponse.builder()
                                .result("Reset password link sent successfully")
                                .build(),
                                HttpStatus.OK);
        }

        /**
         * Updates the user's password based on the reset token.
         * 
         * @param token:       The reset token.
         * @param newPassword: The new password to be set.
         * @return Response entity with result message.
         */
        @PostMapping(value = "/reset-password")
        public ResponseEntity<?> updatePassword(@RequestParam("token") String token,
                        @RequestParam("newPassword") String newPassword, @RequestParam String userId) {
                userService.updatePassword(userId, token, newPassword); // Call service to verify token and update
                                                                        // password
                return new ResponseEntity<>(DataResponse.builder()
                                .result("Password updated successfully")
                                .build(), HttpStatus.OK);
        }

}
