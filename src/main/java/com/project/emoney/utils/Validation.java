package com.project.emoney.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class Validation {
  final String emailRegex = ".+@.+\\..+";
  final String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$";

  public boolean email (String email) {
    return Pattern.matches(emailRegex, email);
  }

  public boolean password (String password) {
    return Pattern.matches(passwordRegex, password);
  }

  public boolean card (String card) {
    if (card.length()!=16){
      return false;
    }
    try {
      Long.parseLong(card);
      return true;
    } catch (NumberFormatException e) {
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

  public boolean phone(String phone) {
    if (phone.length()<10||phone.length()>14){
      return false;
    }
    try {
      Long.parseLong(phone);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String convertPhone(String phone) {
    if (phone.charAt(0)=='+'){
      return phone.substring(1);
    }
    if (phone.charAt(0)=='0'){
      return "62" + phone.substring(1);
    }
    return phone;
  }
}
