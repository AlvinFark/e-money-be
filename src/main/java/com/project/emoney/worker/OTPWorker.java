package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.OTP;
import com.project.emoney.entity.User;
import com.project.emoney.mybatis.OTPService;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.OTPRequest;
import com.project.emoney.payload.UserWithToken;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.security.JwtUserDetailsService;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class OTPWorker {

  @Autowired
  private JwtUserDetailsService userDetailsService;

  @Autowired
  private UserService userService;

  @Autowired
  private OTPService otpService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  ObjectMapper objectMapper = new ObjectMapper();
  private static Logger log = LoggerFactory.getLogger(AuthWorker.class);

  @Async("workerExecutor")
  public String send(String message) throws JsonProcessingException {
    OTPRequest otpRequest = objectMapper.readValue(message, OTPRequest.class);
    log.info("[otp]  Receive otp verification request for email or phone: " + otpRequest.getEmailOrPhone());

    try {
      final UserDetails userDetails = userDetailsService.loadUserByUsername(otpRequest.getEmailOrPhone());
      User user = userService.getUserByEmail(userDetails.getUsername());
      if (user.isActive()) {
        return "account already active";
      }
      //cek master key, master key untuk kebutuhan fe qa
      if (otpRequest.getCode().equals("6666")) {
        return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
      }
      OTP otp = otpService.getByCodeOrderByTimeDesc(otpRequest.getCode());
      //cek jika otp null atau otp bukan punya user tersebut
      if (otp == null || (!(otp.getEmailOrPhone().equals(user.getEmail()) || otp.getEmailOrPhone().equals(user.getPassword())))) {
        return "invalid code";
      }
      //bukan master key dan lebih dari batas waktu
      if (otp.getTime().plusMinutes(2).isAfter(LocalDateTime.now())) {
        System.out.println(otp.getTime().plusMinutes(2));
        System.out.println(LocalDateTime.now());
        return "code expired";
      }
      return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
    } catch (UsernameNotFoundException e) {
      return "user not found";
    }
  }
}
