package com.billing_software.billing_software.services;

import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.billing_software.billing_software.dbInterface.CustomerInterface;
import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.models.customer.Customer;
import com.billing_software.billing_software.payloads.requests.CustomerUpsertRequest;
import com.billing_software.billing_software.utils.cloudinary.CloudinaryService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CustomerService {

    private CustomerInterface customerInterface;
    private OrganizationInterface organizationInterface;
    private CloudinaryService cloudinaryService;

    public CustomerService(CustomerInterface customerInterface, OrganizationInterface organizationInterface,
            CloudinaryService cloudinaryService) {
        this.customerInterface = customerInterface;
        this.organizationInterface = organizationInterface;
        this.cloudinaryService = cloudinaryService;
    }

    public String createOrUpdateCustomer(CustomerUpsertRequest customerUpsertRequest) {

        if (customerUpsertRequest.customerCode != null) {
            Customer customerDetail = customerInterface.getByCustomerCode(customerUpsertRequest.customerCode);
            if (customerUpsertRequest.id != null) {
                if (!customerDetail.getId().equals(customerUpsertRequest.id)) {
                    throw new RuntimeException("Customer Code alredy used");
                }
            } else {
                if (customerDetail != null) {
                    throw new RuntimeException("Customer Code alredy used");
                }
            }
        }

        Customer customerData = new Customer(customerUpsertRequest);

        if (customerUpsertRequest.orgId != null && organizationInterface.get(customerUpsertRequest.orgId) == null) {
            throw new RuntimeException("Invalid org Id");
        }

        if (customerUpsertRequest.outletId == null) {
            throw new RuntimeException("Invalid outlet Id");
        }

        if (customerUpsertRequest.id != null) {
            if (customerInterface.get(customerUpsertRequest.id) != null) {
                return customerInterface.update(customerData);
            } else {
                throw new RuntimeException("Invalid customer Id");
            }
        } else {
            return customerInterface.create(customerData);
        }
    }

    public Customer getIteamById(String customerId) {
        return customerInterface.get(customerId);
    }

    public Object getAllCustomerWithSearch(String searchtext, int pageNumber, int pageSize, String outletId) {
        Map<String, Object> response = new HashMap();
        response.put("customers", customerInterface.getAllWithSearch(searchtext, pageNumber, pageSize, outletId));
        response.put("totalCount", customerInterface.getCount(searchtext, outletId));
        response.put("searchText", searchtext);

        return response;
    }

    public Map<String, Object> uploadProfile(MultipartFile file, String customerId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Customer customerDetails = customerInterface.get(customerId);
        if (customerDetails == null) {
            throw new RuntimeException("Invalid customer Id");
        }
        String imageUrl = cloudinaryService.uploadImage(file,
                "organization/" + orgId + "/customer/" + customerId + "/profile",
                "profile");
        customerDetails.setProfile(imageUrl, "profile");
        customerInterface.update(customerDetails);
        return customerDetails.getProfile();
    }

    public String deleteProfile(String customerId, String publicUrl) {
        Customer customerDetails = customerInterface.get(customerId);
        if (customerDetails == null) {
            throw new RuntimeException("Invalid customer Id");
        }

        customerDetails.getProfile().remove("profile");

        cloudinaryService.deleteImage(publicUrl);
        customerInterface.update(customerDetails);
        return "deleted Successfully";
    }

}
