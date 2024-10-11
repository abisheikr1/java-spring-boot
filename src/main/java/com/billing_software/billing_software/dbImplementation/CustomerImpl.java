package com.billing_software.billing_software.dbImplementation;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.billing_software.billing_software.dbInterface.CustomerInterface;
import com.billing_software.billing_software.models.customer.Customer;
import com.billing_software.billing_software.repositories.CustomerRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class CustomerImpl implements CustomerInterface {

    private CustomerRepository customerRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public CustomerImpl(CustomerRepository customerRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.customerRepository = customerRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(Customer customerData) {
        customerRepository.save(customerData).subscribe();
        return customerData.getId();
    }

    @Override
    public String update(Customer customerData) {
        Query query = Query.query(Criteria.where("_id").is(customerData.getId()));

        Update update = new Update();
        if (customerData.getOrgId() != null)
            update.set("orgId", customerData.getOrgId());
        if (customerData.getCustomerCode() != null)
            update.set("customerCode", customerData.getCustomerCode());
        if (customerData.getName() != null)
            update.set("name", customerData.getName());
        if (customerData.getPhoneNumber() != null)
            update.set("phoneNumber", customerData.getPhoneNumber());
        if (customerData.getEmailAddress() != null)
            update.set("emailAddress", customerData.getEmailAddress());
        if (customerData.getBillingAddress() != null)
            update.set("billingAddress", customerData.getBillingAddress());
        if (customerData.getShippingAddress() != null)
            update.set("shippingAddress", customerData.getShippingAddress());
        if (customerData.getGstin() != null)
            update.set("gstin", customerData.getGstin());
        if (customerData.getCreditLimit() != null)
            update.set("creditLimit", customerData.getCreditLimit());
        if (customerData.getPaymentTerms() != null)
            update.set("paymentTerms", customerData.getPaymentTerms());
        if (customerData.getAccountBalance() != null)
            update.set("accountBalance", customerData.getAccountBalance());
        if (customerData.getCustomerType() != null)
            update.set("customerType", customerData.getCustomerType());
        if (customerData.getLoyaltyPoints() != null)
            update.set("loyaltyPoints", customerData.getLoyaltyPoints());
        if (customerData.getProfile() != null)
            update.set("profile", customerData.getProfile());
        if (customerData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", customerData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, Customer.class).subscribe();
        return customerData.getId();
    }

    @Override
    public Customer get(String customerId) {
        return customerRepository.findById(customerId).blockOptional().orElse(null);
    }

    @Override
    public List<Customer> getAllWithSearch(String searchText, int pageNumber, int pageSize, String outletId) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");
        Criteria customerCodeCriteria = Criteria.where("customerCode").regex(".*" + searchText + ".*", "i");
        Criteria phoneNumberCriteria = Criteria.where("phoneNumber").regex(".*" + searchText + ".*", "i");
        Criteria emailAddressCriteria = Criteria.where("emailAddress").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                customerCodeCriteria,
                phoneNumberCriteria,
                emailAddressCriteria);
        query.addCriteria(orCriteria);

        query.addCriteria(Criteria.where("orgId").is(orgId));
        query.addCriteria(Criteria.where("outletId").is(outletId));
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        query.skip(pageable.getOffset());
        query.limit(pageable.getPageSize());

        return reactiveMongoTemplate.find(query, Customer.class).collectList().blockOptional().orElse(null);
    }

    @Override
    public Long getCount(String searchText, String outletId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");
        Criteria customerCodeCriteria = Criteria.where("customerCode").regex(".*" + searchText + ".*", "i");
        Criteria phoneNumberCriteria = Criteria.where("phoneNumber").regex(".*" + searchText + ".*", "i");
        Criteria emailAddressCriteria = Criteria.where("emailAddress").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                customerCodeCriteria,
                phoneNumberCriteria,
                emailAddressCriteria);
        query.addCriteria(orCriteria);

        query.addCriteria(Criteria.where("orgId").is(orgId));
        query.addCriteria(Criteria.where("outletId").is(outletId));

        return reactiveMongoTemplate.count(query, Customer.class).blockOptional().orElse(0L);
    }

    @Override
    public Customer getByCustomerCode(String customerCode) {
        return customerRepository.findByCustomerCode(customerCode).blockOptional().orElse(null);
    }

    @Override
    public List<Customer> getByEmailOrPhoneNumber(String email, String phno) {
        return customerRepository.findByEmailOrPhoneNum(email, phno).collectList().blockOptional().orElse(null);

    }

}
