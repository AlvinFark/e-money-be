package com.project.emoney.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Generator {

  Random random = new Random();

  public String generateOtp() {
    return String.format("%04d", random.nextInt(10000));
  }
}
