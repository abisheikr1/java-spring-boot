package com.billing_software.billing_software.models.financialDocument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentItem {

    private String itemId; // Unique identifier for the item
    private String itemName; // Name or description of the item
    private Integer quantity; // Quantity of the item
    private Double actualPrice; // Total price for the item (msp * quantity)
    private Double totalTax; // Total price for the item (msp * quantity)
    private Double totalPrice; // Total price for the item (msp * quantity)
    private String description; // Description for item
    private String taxRateDesc; // Description for item

    private String hsnCode; // Harmonized System of Nomenclature code for products
    private String sacCode; // Services Accounting Code for services

}
