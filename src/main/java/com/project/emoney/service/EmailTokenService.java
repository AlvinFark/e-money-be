package com.project.emoney.service;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;

public interface EmailTokenService {
    void insertByUserAndToken(User user, String token);
    EmailToken getByToken(String VerificationToken);
    String sendEmail(User user);
}
