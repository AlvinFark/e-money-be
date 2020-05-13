package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.mybatis.OTPService;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.LoginRequest;
import com.project.emoney.payload.UserWithToken;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.security.JwtUserDetailsService;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Service
public class UserWorker {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtUserDetailsService userDetailsService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  ObjectMapper objectMapper = new ObjectMapper();
  private static Logger log = LoggerFactory.getLogger(AuthWorker.class);

  public String profile(String message) throws JsonProcessingException {

    log.info("[profile]  Receive reload profile for email: " + message);
    User user = userService.getUserByEmail(message);

    return objectMapper.writeValueAsString(user);
  }

  public String password(String message) throws JsonProcessingException {
    User request = objectMapper.readValue(message, User.class);
    request.setPassword(passwordEncoder.encode(request.getPassword()));
    userService.updatePassword(request);
    return "success";
  }
}
