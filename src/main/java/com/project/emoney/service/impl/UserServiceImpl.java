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
    public User getById(long id) {
        return userMapper.getById(id);
    }

    @Override
    public User getByEmailOrPhone(String emailOrPhone) {
        return userMapper.getByEmailOrPhone(emailOrPhone);
    }

    public User insert(User user) {
        userMapper.insert(user);
        return getByEmailOrPhone(user.getPhone());
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.getByEmail(email);
    }

    @Override
    public void setActiveByEmail(String email) {
        userMapper.setActiveByEmail(email);
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
    public void activate(User user) {
        userMapper.activate(user);
    }
}
