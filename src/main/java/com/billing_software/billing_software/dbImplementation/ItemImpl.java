package com.billing_software.billing_software.dbImplementation;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.billing_software.billing_software.dbInterface.ItemInterface;
import com.billing_software.billing_software.models.item.Item;
import com.billing_software.billing_software.repositories.ItemRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;

@Service
public class ItemImpl implements ItemInterface {

    private ItemRepository itemRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public ItemImpl(ItemRepository itemRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.itemRepository = itemRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(Item itemData) {
        itemRepository.save(itemData).subscribe();
        return itemData.getId();
    }

    @Override
    public String update(Item itemData) {
        Query query = Query.query(Criteria.where("_id").is(itemData.getId()));

        Update update = new Update();
        if (itemData.getOrgId() != null)
            update.set("orgId", itemData.getOrgId());
        if (itemData.getItemCode() != null)
            update.set("itemCode", itemData.getItemCode());
        if (itemData.getName() != null)
            update.set("name", itemData.getName());
        if (itemData.getType() != null)
            update.set("type", itemData.getType());
        if (itemData.getDescription() != null)
            update.set("description", itemData.getDescription());
        if (itemData.getHsnCode() != null)
            update.set("hsnCode", itemData.getHsnCode());
        if (itemData.getSacCode() != null)
            update.set("sacCode", itemData.getSacCode());
        if (itemData.getMrp() != null)
            update.set("mrp", itemData.getMrp());
        if (itemData.getMsp() != null)
            update.set("msp", itemData.getMsp());
        if (itemData.getUnitOfMeasure() != null)
            update.set("unitOfMeasure", itemData.getUnitOfMeasure());
        if (itemData.getUnitPrice() != null)
            update.set("unitPrice", itemData.getUnitPrice());
        if (itemData.getAvailableStock() != null)
            update.set("availableStock", itemData.getAvailableStock());
        if (itemData.getCurrency() != null)
            update.set("currency", itemData.getCurrency());
        if (itemData.getProfile() != null)
            update.set("profile", itemData.getProfile());
        if (itemData.getBarcode() != null)
            update.set("barcode", itemData.getBarcode());
        if (itemData.getBrandId() != null)
            update.set("brandId", itemData.getBrandId());
        if (itemData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", itemData.getLastUpdatedDate());

        reactiveMongoTemplate.findAndModify(query, update, Item.class).subscribe();
        return itemData.getId();
    }

    @Override
    public Item get(String itemId) {
        return itemRepository.findById(itemId).blockOptional().orElse(null);
    }

    @Override
    public List<Item> getAllWithSearch(String searchText, int pageNumber, int pageSize, String itemType,
            String outletId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");
        Criteria itemCodeCriteria = Criteria.where("itemCode").regex(".*" + searchText + ".*", "i");
        Criteria descriptionriteria = Criteria.where("description").regex(".*" + searchText + ".*", "i");
        Criteria hsnCriteria = Criteria.where("hsnCode").regex(".*" + searchText + ".*", "i");
        Criteria sacCriteria = Criteria.where("sacCode").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria, itemCodeCriteria,
                descriptionriteria,
                hsnCriteria,
                sacCriteria);
        query.addCriteria(orCriteria);

        query.addCriteria(Criteria.where("orgId").is(orgId));
        query.addCriteria(Criteria.where("type").is(itemType));
        query.addCriteria(Criteria.where("outletId").is(outletId));
        query.with(Sort.by(Sort.Direction.DESC, "createdDate"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        query.skip(pageable.getOffset());
        query.limit(pageable.getPageSize());

        return reactiveMongoTemplate.find(query, Item.class).collectList().blockOptional().orElse(null);
    }

    @Override
    public Long getCount(String searchText, String itemType, String outletId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");
        Criteria itemCodeCriteria = Criteria.where("itemCode").regex(".*" + searchText + ".*", "i");
        Criteria descriptionriteria = Criteria.where("description").regex(".*" + searchText + ".*", "i");
        Criteria hsnCriteria = Criteria.where("hsnCode").regex(".*" + searchText + ".*", "i");
        Criteria sacCriteria = Criteria.where("sacCode").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                itemCodeCriteria,
                descriptionriteria,
                hsnCriteria,
                sacCriteria);
        query.addCriteria(orCriteria);

        query.addCriteria(Criteria.where("orgId").is(orgId));
        query.addCriteria(Criteria.where("type").is(itemType));
        query.addCriteria(Criteria.where("outletId").is(outletId));

        return reactiveMongoTemplate.count(query, Item.class).blockOptional().orElse(0L);
    }

    @Override
    public Item getByCode(String itemCode) {
        return itemRepository.finByItemCode(itemCode).blockOptional().orElse(null);
    }

    @Override
    public List<Item> getAll() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String orgId = (String) request.getAttribute("orgId");

        Query query = new Query();
        query.addCriteria(Criteria.where("orgId").is(orgId));

        return reactiveMongoTemplate.find(query, Item.class).collectList().blockOptional().orElse(null);
    }

}