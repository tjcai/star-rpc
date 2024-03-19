package org.star.common.service;

import org.star.common.model.User;

public interface UserService {
    User getUser(User user);

    default Integer getNumber() {
        return 1;
    }
}

