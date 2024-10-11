package com.billing_software.billing_software.dbInterface;

import com.billing_software.billing_software.models.tax.Tax;
import java.util.List;

public interface TaxInterface {

    public String create(Tax taxData);

    public String update(Tax taxData);

    public Tax get(String taxId);

    public List<Tax> getAll();

    public List<Tax> getAllWithSearchText(String searchText);

}