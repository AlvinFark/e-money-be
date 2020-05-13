package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.LoginRequest;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Service
public class UserWorker {

  @Autowired
  private UserService userService;

  ObjectMapper objectMapper = new ObjectMapper();
  private static Logger log = LoggerFactory.getLogger(AuthWorker.class);

  public String profile(String message) throws JsonProcessingException {
    String email = message;

    log.info("[profile]  Receive reload profile for email: " + email);
    User user = userService.getUserByEmail(email);
    String response = objectMapper.writeValueAsString(user);

    return response;
  }
}
