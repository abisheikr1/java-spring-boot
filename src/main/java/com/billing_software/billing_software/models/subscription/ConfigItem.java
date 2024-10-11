package com.billing_software.billing_software.models.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// public class PlanConfiguration {

//     private ConfigItem durationDays; // Number of days the subscription is valid
//     private ConfigItem maxLogins; // Maximum number of login accounts allowed
//     private ConfigItem maxOutlets; // Maximum number of outlets/locations allowed
//     private ConfigItem maxCustomers; // Maximum number of customers that can be managed
//     private ConfigItem reportRangeDays; // Range in days for report downloads
//     private ConfigItem maxProducts; // Maximum number of products or services that can be managed

//     private ConfigItem mobileApp; // Whether a mobile app is provided with the plan
//     private ConfigItem privateCloud; // Whether private cloud hosting is included
//     private ConfigItem privateLabel; // Whether private branding (labeling) is offered
//     private ConfigItem mailDoc; // Whether documents can be sent via email
//     private ConfigItem whatsappDoc; // Whether documents can be sent via WhatsApp
//     private ConfigItem privateMode; // Whether private mode is allowed
//     private ConfigItem emailCampaign; // Whether customer email campaigns are allowed
//     private ConfigItem whatsappCampaign; // Whether customer WhatsApp campaigns are allowed
//     private ConfigItem loyaltyPoints; // Whether loyalty points for customers are enabled
//     private ConfigItem pdfDownload; // Whether documents can be downloaded as PDF
//     private ConfigItem excelExport; // Whether customer lists can be exported as Excel

//     private ConfigItem syncData; // Sync Data accross all device
//     private ConfigItem itemBulkUpload; // Bulk upload option for item/service
//     private ConfigItem CustomerBulkUpload; // Bulk upload option for customer/vendor
//     private ConfigItem driveStorage; // Maximum amount of storage allowed
//     private ConfigItem bacthAndExpiry; // Support for batch and expiry
//     private ConfigItem printBarcode; // print barcode for product

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfigItem {

    private String title;
    private String description;
    private Integer value;
    private Boolean isEnabled;

}

// }
