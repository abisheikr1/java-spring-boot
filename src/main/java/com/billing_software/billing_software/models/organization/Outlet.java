package com.billing_software.billing_software.models.organization;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Transient;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.models.financialDocument.DocumentType;
import com.billing_software.billing_software.models.organization.sequence.SequenceConfig;
import com.billing_software.billing_software.models.organization.workinghours.DailyHours;
import com.billing_software.billing_software.payloads.requests.OutletUpdateRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Outlet extends CoreContext {

    @Transient
    public static String shortCode = "OLT";

    private String outletId; // Unique identifier for the shop
    private String outletName; // Name of the shop or outlet
    private String outletCode; // Unique code for the shop (e.g., for identification)
    private String phoneNumber; // Contact phone number for the shop
    private String email; // Contact email address for the shop
    private String address; // Address of the shop
    private String managerName; // Name of the shop manager or point of contact
    private Map<String, DailyHours> openingHours; // Opening hours for the shop

    private Map<String, SequenceConfig> sequenceConfigMap; // Key: Sequence Type, Value: SequenceConfig
    private Map<String, String> SMTPConfig; // Key: Sequence Type, Value: SequenceConfig
    private Map<String, Object> profile; // Org Logo
    private Map<String, String> upiConfig; // for collecting upi pay payment

    public Outlet(OutletUpdateRequest outletUpdateRequest) {
        if (outletUpdateRequest.outletId != null) {
            this.outletId = outletUpdateRequest.outletId;
            this.setLastUpdatedDate(new Date());
        } else {
            this.outletId = GenerateIdService.generateId(Outlet.shortCode);
            this.setCreatedDate(new Date());
        }

        this.outletName = outletUpdateRequest.outletName;
        this.outletCode = outletUpdateRequest.outletCode;
        this.phoneNumber = outletUpdateRequest.phoneNumber;
        this.email = outletUpdateRequest.email;
        this.address = outletUpdateRequest.address;
        this.managerName = outletUpdateRequest.managerName;
        this.openingHours = outletUpdateRequest.openingHours;
        this.sequenceConfigMap = outletUpdateRequest.sequenceConfigMap;
        this.upiConfig = outletUpdateRequest.upiconfig;
    }

    public void setOutletProfile(String imageUrl, String imageType) {
        if (this.profile == null) {
            this.profile = new HashMap<>();
        }
        this.profile.put(imageType, imageUrl);
    }
}
