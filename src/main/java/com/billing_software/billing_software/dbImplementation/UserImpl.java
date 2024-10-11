package com.billing_software.billing_software.dbImplementation;

import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.billing_software.billing_software.dbInterface.UserInterface;
import com.billing_software.billing_software.models.organization.Organization;
import com.billing_software.billing_software.models.user.User;
import com.billing_software.billing_software.repositories.UserRepository;

@Service
public class UserImpl implements UserInterface {

    private UserRepository userRepository;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public UserImpl(UserRepository userRepository, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.userRepository = userRepository;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public String create(User userData) {
        userRepository.save(userData).subscribe();
        return userData.getId();
    }

    @Override
    public String update(User userData) {
        Query query = Query.query(Criteria.where("_id").is(userData.getId()));

        Update update = new Update();
        if (userData.getName() != null)
            update.set("name", userData.getName());
        if (userData.getOrgId() != null)
            update.set("orgId", userData.getOrgId());
        if (userData.getUsername() != null)
            update.set("username", userData.getUsername());
        if (userData.getPassword() != null)
            update.set("password", userData.getPassword());
        if (userData.getSecret() != null)
            update.set("secret", userData.getSecret());
        if (userData.getEmail() != null)
            update.set("email", userData.getEmail());
        if (userData.getPhoneNumber() != null)
            update.set("phoneNumber", userData.getPhoneNumber());
        if (userData.getIsActive() != null)
            update.set("isActive", userData.getIsActive());
        if (userData.getActiveDesc() != null)
            update.set("activeDesc", userData.getActiveDesc());
        if (userData.getProfile() != null)
            update.set("profile", userData.getProfile());
        if (userData.getLastUpdatedDate() != null)
            update.set("lastUpdatedDate", userData.getLastUpdatedDate());
        if (userData.getResetToken() != null)
            update.set("resetToken", userData.getResetToken());

        reactiveMongoTemplate.findAndModify(query, update, User.class).subscribe();
        return userData.getId();
    }

    @Override
    public User get(String userId) {
        return userRepository.findById(userId).blockOptional().orElse(null);

    }

    @Override
    public List<User> getAllWithSearch(String searchText) {
        Query query = new Query();

        // Create the criteria for each field
        Criteria nameCriteria = Criteria.where("name").regex(".*" + searchText + ".*", "i");
        Criteria userNameCriteria = Criteria.where("username").regex(".*" + searchText + ".*", "i");
        Criteria phoneNumberCriteria = Criteria.where("phoneNumber").regex(".*" + searchText + ".*", "i");
        Criteria emailCriteria = Criteria.where("email").regex(".*" + searchText + ".*", "i");

        Criteria orCriteria = new Criteria().orOperator(
                nameCriteria,
                userNameCriteria,
                phoneNumberCriteria,
                emailCriteria);
        query.addCriteria(orCriteria);

        return reactiveMongoTemplate.find(query, User.class).collectList().blockOptional().orElse(null);
    }

    @Override
    public User getByUserName(String userName) {
        return userRepository.findByUserName(userName).blockOptional().orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).blockOptional().orElse(null);
    }

}
