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
    assert validation.card("8789267471928467");
  }

  @Test
  public void invalidCardTest() {
    assert !validation.card("87892674719284679");
    assert !validation.card("878926747192846");
    assert !validation.card("878926747198f46A");
    assert !validation.card("87892674719a846!");
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

  @Test
  public void invalidOTPTest() {
    assert !validation.otp("958");
    assert !validation.otp("95824");
    assert !validation.otp("982A");
  }

  @Test
  public void validPhoneTest() {
    assert validation.phone("628595828593");
  }

  @Test
  public void invalidPhoneTest() {
    assert !validation.phone("628595828");
    assert !validation.phone("628595828294591");
    assert !validation.phone("6285958282AA");
  }

  @Test void convertPhoneTest() {
    assert validation.convertPhone("08595828593").equals("628595828593");
    assert validation.convertPhone("+628595828593").equals("628595828593");
    assert validation.convertPhone("628595828593").equals("628595828593");
  }
}
