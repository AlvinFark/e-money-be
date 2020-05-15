package com.project.emoney.controller;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.Locale;

@RestController
public class EmailTokenController {

  @Autowired
  UserService userService;

  @Autowired
  EmailTokenService emailTokenService;

  @GetMapping(value = "/api/verify/code?{token}")
  public String confirmRegistration(@PathVariable String token) {
    return null;
  }
}
