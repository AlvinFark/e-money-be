package com.project.emoney.service.impl;

import com.project.emoney.entity.User;
import com.project.emoney.mapper.UserMapper;
import com.project.emoney.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(long id) {
        return userMapper.getUserById(id);
    }

    @Override
    public User getUserByEmailOrPhone(String emailOrPhone) {
        return userMapper.getUserByEmailOrPhone(emailOrPhone);
    }

    public User insert(User user) {
        userMapper.insert(user);
        return getUserByEmailOrPhone(user.getPhone());
    }

    @Override
    public User getUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }

    @Override
    public void setActive(String email) {
        userMapper.setActive(email);
    }

    @Override
    public void updateBalance(User user) {
        userMapper.updateBalance(user);
    }

    @Override
    public void updatePassword(User user) {
        userMapper.updatePassword(user);
    }

    @Override
    public void activateUser(User user) {
        userMapper.activateUser(user);
    }
}
