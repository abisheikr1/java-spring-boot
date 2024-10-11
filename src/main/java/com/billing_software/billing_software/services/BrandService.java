package com.billing_software.billing_software.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.billing_software.billing_software.dbInterface.BrandInterface;
import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.models.brand.Brand;
import com.billing_software.billing_software.payloads.requests.BrandUpsertRequest;
import com.billing_software.billing_software.utils.cloudinary.CloudinaryService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.HashMap;

@Service
public class BrandService {

    private final BrandInterface brandInterface;
    private final OrganizationInterface organizationInterface;
    private final CloudinaryService cloudinaryService;

    public BrandService(BrandInterface brandInterface, OrganizationInterface organizationInterface,
            CloudinaryService cloudinaryService) {
        this.brandInterface = brandInterface;
        this.organizationInterface = organizationInterface;
        this.cloudinaryService = cloudinaryService;
    }

    // Method to create or update a Brand
    public String createOrUpdateBrand(BrandUpsertRequest brandUpsertRequest) {
        Brand brandData = new Brand(brandUpsertRequest);

        if (brandUpsertRequest.orgId != null
                && organizationInterface.get(brandUpsertRequest.orgId) == null) {
            throw new RuntimeException("Invalid organization ID");
        }

        if (brandUpsertRequest.id != null) {
            if (brandInterface.get(brandUpsertRequest.id) != null) {
                return brandInterface.update(brandData);
            } else {
                throw new RuntimeException("Invalid brand ID");
            }
        } else {
            return brandInterface.create(brandData);
        }
    }

    // Method to get a Brand by its ID
    public Brand getBrandById(String brandId) {
        Brand brand = brandInterface.get(brandId);
        if (brand == null) {
            throw new RuntimeException("Brand not found with ID: " + brandId);
        }
        return brand;
    }

    // Method to get all Brands with optional search functionality
    public Object getAllBrandsWithSearch(String searchtext, int pageNumber, int pageSize) {
        Map<String, Object> response = new HashMap();
        response.put("brands", brandInterface.getAllWithSearch(searchtext, pageNumber, pageSize));
        response.put("totalCount", brandInterface.getCount(searchtext));
        response.put("searchText", searchtext);

        return response;
    }

    public Map<String, Object> uploadProfile(MultipartFile file, String brandId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Brand brandDetails = brandInterface.get(brandId);
        if (brandDetails == null) {
            throw new RuntimeException("Invalid brand Id");
        }
        String imageUrl = cloudinaryService.uploadImage(file,
                "organization/" + orgId + "/brand/" + brandId + "/profile",
                "profile");
        brandDetails.setProfile(imageUrl, "profile");
        brandInterface.update(brandDetails);
        return brandDetails.getProfile();
    }

    public String deleteProfile(String brandId, String publicUrl) {
        Brand brandDetails = brandInterface.get(brandId);
        if (brandDetails == null) {
            throw new RuntimeException("Invalid brand Id");
        }

        brandDetails.getProfile().remove("profile");

        cloudinaryService.deleteImage(publicUrl);
        brandInterface.update(brandDetails);
        return "deleted Successfully";
    }

}
