package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.payloads.requests.LoginRequest;
import com.billing_software.billing_software.payloads.requests.TokenReq;
import com.billing_software.billing_software.services.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/user")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping(value = "/{orgId}/login")
    public ResponseEntity<?> userLogin(@PathVariable String orgId, @Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(
                DataResponse.builder().result(authenticationService.userAuthentication(loginRequest, orgId)).build(),
                HttpStatus.OK);
    }

    @PostMapping("/{orgId}/refresh/token")
    public ResponseEntity<?> refreshToken(@PathVariable String orgId,
            @Valid @RequestBody TokenReq tokenReq) {
        return new ResponseEntity<>(DataResponse.builder()
                .result(authenticationService.refreshToken(tokenReq.token, orgId)).build(),
                HttpStatus.OK);
    }

    @PutMapping(value = "/logout")
    public ResponseEntity<?> userLogout() {
        return new ResponseEntity<>(
                DataResponse.builder().result(authenticationService.userLogOut()).build(),
                HttpStatus.OK);
    }

}
