package com.billing_software.billing_software.models.item;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.brand.Brand;
import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.payloads.requests.ItemUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("item")
public class Item extends CoreContext {

    @Transient
    private static String shortCode = "ITM";

    private String id; // Unique identifier for the product/service
    private String orgId; // Belongs to organization
    private String itemCode; // Unique code for the item
    private String name; // Name of the product or service
    private String type; // Type of the item ("Product" or "Service")
    private String description; // Detailed description of the product or service
    private String hsnCode; // Harmonized System of Nomenclature code for products
    private String sacCode; // Services Accounting Code for services
    private Double mrp; // Maximum Retail Price
    private Double msp; // Minimum Selling Price
    private String unitOfMeasure; // Unit of measure (e.g., "pcs", "kg", "hour")
    private Double unitPrice; // Price per unit of the item (can be a standard selling price)
    private Integer availableStock; // Available stock for products (not applicable for services)
    private String currency; // Currency type for the pricing
    private String taxId; // which tax slab is applicable for product

    private String barcode; // Barcode for product
    private Date expiryDate; // Expiry date
    private BatchDetails batchDetails; // Item bacth details

    private String outletId; // belongs to outlet
    private Map<String, Object> profile; // user Logo
    private String brandId; // Brand of the item
    private Brand brandDetails; // Brand of the item

    public Item(ItemUpsertRequest itemUpsertrequest) {
        if (itemUpsertrequest.id != null) {
            this.id = itemUpsertrequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(Item.shortCode);
            this.setCreatedDate(new Date());
        }

        this.orgId = itemUpsertrequest.orgId;
        this.itemCode = itemUpsertrequest.itemCode;

        this.name = itemUpsertrequest.name;
        this.type = itemUpsertrequest.type;
        this.description = itemUpsertrequest.description;
        this.hsnCode = itemUpsertrequest.hsnCode;
        this.sacCode = itemUpsertrequest.sacCode;
        this.mrp = itemUpsertrequest.mrp;
        this.msp = itemUpsertrequest.msp;
        this.unitOfMeasure = itemUpsertrequest.unitOfMeasure;
        this.unitPrice = itemUpsertrequest.unitPrice;
        this.availableStock = itemUpsertrequest.availableStock;
        this.currency = itemUpsertrequest.currency;
        this.batchDetails = itemUpsertrequest.batchDetails;
        this.expiryDate = itemUpsertrequest.expiryDate;
        this.outletId = itemUpsertrequest.outletId;
        this.taxId = itemUpsertrequest.taxId;
        this.brandId = itemUpsertrequest.brandId;

        if(this.barcode == null){
            this.barcode = GenerateIdService.generateEAN13Barcode();
        }
    }

    public void setProfile(String imageUrl, String imageType) {
        if (this.profile == null) {
            this.profile = new HashMap<>();
        }
        this.profile.put(imageType, imageUrl);
    }

}
