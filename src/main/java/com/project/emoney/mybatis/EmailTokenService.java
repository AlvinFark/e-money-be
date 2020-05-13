package com.project.emoney.mybatis;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;

public interface EmailTokenService {

    EmailToken findByToken(String token);

    EmailToken findByUser(User user);

    void save(EmailToken token);

}
