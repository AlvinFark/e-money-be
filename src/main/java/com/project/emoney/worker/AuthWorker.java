package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private EmailTokenService emailTokenService;

  @Autowired
  private Generator generator;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  JavaMailSender javaMailSender;

  private final String myTwilioPhoneNumber = System.getenv("T6phoneNumber");
  private final String twilioAccountSid = System.getenv("T6twilioAccountSid");
  private final String twilioAuthToken = System.getenv("T6twilioAuthToken");

  ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(AuthWorker.class);

  public String register(String message) throws JsonProcessingException {
    User userRequest = objectMapper.readValue(message, User.class);
    log.info("[register]  Receive register request for email: " + userRequest.getEmail());
    log.info("[register]  Receive register request for phone: " + userRequest.getPhone());
    try {
      //save user
      User user = userService.insert(userRequest);
      sendEmail(user);
    } catch (Exception e) {
      e.printStackTrace();
      return "too many connections";
    }
    return sendOtp(userRequest.getPhone());
  }

  public String login(String message) throws JsonProcessingException {
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
    return sendOtp(user.getPhone());
  }

  private void sendEmail(User user) throws MessagingException {
    //generate and save to db
    String token = generator.generateToken();
    emailTokenService.createVerificationToken(user,token);
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setSubject("Please confirm your new e-Money App account");
    helper.setTo(user.getEmail());
    helper.setText("<a href=\"https://be-emoney.herokuapp.com/api/verify/code?"+token+"\">Please click here to activate your account</a>", true);

    javaMailSender.send(message);
  }

  private String sendOtp(String phone) {
    try {
      //initialize otp details
      OTP otp = new OTP();
      otp.setEmailOrPhone(phone);
      otp.setCode(generator.generateOtp());
      //add 7 hours to calibrate it with server
      otp.setTime(LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS));
      Twilio.init(twilioAccountSid, twilioAuthToken);
      Message.creator(
          new PhoneNumber("+"+phone),
          new PhoneNumber(myTwilioPhoneNumber),
          "Kode OTP: "+otp.getCode()).create();
      otpService.create(otp);
      return "inactive account, otp sent";
    } catch (ApiException e) {
      //twilio free can only send to verified number
      e.printStackTrace();
      return "unverified number, can't send otp";
    }
  }

  private void authenticate(String username, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
  }
}
