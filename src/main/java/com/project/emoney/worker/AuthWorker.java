package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.dto.UserWithToken;
import com.project.emoney.payload.request.LoginRequest;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.security.JwtUserDetailsService;
import com.project.emoney.service.AsyncAdapterService;
import com.project.emoney.service.OTPService;
import com.project.emoney.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AuthWorker {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUserDetailsService userDetailsService;

  @Autowired
  private UserService userService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private AsyncAdapterService asyncAdapterService;

  @Autowired
  private OTPService otpService;

  final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(AuthWorker.class);

  public String register(String message) throws JsonProcessingException, ExecutionException, InterruptedException {
    User userRequest = objectMapper.readValue(message, User.class);
    log.info("[register]  Receive register request for email: " + userRequest.getEmail() + " and phone: " + userRequest.getPhone());

    User user;
    //save user
    user = userService.insert(userRequest);
    CompletableFuture<String> statusOtp = asyncAdapterService.sendEmail(user);
    CompletableFuture<String> statusEmail = asyncAdapterService.sendOtp(user.getPhone());
    CompletableFuture.allOf(statusOtp,statusEmail).join();
    if (statusOtp.get().equals("success")&&statusEmail.get().equals("success")){
      return "created, check email or sms for activation";
    } else if (statusOtp.get().equals("success")){
      return "created, check sms for activation";
    } else if (statusEmail.get().equals("success")){
      return "created, check email for activation";
    } else {
      return "created, but failed to send sms and email, try again later";
    }
  }

  public String login(String message) throws JsonProcessingException {
    LoginRequest loginRequest = objectMapper.readValue(message, LoginRequest.class);
    log.info("[login]  Receive login request for email or phone: " + loginRequest.getEmailOrPhone());
    User user = userService.getByEmailOrPhone(loginRequest.getEmailOrPhone());
    if (user==null){
      return "email or phone not registered";
    }
    try {
      //check user credentials
      authenticate(loginRequest.getEmailOrPhone(), loginRequest.getPassword());
    } catch (BadCredentialsException e) {
      return "wrong password";
    }
    final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmailOrPhone());
    //if already active, then return token
    if (user.isActive()) {
      return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
    }
    //if inactive, send otp
    String statusOtp = otpService.sendOtp(user.getPhone());
    if (statusOtp.equals("success")){
      return "inactive account, otp sent";
    }
    return "unverified number, can't send otp";
  }

  private void authenticate(String username, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
  }
}
