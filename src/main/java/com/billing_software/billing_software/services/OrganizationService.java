package com.billing_software.billing_software.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.models.organization.Outlet;
import com.billing_software.billing_software.models.subscription.Subscription;
import com.billing_software.billing_software.payloads.requests.OrganizationUpsertRequest;
import com.billing_software.billing_software.payloads.requests.OutletUpdateRequest;
import com.billing_software.billing_software.utils.cloudinary.CloudinaryService;

@Service
public class OrganizationService {

    private OrganizationInterface organizationInterface;
    private SubscriptionService subscriptionService;
    private CloudinaryService cloudinaryService;

    public OrganizationService(OrganizationInterface organizationInterface,
            SubscriptionService subscriptionService, CloudinaryService cloudinaryService) {
        this.organizationInterface = organizationInterface;
        this.subscriptionService = subscriptionService;
        this.cloudinaryService = cloudinaryService;
    }

    public String createOrUpdateOrganization(OrganizationUpsertRequest organizationUpsertRequest) {
        Organization orgData = new Organization(organizationUpsertRequest);

        if (organizationUpsertRequest.id != null) {
            if (organizationInterface.get(organizationUpsertRequest.id) != null) {
                return organizationInterface.update(orgData);
            } else {
                throw new RuntimeException("Invalid org Id");
            }
        } else {
            return organizationInterface.create(orgData);
        }
    }

    public String createOrUpdateOrgOutlet(OutletUpdateRequest outletUpdateRequest, String orgId) {
        Organization orgData = organizationInterface.get(orgId);
        if (orgData != null) {
            Outlet outlet = new Outlet(outletUpdateRequest);
            orgData.upsertOutlet(outlet, outletUpdateRequest);
            organizationInterface.update(orgData);
            return outlet.getOutletId();
        } else {
            throw new RuntimeException("Invalid org Id");
        }
    }

    public Organization getOrganizationById(String orgId) {
        Organization orgData = organizationInterface.get(orgId);
        return orgData;
    }

    public Outlet getOrganizationOutlet(String orgId, String outletId) {
        Organization orgData = organizationInterface.get(orgId);
        Outlet outletFound = orgData.getOutlets().stream()
                .filter(o -> o.getOutletId().equals(outletId))
                .findFirst().orElse(null);
        return outletFound;
    }

    public Organization getOrganizationMinimalById(String orgId) {
        Organization orgData = organizationInterface.getMinimal(orgId);
        return orgData;
    }

    public List<Organization> getAllOrganizationWithSearch(String searchtext) {
        return organizationInterface.getAllWithSearch(searchtext);
    }

    public String updateSubscriptionToOrg(String orgId, String subscriptionId) {
        Subscription subscriptionDetail = subscriptionService.getSubscriptionId(subscriptionId);
        if (subscriptionDetail == null) {
            throw new RuntimeException("Invalid subscription Id");
        }
        Organization orgDetails = organizationInterface.get(orgId);
        if (orgDetails == null) {
            throw new RuntimeException("Invalid org Id");
        }
        orgDetails.upsertSubscription(subscriptionDetail);
        return organizationInterface.update(orgDetails);
    }

    public Map<String, Object> uploadOrgLogo(MultipartFile file, String orgId, String logoType) {
        Organization orgDetails = organizationInterface.get(orgId);
        if (orgDetails == null) {
            throw new RuntimeException("Invalid org Id");
        }
        String imageUrl = cloudinaryService.uploadImage(file, "organization/" + orgId + "/logo/" + logoType + "/",
                logoType);
        orgDetails.setLogo(imageUrl, logoType);
        organizationInterface.update(orgDetails);
        return orgDetails.getLogo();
    }

    public String deleteLogo(String orgId, String publicUrl, String logoType) {
        Organization orgDetails = organizationInterface.get(orgId);
        if (orgDetails == null) {
            throw new RuntimeException("Invalid org Id");
        }

        orgDetails.getLogo().remove(logoType);

        cloudinaryService.deleteImage(publicUrl);
        organizationInterface.update(orgDetails);
        return "deleted Successfully";
    }

    public Map<String, Object> uploadOutletProfile(MultipartFile file, String orgId, String outletId) {
        Organization orgDetails = organizationInterface.get(orgId);
        if (orgDetails == null) {
            throw new RuntimeException("Invalid org Id");
        }
        String imageUrl = cloudinaryService.uploadImage(file,
                "organization/" + orgId + "/outlet/" + outletId + "/profile", "profile");

        Outlet outletFound = orgDetails.getOutlets().stream()
                .filter(o -> o.getOutletId().equals(outletId))
                .findFirst().orElse(null);

        outletFound.setOutletProfile(imageUrl, "profile");

        organizationInterface.update(orgDetails);
        return orgDetails.getLogo();
    }

    public String deleteOutletProfile(String orgId, String publicUrl, String outletId) {
        Organization orgDetails = organizationInterface.get(orgId);
        if (orgDetails == null) {
            throw new RuntimeException("Invalid org Id");
        }

        Outlet outletFound = orgDetails.getOutlets().stream()
                .filter(o -> o.getOutletId().equals(outletId))
                .findFirst().orElse(null);

        outletFound.getProfile().remove("profile");

        cloudinaryService.deleteImage(publicUrl);
        organizationInterface.update(orgDetails);
        return "deleted Successfully";
    }

}
