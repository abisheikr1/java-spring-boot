package com.billing_software.billing_software.models.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.Map;

import com.billing_software.billing_software.models.subscription.ConfigItem;
import com.billing_software.billing_software.models.subscription.Subscription;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgSubscription {

    private Date startDate;
    private Date endDate;
    private String subscriptionId;

    private Map<String, ConfigItem> configuration;

}
