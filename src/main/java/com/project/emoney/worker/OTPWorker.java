package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.OTP;
import com.project.emoney.entity.User;
import com.project.emoney.service.OTPService;
import com.project.emoney.service.UserService;
import com.project.emoney.payload.request.OTPRequest;
import com.project.emoney.payload.dto.UserWithToken;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.security.JwtUserDetailsService;
import com.project.emoney.utils.GlobalVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

  public String send(String message) throws JsonProcessingException {
    OTPRequest otpRequest = objectMapper.readValue(message, OTPRequest.class);
    log.info("[otp]  Receive otp verification request for email or phone: " + otpRequest.getEmailOrPhone());

    try {
      //get user details
      final UserDetails userDetails = userDetailsService.loadUserByUsername(otpRequest.getEmailOrPhone());
      User user = userService.getUserByEmail(userDetails.getUsername());
      //if active reject request
      if (user.isActive()) {
        return "account already active";
      }
      //check master key, master key requested by FE QA for automation
      if (otpRequest.getCode().equals("6666")) {
        //set active
        userService.setActive(user.getEmail());
        return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
      }
      //check whether otp didn't exist on db (get the latest if duplicate) and whether exist but not for this user
      OTP otp = otpService.getByCodeOrderByTimeDesc(otpRequest.getCode());
      if (otp == null || (!otp.getEmailOrPhone().equals(user.getEmail())&&!otp.getEmailOrPhone().equals(user.getPhone()))) {
        return "invalid code";
      }
      log.debug(String.valueOf(otp.getTime().plusMinutes(GlobalVariable.OTP_LIFETIME_MINUTES)));
      log.debug(String.valueOf(LocalDateTime.now()));
      //check whether already expired, expiration time is set in global variable
      if (otp.getTime().plusMinutes(GlobalVariable.OTP_LIFETIME_MINUTES).isBefore(LocalDateTime.now())) {
        return "code expired";
      }
      //return dan set active
      userService.setActive(user.getEmail());
      return objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
    } catch (UsernameNotFoundException e) {
      return "user not found";
    }
  }
}
