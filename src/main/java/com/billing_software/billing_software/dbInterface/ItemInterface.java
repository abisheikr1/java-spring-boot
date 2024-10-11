package com.billing_software.billing_software.dbInterface;

import java.util.List;
import com.billing_software.billing_software.models.item.Item;

public interface ItemInterface {

    public String create(Item itemData);

    public String update(Item itemData);

    public Item get(String itemId);

    public List<Item> getAll();

    public List<Item> getAllWithSearch(String searchText, int pageNumber, int pageSize, String itemType,
            String outletId);

    public Long getCount(String searchText, String itemType, String outletId);

    public Item getByCode(String itemCode);

}
