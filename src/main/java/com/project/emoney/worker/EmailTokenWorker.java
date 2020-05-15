package com.project.emoney.worker;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.service.UserService;
import com.project.emoney.utils.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailTokenWorker {

  @Autowired
  EmailTokenService emailTokenService;

  @Autowired
  UserService userService;

  public String verify(String token){
    EmailToken emailToken = emailTokenService.getVerificationToken(token);
    if (emailToken==null){
      return "invalid code";
    }
    if (emailToken.getTime().plusHours(GlobalVariable.EMAILTOKEN_LIFETIME_HOURS).isBefore(LocalDateTime.now())){
      return "expired code";
    }
    User user = userService.getUserById(emailToken.getUserId());
    userService.activateUser(user);
    return "success";
  }
}
