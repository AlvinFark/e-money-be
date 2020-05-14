package com.project.emoney.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.payload.response.ResponseWrapper;
import com.project.emoney.service.UserService;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import com.project.emoney.payload.request.LoginRequest;
import com.project.emoney.payload.dto.UserWithToken;
import com.project.emoney.utils.RPCClient;
import com.project.emoney.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@RestController
@CrossOrigin
public class AuthController {

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  Validation validation;

  @Autowired
  private UserService userService;

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  PasswordEncoder passwordEncoder;

  @RequestMapping(value = "/api/register", method = RequestMethod.POST)
  public ResponseEntity<?> saveUser(@RequestBody User user, HttpServletRequest request, Locale locale) throws Exception {
    //validate password
    if (!validation.password(user.getPassword())){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
    } else {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    //validate name
    if(!validation.email(user.getEmail())) {
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
    }

    //validate name
    if(!validation.name(user.getName())) {
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
    }

    //validate phone & convert phone
    if (!validation.phone(user.getPhone())) {
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
    } else {
      user.setPhone(validation.convertPhone(user.getPhone()));
    }

    //check email & phone duplication
    if (userService.getUserByEmailOrPhone(user.getEmail()) != null || userService.getUserByEmailOrPhone(user.getPhone()) != null) {
      return new ResponseEntity<>(new SimpleResponseWrapper(409, "user with this phone number or email already exist"), HttpStatus.CONFLICT);
    }

    //send and receive MQ
    RPCClient rpcClient = new RPCClient("register");
    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(user));

    //translate MQ response
    switch (responseMQ) {
      case "inactive account, otp sent":
        //Send email verification
//      try {
//        String appUrl = request.getRequestURL().toString();
//        eventPublisher.publishEvent(new OnRegistrationSuccessEvent(user, locale, appUrl));
//      }catch(Exception re) {
//        return new ResponseEntity<>(new SimpleResponseWrapper(420, "timeout connection problem"), HttpStatus.REQUEST_TIMEOUT);
//      }
        return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.CREATED);
      case "unverified number, can't send otp":
        return new ResponseEntity<>(new SimpleResponseWrapper(500, responseMQ), HttpStatus.INTERNAL_SERVER_ERROR);
      case "bad credentials":
        return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
      default:
        return new ResponseEntity<>(new SimpleResponseWrapper(401, responseMQ), HttpStatus.UNAUTHORIZED);
    }
  }

  @RequestMapping(value = "/api/login", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
    //validate password
    if (!validation.password(loginRequest.getPassword())){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
    }

    //validate email & convert phone & validate phone
    if (!validation.email(loginRequest.getEmailOrPhone())){
      loginRequest.setEmailOrPhone(validation.convertPhone(loginRequest.getEmailOrPhone()));
      if (!validation.phone(loginRequest.getEmailOrPhone())) {
        return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
      }
    }

    //send and receive MQ
    RPCClient rpcClient = new RPCClient("login");
    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(loginRequest));

    //translate MQ response
    switch (responseMQ) {
      case "bad credentials":
        return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
      case "inactive account, otp sent":
        return new ResponseEntity<>(new SimpleResponseWrapper(203, responseMQ), HttpStatus.NON_AUTHORITATIVE_INFORMATION);
      case "unverified number, can't send otp":
        return new ResponseEntity<>(new SimpleResponseWrapper(500, responseMQ), HttpStatus.INTERNAL_SERVER_ERROR);
      default:
        UserWithToken userWithToken = objectMapper.readValue(responseMQ, UserWithToken.class);
        return new ResponseEntity<>(new ResponseWrapper(202, "accepted", userWithToken), HttpStatus.ACCEPTED);
    }
  }
}