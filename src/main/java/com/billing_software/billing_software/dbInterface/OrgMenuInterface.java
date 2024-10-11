package com.billing_software.billing_software.dbInterface;

import java.util.List;

import com.billing_software.billing_software.models.menu.OrgMenu;

public interface OrgMenuInterface {

    public String create(OrgMenu orgMenuData);

    public String update(OrgMenu orgMenuData);

    public OrgMenu get(String orgMenuId);

    public List<OrgMenu> getAll();

}
