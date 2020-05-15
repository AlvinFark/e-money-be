package com.project.emoney.service;

import com.project.emoney.entity.User;

public interface UserService {
    User getUserById(long id);
    User getUserByEmailOrPhone(String emailOrPhone);
    User getUserByEmail(String email);

    User insert(User user);
    void updateBalance(User user);
    void updatePassword(User user);
    void setActiveByEmail(String email);
    void activateUser(User user);
}
