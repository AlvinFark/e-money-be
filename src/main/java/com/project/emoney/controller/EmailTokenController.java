package com.project.emoney.controller;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.Locale;

@RestController
public class EmailTokenController {

  @Autowired
  UserService userService;

  @Autowired
  EmailTokenService emailTokenService;

  @RequestMapping(value = "/confirmRegistration", method = RequestMethod.GET)
  public String confirmRegistration(WebRequest request, @RequestParam("token") String token) {

    Locale locale = request.getLocale();
    EmailToken verificationToken = emailTokenService.getVerificationToken(token);
    if(verificationToken == null) {
      return "redirect:access-denied";
    }

    User user = verificationToken.getUser();
    Calendar calendar = Calendar.getInstance();
    if((verificationToken.getExpiryDate().getTime()-calendar.getTime().getTime())<=0) {
      return "redirect:access-denied";
    }

    user.setActive(true);
    userService.activateUser(user);
    return null;
  }
}
