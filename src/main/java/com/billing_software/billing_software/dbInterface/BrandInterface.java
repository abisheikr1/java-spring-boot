package com.billing_software.billing_software.dbInterface;

import com.billing_software.billing_software.models.brand.Brand;
import java.util.List;

public interface BrandInterface {

    public String create(Brand brandData);

    public String update(Brand brandData);

    public Brand get(String brandId);

    public List<Brand> getAllWithSearch(String searchText, int pageNumber, int pageSize);

    public Long getCount(String searchText);

}
