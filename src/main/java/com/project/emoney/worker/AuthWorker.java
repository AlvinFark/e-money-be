package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.OTP;
import com.project.emoney.entity.User;
import com.project.emoney.mybatis.OTPService;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.LoginRequest;
import com.project.emoney.payload.UserWithToken;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.security.JwtUserDetailsService;
import com.project.emoney.utils.Generator;
import com.rabbitmq.client.*;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class AuthWorker {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUserDetailsService userDetailsService;

  @Autowired
  private UserService userService;

  @Autowired
  private OTPService otpService;

  @Autowired
  private Generator generator;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  private String myTwilioPhoneNumber = System.getenv("phoneNumber");

  private String twilioAccountSid = System.getenv("twilioAccountSid");

  private String twilioAuthToken = System.getenv("twilioAuthToken");

  ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(AuthWorker.class);

  public String register(String message) throws JsonProcessingException {
    User user = objectMapper.readValue(message, User.class);
    log.info("[register]  Receive register request for email: " + user.getEmail());
    log.info("[register]  Receive register request for phone: " + user.getPhone());
    try {
      userService.insert(user);
      return sendOtp(user.getPhone());
    } catch (Exception e) {
      return "bad credentials";
    }
  }

  public String login(String message) throws JsonProcessingException {
      LoginRequest loginRequest = objectMapper.readValue(message, LoginRequest.class);
      log.info("[login]  Receive login request for email or phone: " + loginRequest.getEmailOrPhone());
      try {
        authenticate(loginRequest.getEmailOrPhone(), loginRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmailOrPhone());
        User user = userService.getUserByEmail(userDetails.getUsername());
        if (user.isActive()){
          return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
        }
        return sendOtp(user.getPhone());
      } catch (Exception e) {
        return "bad credentials";
      }
  }

  private String sendOtp(String phone) {
    try {
      OTP otp = new OTP();
      otp.setEmailOrPhone(phone);
      otp.setCode(generator.generateOtp());
      otp.setTime(LocalDateTime.now().plusHours(7));
      Twilio.init(twilioAccountSid, twilioAuthToken);
      Message.creator(
          new PhoneNumber("+"+phone),
          new PhoneNumber(myTwilioPhoneNumber),
          "Kode OTP: "+otp.getCode()).create();
      otpService.create(otp);
      return "inactive account, otp sent";
    } catch (ApiException e) {
      return "unverified number, can't send otp";
    }
  }

  private void authenticate(String username, String password) throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID_CREDENTIALS", e);
    }
  }
}
