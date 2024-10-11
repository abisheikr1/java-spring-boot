package com.billing_software.billing_software.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.AdditionalChargeInterface;
import com.billing_software.billing_software.dbInterface.BrandInterface;
import com.billing_software.billing_software.dbInterface.ItemInterface;
import com.billing_software.billing_software.dbInterface.TaxInterface;
import com.billing_software.billing_software.models.item.Item;

@Service
public class QuickViewService {

    private final ItemInterface itemInterface;
    private final BrandInterface brandInterface;
    private final AdditionalChargeInterface additionalChargeInterface;
    private final TaxInterface taxInterface;

    // Constructor to inject dependencies
    public QuickViewService(ItemInterface itemInterface, BrandInterface brandInterface,
            AdditionalChargeInterface additionalChargeInterface, TaxInterface taxInterface) {
        this.itemInterface = itemInterface;
        this.brandInterface = brandInterface;
        this.additionalChargeInterface = additionalChargeInterface;
        this.taxInterface = taxInterface;
    }

    // Method to get quick view response based on parameters
    public Object getQuickViewResponse(boolean isGetProduct, boolean isGetAdditionalCharge,
            boolean isGetTaxDetail, String productGroupBy) {
        Map<String, Object> response = new HashMap<>();

        // Fetch products if requested
        if (isGetProduct) {
            List<Item> products = itemInterface.getAll();
            // Set brand details for each product
            products.forEach(eachItem -> {
                if (eachItem.getBrandId() != null) {
                    eachItem.setBrandDetails(brandInterface.get(eachItem.getBrandId()));
                }
            });

            // Group products based on the specified criteria
            if (productGroupBy != null && "type".equals(productGroupBy)) {
                response.put("items", groupProductsByType(products));
            } else if (productGroupBy != null && "brand".equals(productGroupBy)) {
                response.put("items", groupProductsByBrand(products));
            } else {
                response.put("items", products);
            }
        }

        // Fetch additional charges if requested
        if (isGetAdditionalCharge) {
            response.put("additionalCharges", additionalChargeInterface.getAll());
        }

        // Fetch tax details if requested
        if (isGetTaxDetail) {
            response.put("taxes", taxInterface.getAll());
        }

        return response;
    }

    // Group products by their type
    private Map<String, List<Item>> groupProductsByType(List<Item> products) {
        // Partition products based on whether the type is null and group them
        Map<Boolean, Map<String, List<Item>>> partitionedProducts = products.stream()
                .collect(Collectors.partitioningBy(
                        item -> item.getType() == null, // Partition based on null type
                        Collectors.groupingBy(item -> item.getType() == null ? "Others" : item.getType()) // Group by
                                                                                                          // type
                ));

        // Merge non-null and null type groups into a single map
        Map<String, List<Item>> groupedProducts = partitionedProducts.get(false); // Non-null types
        groupedProducts.putAll(partitionedProducts.get(true)); // Null types as "Others"

        return groupedProducts;
    }

    // Group products by their brand
    private Map<String, List<Item>> groupProductsByBrand(List<Item> products) {
        return products.stream()
                .collect(Collectors.groupingBy(
                        item -> {
                            String brandName = item.getBrandDetails() != null ? item.getBrandDetails().getName() : null;
                            return brandName != null ? brandName : "Others"; // Default value for null brand names
                        }));
    }
}
