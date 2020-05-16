package com.project.emoney.service;

import com.project.emoney.entity.User;

public interface UserService {
    User getById(long id);
    User getByEmailOrPhone(String emailOrPhone);
    User getByEmail(String email);

    User insert(User user);
    void updateBalance(User user);
    void updatePassword(User user);
    void setActiveByEmail(String email);
    void activate(User user);
}
