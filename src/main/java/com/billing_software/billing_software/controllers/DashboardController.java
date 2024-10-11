package com.billing_software.billing_software.controllers;

import com.billing_software.billing_software.models.common.DataResponse;
import com.billing_software.billing_software.services.DashboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Fetches dashboard data (monthly sales trends, top products, top customers,
     * etc.)
     * for a specific date range (fromDate to toDate).
     *
     * @param fromDate Start date (ISO format: yyyy-MM-dd)
     * @param toDate   End date (ISO format: yyyy-MM-dd)
     * @return DashboardData object containing sales insights
     */
    @GetMapping("/trend")
    public ResponseEntity<?> getDashboardDataByDateRange(
            @RequestParam String fromDate,
            @RequestParam String toDate) throws ParseException {

        return new ResponseEntity<>(
                DataResponse.builder()
                        .result(dashboardService.getDashboardData(fromDate, toDate))
                        .build(),
                HttpStatus.OK);
    }
}
