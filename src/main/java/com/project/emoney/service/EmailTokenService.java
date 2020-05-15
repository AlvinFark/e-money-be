package com.project.emoney.service;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;

public interface EmailTokenService {
    void createVerificationToken(User user, String token);
    EmailToken getVerificationToken(String VerificationToken);
}
