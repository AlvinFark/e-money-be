package com.project.emoney.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Generator {

  private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  Random random = new Random();

  public String generateOtp() {
    return String.format("%04d", random.nextInt(10000));
  }

  public String generateToken() {
    int count = 20;
    StringBuilder builder = new StringBuilder();
    while (count-- != 0) {
      int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
      builder.append(ALPHA_NUMERIC_STRING.charAt(character));
    }
    return builder.toString();

  }
}
