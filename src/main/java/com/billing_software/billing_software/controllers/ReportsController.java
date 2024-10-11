package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.services.ReportService;

@RestController
@RequestMapping(value = "/report")
public class ReportsController {

    @Autowired
    ReportService reportService;

    /**
     * Fetches report data for the specified date range.
     *
     * @param fromDate Start date of the report (as a String).
     * @param toDate   End date of the report (as a String).
     * @return ResponseEntity with DataResponse containing the report result.
     */
    @GetMapping(value = "/get/{reportType}")
    public ResponseEntity<?> getReportForDateRange(@PathVariable String reportType,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {

        // Build the response with the report result and return an HTTP 200 (OK) status.
        return new ResponseEntity<>(
                DataResponse.builder()
                        .result(reportService.generateReport(fromDate, toDate, reportType)) // Call to the service layer
                        .build(),
                HttpStatus.OK);
    }

}
