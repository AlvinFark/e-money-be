package com.project.emoney.mybatis;

import com.project.emoney.entity.User;
import com.project.emoney.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserById(long id) {
        return userMapper.getUserById(id);
    }
}
