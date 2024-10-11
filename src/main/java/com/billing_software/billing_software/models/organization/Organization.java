package com.billing_software.billing_software.models.organization;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.models.subscription.Subscription;
import com.billing_software.billing_software.payloads.requests.OrganizationUpsertRequest;
import com.billing_software.billing_software.payloads.requests.OutletUpdateRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("organizations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Organization extends CoreContext {

    @Transient
    private static String shortCode = "ORG";

    private String id; // Unique identifier for the tenant
    private String shortId; // Unique short code for redeable url
    private String name; // Name of the tenant (e.g., company name)
    private String phoneNumber; // Contact information for the tenant
    private String email; // Contact information for the tenant
    private String billingAddress; // Billing address for the tenant
    private String shippingAddress; // Shipping address for the organization (if different from billing address)
    private String website; // Website URL for the organization
    private String industryType; // Type of industry or sector the organization belongs to
    private String gstin; // Unique GST number of the organization
    private List<Outlet> outlets; // List of shops or outlets under the organization
    private OrgSubscription subscription; // Subscription details of org
    private Map<String, Object> theme; // The theme for org is maintained hear
    private Map<String, Object> logo; // Org Logo
    private OrgSettings orgSettings; // Org Logo

    public Organization(OrganizationUpsertRequest organizationUpsertRequest) {
        if (organizationUpsertRequest.id != null) {
            this.id = organizationUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(Organization.shortCode);
            this.setCreatedDate(new Date());
        }

        this.shortId = organizationUpsertRequest.shortId;
        this.name = organizationUpsertRequest.name;
        this.phoneNumber = organizationUpsertRequest.phoneNumber;
        this.email = organizationUpsertRequest.email;
        this.billingAddress = organizationUpsertRequest.billingAddress;
        this.shippingAddress = organizationUpsertRequest.shippingAddress;
        this.website = organizationUpsertRequest.website;
        this.industryType = organizationUpsertRequest.industryType;
        this.theme = organizationUpsertRequest.theme;
        this.gstin = organizationUpsertRequest.gstin;
        this.orgSettings = organizationUpsertRequest.orgSettings;
    }

    public void upsertOutlet(Outlet outlet, OutletUpdateRequest outletUpdateRequest) {
        if (this.outlets == null) {
            this.outlets = new ArrayList<>();
            this.outlets.add(outlet);
        } else {
            Outlet outletFound = this.outlets.stream()
                    .filter(o -> o.getOutletId().equals(outlet.getOutletId()))
                    .findFirst().orElse(null);
            if (outletFound != null) {
                int index = this.outlets.indexOf(outletFound);
                this.outlets.set(index, outlet);
            } else {
                if (outletUpdateRequest.outletId == null) {
                    this.outlets.add(outlet);
                } else {
                    throw new RuntimeException("Outlet id not found");
                }
            }
        }

    }

    public void upsertSubscription(Subscription subscription) {
        if (this.subscription != null) {
            if (!this.subscription.getEndDate().before(new Date())) {
                throw new RuntimeException("Alredy has an active subscription");
            }
        } else {
            this.subscription = new OrgSubscription();
        }
        this.subscription.setSubscriptionId(subscription.getId());
        this.subscription.setConfiguration(subscription.getConfiguration());
        Date today = new Date();
        this.subscription.setStartDate(today);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, subscription.getConfiguration().get("durationDays").getValue()); // add 5 days
        this.subscription.setEndDate(calendar.getTime());
    }

    public void setLogo(String imageUrl, String imageType) {
        if (this.logo == null) {
            this.logo = new HashMap<>();
        }
        this.logo.put(imageType, imageUrl);
    }
}
