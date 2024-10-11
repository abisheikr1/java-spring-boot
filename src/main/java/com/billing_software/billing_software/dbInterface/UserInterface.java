package com.billing_software.billing_software.dbInterface;

import com.billing_software.billing_software.models.user.User;
import java.util.List;

public interface UserInterface {

    public String create(User userData);

    public String update(User userData);

    public User get(String userId);

    public User getByUserName(String userName);

    public User getByEmail(String email);

    public List<User> getAllWithSearch(String searchText);

}