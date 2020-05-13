package com.project.emoney.mybatis;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;

public interface UserService {

    User getUserById(long id);
    User getUserByEmailOrPhone(String emailOrPhone);
    User getUserByEmail(String email);

    User insert(User user);
    void createVerificationToken(User user, String token);
    EmailToken getVerificationToken(String VerificationToken);

    void activateUser(User user);
}
