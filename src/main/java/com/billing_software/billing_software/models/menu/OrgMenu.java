package com.billing_software.billing_software.models.menu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.billing_software.billing_software.models.common.CoreContext;
import com.billing_software.billing_software.payloads.requests.OrgMenuUpsertRequest;
import com.billing_software.billing_software.utils.GenerateIdService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("orgMenus")
public class OrgMenu extends CoreContext {

    @Transient
    private static String shortCode = "MEN";

    private String id; // Unique identifier for the menu
    private String orgId; // Organization ID to which this menu belongs
    private String title; // Title of the menu item
    private String description; // Description of the menu item
    private String icon; // Icon name or URL associated with the menu item
    private String route; // Front-end route or URL to navigate when this menu item is clicked
    private Boolean isEnabled; // Whether this menu item is enabled or not
    private List<OrgMenu> subMenus; // List of sub-menu items

    public OrgMenu(OrgMenuUpsertRequest orgMenuUpsertRequest) {
        if (orgMenuUpsertRequest.id != null) {
            this.id = orgMenuUpsertRequest.id;
            this.setLastUpdatedDate(new Date());
        } else {
            this.id = GenerateIdService.generateId(OrgMenu.shortCode);
            this.setCreatedDate(new Date());
        }

        this.orgId = orgMenuUpsertRequest.orgId;
        this.title = orgMenuUpsertRequest.title;
        this.description = orgMenuUpsertRequest.description;
        this.icon = orgMenuUpsertRequest.icon;
        this.route = orgMenuUpsertRequest.route;
        this.isEnabled = true;
    }

  
    public void upsertSubMenu(OrgMenu subMenu, OrgMenuUpsertRequest orgSubMenuUpsertRequest) {
        if (this.subMenus == null) {
            this.subMenus = new ArrayList<>();
            this.subMenus.add(subMenu);
        } else {
            OrgMenu subMenuFound = this.subMenus.stream()
                    .filter(o -> o.getId().equals(subMenu.getId()))
                    .findFirst().orElse(null);
            if (subMenuFound != null) {
                int index = this.subMenus.indexOf(subMenuFound);
                this.subMenus.set(index, subMenu);
            } else {
                if (orgSubMenuUpsertRequest.id == null) {
                    this.subMenus.add(subMenu);
                } else {
                    throw new RuntimeException("SubMenu id not found");
                }
            }
        }
    }

}
