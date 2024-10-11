package com.billing_software.billing_software.dbInterface;

import java.util.List;

import com.billing_software.billing_software.models.customer.Customer;

public interface CustomerInterface {

    public String create(Customer customerData);

    public String update(Customer customerData);

    public Customer get(String customerId);

    public List<Customer> getAllWithSearch(String searchText, int pageNumber, int pageSize, String outletId);

    public Long getCount(String searchText, String outletId);

    public Customer getByCustomerCode(String customerCode);

    public List<Customer> getByEmailOrPhoneNumber(String email, String phno);

}
