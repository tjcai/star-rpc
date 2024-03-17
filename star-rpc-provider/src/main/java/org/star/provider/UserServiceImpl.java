package org.star.provider;

import org.star.common.model.User;
import org.star.common.service.UserService;

public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("UserServiceImpl.getUser");
        return user;
    }
}
