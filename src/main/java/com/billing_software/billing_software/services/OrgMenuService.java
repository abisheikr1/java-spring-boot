package com.billing_software.billing_software.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.OrgMenuInterface;
import com.billing_software.billing_software.dbInterface.OrganizationInterface;
import com.billing_software.billing_software.models.menu.OrgMenu;
import com.billing_software.billing_software.payloads.requests.OrgMenuUpsertRequest;

@Service
public class OrgMenuService {

    private OrgMenuInterface orgMenuInterface;
    private OrganizationInterface organizationInterface;

    public OrgMenuService(OrgMenuInterface orgMenuInterface, OrganizationInterface organizationInterface) {
        this.orgMenuInterface = orgMenuInterface;
        this.organizationInterface = organizationInterface;
    }

    public String createOrUpdateOrgMenu(OrgMenuUpsertRequest orgMenuUpsertRequest) {
        OrgMenu orgMenuData = new OrgMenu(orgMenuUpsertRequest);

        if (orgMenuUpsertRequest.orgId != null && organizationInterface.get(orgMenuUpsertRequest.orgId) == null) {
            throw new RuntimeException("Invalid org Id");
        }

        if (orgMenuUpsertRequest.id != null) {
            if (orgMenuInterface.get(orgMenuUpsertRequest.id) != null) {
                return orgMenuInterface.update(orgMenuData);
            } else {
                throw new RuntimeException("Invalid orgMenu Id");
            }
        } else {
            return orgMenuInterface.create(orgMenuData);
        }
    }

    public String createOrUpdateOrgSubMenu(OrgMenuUpsertRequest orgMenuUpsertRequest, String menuId) {
        OrgMenu menuData = orgMenuInterface.get(menuId);
        if (menuData != null) {
            OrgMenu orgSubMenu = new OrgMenu(orgMenuUpsertRequest);
            menuData.upsertSubMenu(orgSubMenu, orgMenuUpsertRequest);
            orgMenuInterface.update(menuData);
            return orgSubMenu.getId();
        } else {
            throw new RuntimeException("Invalid orgMenu Id");
        }
    }

    public OrgMenu getOrgMenuById(String orgMenuId) {
        return orgMenuInterface.get(orgMenuId);
    }

    public List<OrgMenu> getAllOrgMenu() {
        return orgMenuInterface.getAll();
    }
}