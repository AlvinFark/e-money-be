package com.project.emoney.service;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.mapper.EmailTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailTokenImpl implements EmailTokenService{

    @Autowired
    EmailTokenMapper emailTokenMapper;

    @Override
    public EmailToken findByToken(String token) {
        return emailTokenMapper.findTokenByToken(token);
    }

    @Override
    public EmailToken findByUser(User user) {
        return emailTokenMapper.findTokenByUser(user);
    }

    @Override
    public void save(EmailToken token) {
        emailTokenMapper.createToken(token);
    }
}
