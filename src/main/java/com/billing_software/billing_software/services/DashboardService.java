package com.billing_software.billing_software.services;

import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.DashboardInterface;
import com.billing_software.billing_software.models.dashboard.DashboardData;

@Service
public class DashboardService {

    private final DashboardInterface dashboardInterface;

    public DashboardService(DashboardInterface dashboardInterface) {
        this.dashboardInterface = dashboardInterface;
    }

    public DashboardData getDashboardData(String startDate, String endDate) {
        return dashboardInterface.getDashBoardData(startDate, endDate);
        // return null;
    }
}
