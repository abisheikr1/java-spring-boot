package com.billing_software.billing_software.models.organization.workinghours;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyHours {

    private String openTime; // Opening time (e.g., "9:00 AM")
    private String closeTime; // Closing time (e.g., "5:00 PM")

}
