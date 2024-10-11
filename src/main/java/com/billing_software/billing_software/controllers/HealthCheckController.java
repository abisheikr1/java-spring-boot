package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class HealthCheckController {

    @GetMapping(value = "health-check")
    public ResponseEntity<?> healthCheckFunction() {
        return new ResponseEntity<>("UP", HttpStatus.OK);
    }

}
