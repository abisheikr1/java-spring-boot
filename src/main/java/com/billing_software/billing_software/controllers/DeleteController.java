package com.billing_software.billing_software.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.billing_software.billing_software.models.additionalCharge.AdditionalCharge;
import com.billing_software.billing_software.models.customer.Customer;
import com.billing_software.billing_software.models.item.Item;
import com.billing_software.billing_software.models.menu.OrgMenu;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.models.subscription.Subscription;
import com.billing_software.billing_software.models.tax.Tax;
import com.billing_software.billing_software.models.user.User;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/delete")
public class DeleteController {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    // Map of collection names to corresponding model classes
    private static final Map<String, Class<?>> collectionModelMap = new HashMap<>();

    static {
        collectionModelMap.put("additionalCharges", AdditionalCharge.class);
        collectionModelMap.put("customers", Customer.class);
        collectionModelMap.put("item", Item.class);
        collectionModelMap.put("orgMenus", OrgMenu.class);
        collectionModelMap.put("organizations", Organization.class);
        collectionModelMap.put("subscriptions", Subscription.class);
        collectionModelMap.put("taxes", Tax.class);
        collectionModelMap.put("users", User.class);
    }

    @DeleteMapping("/{collectionName}/{id}")
    public ResponseEntity<?> deleteById(@PathVariable String collectionName, @PathVariable String id) {
        try {
            // Get the model class for the given collection name
            Class<?> modelClass = collectionModelMap.get(collectionName);

            if (modelClass == null) {
                return new ResponseEntity<>("Collection not found", HttpStatus.BAD_REQUEST);
            }

            // Create a query to match the id field
            Query query = new Query(Criteria.where("_id").is(id));

            // Perform the deletion
            mongoTemplate.remove(query, modelClass, collectionName).subscribe();

            return new ResponseEntity<>("Document deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
