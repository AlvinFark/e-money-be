package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.async.OTPAsync;
import com.project.emoney.entity.OTP;
import com.project.emoney.entity.User;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.service.OTPService;
import com.project.emoney.service.UserService;
import com.project.emoney.payload.request.LoginRequest;
import com.project.emoney.payload.dto.UserWithToken;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.security.JwtUserDetailsService;
import com.project.emoney.utils.Generator;
import com.project.emoney.utils.GlobalVariable;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.SQLSyntaxErrorException;
import java.time.LocalDateTime;
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
  private OTPAsync otpAsync;

  ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(AuthWorker.class);

  public String register(String message) throws JsonProcessingException, ExecutionException, InterruptedException {
    User userRequest = objectMapper.readValue(message, User.class);
    log.info("[register]  Receive register request for email: " + userRequest.getEmail());
    log.info("[register]  Receive register request for phone: " + userRequest.getPhone());

    User user;
    try {
      //save user
      user = userService.insert(userRequest);
    } catch (Exception e) {
      e.printStackTrace();
      return "too many connections";
    }
    CompletableFuture<String> statusOtp = otpAsync.sendEmail(user);
    CompletableFuture<String> statusEmail = otpAsync.sendOtp(user.getPhone());
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

  public String login(String message) throws JsonProcessingException, ExecutionException, InterruptedException {
    LoginRequest loginRequest = objectMapper.readValue(message, LoginRequest.class);
    log.info("[login]  Receive login request for email or phone: " + loginRequest.getEmailOrPhone());
    try {
      //check user credentials
      authenticate(loginRequest.getEmailOrPhone(), loginRequest.getPassword());
    } catch (BadCredentialsException e) {
      return "bad credentials";
    } catch (Exception e) {
      e.printStackTrace();
      return "too many connections";
    }
    final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmailOrPhone());
    User user = userService.getUserByEmail(userDetails.getUsername());
    //if already active, then return token
    if (user.isActive()) {
      return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
    }
    //if inactive, send otp
    CompletableFuture<String> statusOtp = otpAsync.sendOtp(user.getPhone());
    CompletableFuture.allOf(statusOtp).join();
    if (statusOtp.get().equals("success")){
      return "inactive account, otp sent";
    }
    return "unverified number, can't send otp";
  }

  private void authenticate(String username, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
  }
}
