package com.billing_software.billing_software.models.financialDocument;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalChargeItem {

    private String chargeId; // Unique identifier for the additional charge
    private String chargeName; // Name or description of the item
    private Integer quantity; // Quantity of the item
    private Double actualPrice; // Total price for the item (msp * quantity)
    private Double totalPrice; // Total price for the item (msp * quantity)
    private String description; // Description for item

}
