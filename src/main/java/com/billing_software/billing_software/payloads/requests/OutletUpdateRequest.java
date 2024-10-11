package com.billing_software.billing_software.payloads.requests;

import java.util.Map;

import com.billing_software.billing_software.models.financialDocument.DocumentType;
import com.billing_software.billing_software.models.organization.sequence.SequenceConfig;
import com.billing_software.billing_software.models.organization.workinghours.DailyHours;

public class OutletUpdateRequest {

    public String outletId;
    public String outletName;
    public String outletCode;
    public String phoneNumber;
    public String email;
    public String address;
    public String managerName;
    public Map<String, DailyHours> openingHours;
    public Map<String, SequenceConfig> sequenceConfigMap;
    public Map<String, String> upiconfig;

}
