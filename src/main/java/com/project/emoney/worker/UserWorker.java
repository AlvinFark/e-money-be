package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserWorker {

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(AuthWorker.class);

  public String profile(String message) throws JsonProcessingException {

    log.info("[profile]  Receive reload profile for email: " + message);
    User user = userService.getByEmail(message);

    return objectMapper.writeValueAsString(user);
  }

  public String password(String message) throws JsonProcessingException {
    User request = objectMapper.readValue(message, User.class);
    request.setPassword(passwordEncoder.encode(request.getPassword()));
    try {
      userService.updatePassword(request);
      return "success";
    } catch (Exception e) {
      e.printStackTrace();
      return "failed";
    }
  }
}
