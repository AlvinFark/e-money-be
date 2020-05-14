package com.project.emoney.service;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;

public interface EmailTokenService {
    EmailToken findByToken(String token);
    EmailToken findByUser(User user);
    void save(EmailToken token);
    void createVerificationToken(User user, String token);
    EmailToken getVerificationToken(String VerificationToken);
}
