package com.billing_software.billing_software.dbImplementation;

import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.OrgMenuInterface;
import com.billing_software.billing_software.models.menu.OrgMenu;
import com.billing_software.billing_software.repositories.OrgMenuRepository;

@Service
public class OrgMenuImplementation implements OrgMenuInterface {

    private OrgMenuRepository orgMenuRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public OrgMenuImplementation(OrgMenuRepository orgMenuRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.orgMenuRepository = orgMenuRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(OrgMenu orgMenuData) {
        orgMenuRepository.save(orgMenuData).subscribe();
        return orgMenuData.getId();
    }

    @Override
    public String update(OrgMenu orgMenuData) {
        Query query = Query.query(Criteria.where("_id").is(orgMenuData.getId()));

        Update update = new Update();
        if (orgMenuData.getOrgId() != null)
            update.set("orgId", orgMenuData.getOrgId());
        if (orgMenuData.getTitle() != null)
            update.set("title", orgMenuData.getTitle());
        if (orgMenuData.getDescription() != null)
            update.set("description", orgMenuData.getDescription());
        if (orgMenuData.getIcon() != null)
            update.set("icon", orgMenuData.getIcon());
        if (orgMenuData.getRoute() != null)
            update.set("route", orgMenuData.getRoute());
        if (orgMenuData.getIsEnabled() != null)
            update.set("isEnabled", orgMenuData.getIsEnabled());
        if (orgMenuData.getSubMenus() != null)
            update.set("subMenus", orgMenuData.getSubMenus());
        if (orgMenuData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", orgMenuData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, OrgMenu.class).subscribe();
        return orgMenuData.getId();
    }

    @Override
    public OrgMenu get(String orgMenuId) {
        return orgMenuRepository.findById(orgMenuId).blockOptional().orElse(null);
    }

    @Override
    public List<OrgMenu> getAll() {
        return orgMenuRepository.findAll().collectList().blockOptional().orElse(null);
    }

}