package com.project.emoney.service.impl;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.mapper.EmailTokenMapper;
import com.project.emoney.service.EmailTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailTokenServiceImpl implements EmailTokenService {

    @Autowired
    EmailTokenMapper emailTokenMapper;

    @Override
    public void createVerificationToken(User user, String token) {
        EmailToken newUserToken = new EmailToken(token, user);
        emailTokenMapper.createToken(newUserToken);
    }

    @Override
    public EmailToken getVerificationToken(String verificationToken) {
        return emailTokenMapper.findTokenByToken(verificationToken);
    }
}
