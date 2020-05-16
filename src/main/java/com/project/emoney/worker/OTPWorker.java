package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.OTP;
import com.project.emoney.entity.User;
import com.project.emoney.payload.dto.UserWithToken;
import com.project.emoney.payload.request.OTPRequest;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.service.AsyncAdapterService;
import com.project.emoney.service.OTPService;
import com.project.emoney.service.UserService;
import com.project.emoney.utils.GlobalVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class OTPWorker {

  @Autowired
  private UserService userService;

  @Autowired
  private OTPService otpService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private AsyncAdapterService asyncAdapterService;

  final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(AuthWorker.class);

  public String send(String message) throws JsonProcessingException {
    OTPRequest otpRequest = objectMapper.readValue(message, OTPRequest.class);
    log.info("[otp]  Receive otp verification request for email or phone: " + otpRequest.getEmailOrPhone());

    try {
      //get user details
      CompletableFuture<User> userCompletableFuture = asyncAdapterService.getUserByEmailOrPhone(otpRequest.getEmailOrPhone());
      CompletableFuture<UserDetails> userDetailsCompletableFuture = asyncAdapterService.loadUserDetailsByUsername(otpRequest.getEmailOrPhone());
      User user = userCompletableFuture.get();
      //if active reject request
      if (user.isActive()) {
        return "account already active";
      }
      final UserDetails userDetails = userDetailsCompletableFuture.get();
      CompletableFuture.allOf(userCompletableFuture,userDetailsCompletableFuture);
      //check master key, master key requested by FE QA for automation
      if (otpRequest.getCode().equals("6666")) {
        //set active
        userService.setActiveByEmail(user.getEmail());
        return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
      }
      //check whether otp didn't exist on db (get the latest if duplicate) and whether exist but not for this user
      OTP otp = otpService.getByCodeOrderByTimeDesc(otpRequest.getCode());
      if (otp == null || (!otp.getEmailOrPhone().equals(user.getEmail())&&!otp.getEmailOrPhone().equals(user.getPhone()))) {
        return "invalid code";
      }
      //check whether already expired, expiration time is set in global variable
      if (otp.getTime().plusMinutes(GlobalVariable.OTP_LIFETIME_MINUTES).isBefore(LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_APP_HOURS))) {
        return "code expired";
      }
      //return dan set active
      userService.setActiveByEmail(user.getEmail());
      return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
    } catch (UsernameNotFoundException | InterruptedException | ExecutionException e) {
      return "user not found";
    }
  }
}
