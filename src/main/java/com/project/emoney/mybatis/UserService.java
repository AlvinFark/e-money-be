package com.project.emoney.mybatis;

import com.project.emoney.entity.User;

public interface UserService {

    User getUserById(long id);
    User getUserByEmailOrPhone(String emailOrPhone);
    User insert(User user);
    User getUserByEmail(String email);
    void setActive(String email);
}
