package com.project.emoney.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class Validation {
  String emailRegex = ".+@.+\\..+";

  public boolean email (String email) {
    return Pattern.matches(emailRegex, email);
  }

  public boolean password (String password) {
    return password.length()>=4&&password.length()<=50;
  }

  public boolean card (String card) {
    if (card.length()!=16){
      return false;
    }
    try {
      Long.parseLong(card);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean name(String name) {
    return name.length()>=2&&name.length()<=50;
  }

  public boolean otp (String otp) {
    if (otp.length()!=4){
      return false;
    }
    try {
      Long.parseLong(otp);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
