package com.billing_software.billing_software.dbInterface;

import com.billing_software.billing_software.models.dashboard.DashboardData;

public interface DashboardInterface {

    public DashboardData getDashBoardData(String startDate, String endDate);

}
