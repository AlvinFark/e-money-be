package com.project.emoney.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ValidationTest {

  @Autowired
  Validation validation;

  @Test
  public void validEmailTest() {
    assert validation.email("dwight@dundermifflin.co");
  }

  @Test
  public void invalidEmailTest() {
    assert !validation.email("dwight.com");
    assert !validation.email("dwight@co");
    assert !validation.email("dwight");
  }

  @Test
  public void validPasswordTest() {
    assert validation.password("P@ssw0rd");
  }

  @Test
  public void invalidPasswordTest() {
    assert !validation.password("password");
    assert !validation.password("p@ssword");
    assert !validation.password("passw0rd");
    assert !validation.password("p@ssw0r");
  }

  @Test
  public void validCardTest() {
    assert validation.card("878926747192846729");
  }

  @Test
  public void invalidCardTest() {
    assert !validation.card("8789267471928467292");
    assert !validation.card("87892674719284672");
    assert !validation.card("878926747198f4672A");
    assert !validation.card("87892674719a84672!");
  }

  @Test
  public void validNameTest() {
    assert validation.name("Dwight Schrute");
  }

  @Test
  public void invalidNameTest() {
    assert !validation.name("A");
    assert !validation.name("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
  }

  @Test
  public void validOTPTest() {
    assert validation.otp("9582");
  }
}
