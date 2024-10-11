package com.billing_software.billing_software.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.billing_software.billing_software.dbInterface.BrandInterface;
import com.billing_software.billing_software.dbInterface.ItemInterface;
import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.models.brand.Brand;
import com.billing_software.billing_software.models.item.Item;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.payloads.requests.ItemUpsertRequest;
import com.billing_software.billing_software.utils.cloudinary.CloudinaryService;
import com.billing_software.billing_software.utils.commons.BarcodeService;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ItemService {

    private ItemInterface itemInterface;
    private OrganizationInterface organizationInterface;
    private CloudinaryService cloudinaryService;
    private BrandInterface brandInterface;
    private BarcodeService barcodeService;

    public ItemService(ItemInterface itemInterface, OrganizationInterface organizationInterface,
            CloudinaryService cloudinaryService, BrandInterface brandInterface, BarcodeService barcodeService) {
        this.itemInterface = itemInterface;
        this.organizationInterface = organizationInterface;
        this.cloudinaryService = cloudinaryService;
        this.brandInterface = brandInterface;
        this.barcodeService = barcodeService;
    }

    public String createOrUpdateItem(ItemUpsertRequest itemUpsertrequest) {
        Item itemData = new Item(itemUpsertrequest);

        if (itemUpsertrequest.itemCode != null) {
            Item ietmDetails = itemInterface.getByCode(itemUpsertrequest.itemCode);
            if (itemUpsertrequest.id != null) {
                if (!ietmDetails.getId().equals(itemUpsertrequest.id)) {
                    throw new RuntimeException("Item Code alredy used");
                }
            } else {
                if (ietmDetails != null) {
                    throw new RuntimeException("Item Code alredy used");
                }
            }
        }

        Organization orgDetails = organizationInterface.get(itemUpsertrequest.orgId);

        if ((itemUpsertrequest.orgId != null && orgDetails == null) || itemUpsertrequest.orgId == null) {
            throw new RuntimeException("Invalid org Id");
        }

        if (itemUpsertrequest.outletId == null) {
            throw new RuntimeException("Invalid outletId Id");
        }

        if (itemUpsertrequest.brandId != null) {
            Brand brandFound = brandInterface.get(itemUpsertrequest.brandId);
            if (brandFound == null) {
                throw new RuntimeException("Invalid brand Id");
            }
        }

        if (itemUpsertrequest.id != null) {
            Item itemFound = itemInterface.get(itemUpsertrequest.id);
            if (itemFound != null) {
                if (itemFound.getProfile() == null || itemFound.getProfile().get("barcode") == null) {
                    uploadBarcode(itemData);
                }
                return itemInterface.update(itemData);
            } else {
                throw new RuntimeException("Invalid item Id");
            }
        } else {
            return itemInterface.create(itemData);
        }
    }

    public Item getIteamById(String itemId) {
        Item itemData = itemInterface.get(itemId);
        if (itemData.getBrandId() != null) {
            itemData.setBrandDetails(brandInterface.get(itemData.getBrandId()));
        }
        return itemData;
    }

    public Object getAllItemWithSearch(String searchtext, int pageNumber, int pageSize, String itemType,
            String outletId) {
        Map<String, Object> response = new HashMap();
        List<Item> itemList = itemInterface.getAllWithSearch(searchtext, pageNumber, pageSize, itemType, outletId);

        itemList.stream().forEach(eachItem -> {
            if(eachItem.getBrandId() != null){
                eachItem.setBrandDetails(brandInterface.get(eachItem.getBrandId()));
            }
        });

        response.put("items", itemList);
        response.put("totalCount", itemInterface.getCount(searchtext, itemType, outletId));
        response.put("searchText", searchtext);

        return response;
    }

    public Map<String, Object> uploadProfile(MultipartFile file, String itemId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Item itemDetail = itemInterface.get(itemId);
        if (itemDetail == null) {
            throw new RuntimeException("Invalid item Id");
        }
        String imageUrl = cloudinaryService.uploadImage(file,
                "organization/" + orgId + "/item/" + itemId + "/profile",
                "profile");
        itemDetail.setProfile(imageUrl, "profile");
        itemInterface.update(itemDetail);
        return itemDetail.getProfile();
    }

    public String deleteProfile(String itemId, String publicUrl) {
        Item itemDetail = itemInterface.get(itemId);
        if (itemDetail == null) {
            throw new RuntimeException("Invalid item Id");
        }

        itemDetail.getProfile().remove("profile");

        cloudinaryService.deleteImage(publicUrl);
        itemInterface.update(itemDetail);
        return "deleted Successfully";
    }

    private Map<String, Object> uploadBarcode(Item itemDetail) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        byte[] barcodeBytes = barcodeService.generateBarcode(itemDetail.getBarcode());

        String imageUrl = cloudinaryService.uploadImage(barcodeBytes,
                "organization/" + orgId + "/item/" + itemDetail.getId() + "/barcode",
                "barcode");
        itemDetail.setProfile(imageUrl, "barcode");
        itemInterface.update(itemDetail);
        return itemDetail.getProfile();
    }

}
