package com.project.emoney.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.OTP;
import com.project.emoney.entity.User;
import com.project.emoney.mybatis.OTPService;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.LoginRequest;
import com.project.emoney.security.JwtUserDetailsService;
import com.project.emoney.utils.Generator;
import com.rabbitmq.client.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

  @Value("${phoneNumber}")
  private String myTwilioPhoneNumber;

  @Value("${twilioAccountSid}")
  private String twilioAccountSid;

  @Value("${twilioAuthToken}")
  private String twilioAuthToken;

  ObjectMapper objectMapper = new ObjectMapper();
  private static Logger log = LoggerFactory.getLogger(AuthWorker.class);

  @Async("workerExecutor")
  public void login() {
    final String QUEUE_NAME = "login";
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queuePurge(QUEUE_NAME);

      channel.basicQos(1);

      log.info("[login]  Awaiting login requests");

      Object monitor = new Object();
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
            .Builder()
            .correlationId(delivery.getProperties().getCorrelationId())
            .build();

        String response = "";

        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        LoginRequest loginRequest = objectMapper.readValue(message, LoginRequest.class);
        log.info("[login]  Receive login request for email or phone: " + loginRequest.getEmailOrPhone());

        try {
          authenticate(loginRequest.getEmailOrPhone(), loginRequest.getPassword());
          final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmailOrPhone());
          User user = userService.getUserByEmail(userDetails.getUsername());
          if (user.isActive()){
            OTP otp = new OTP();
            otp.setEmailOrPhone(loginRequest.getEmailOrPhone());
            otp.setCode(generator.generateOtp());
            otp.setTime(LocalDateTime.now());
            otpService.create(otp);
            Twilio.init(twilioAccountSid, twilioAuthToken);
            Message.creator(
                new PhoneNumber("+"+user.getPhone()),
                new PhoneNumber(myTwilioPhoneNumber),
                "Kode OTP: "+otp.getCode()).create();
//            final String token = jwtTokenUtil.generateToken(userDetails);
            response = "success, otp sent";
          } else {
            response = "inactive account, check your email or resend email verification";
          }
        } catch (Exception e) {
          response = "bad credentials";
        }
        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes(StandardCharsets.UTF_8));
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        synchronized (monitor) {
          monitor.notify();
        }
      };

      channel.basicConsume(QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
      while (true) {
        synchronized (monitor) {
          try {
            monitor.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Async("workerExecutor")
  public void register() {
    final String QUEUE_NAME = "register";
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");

    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queuePurge(QUEUE_NAME);

      channel.basicQos(1);

      log.info("[register]  Awaiting register requests");

      Object monitor = new Object();
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                .Builder()
                .correlationId(delivery.getProperties().getCorrelationId())
                .build();

        String response = "";

        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        User user = objectMapper.readValue(message, User.class);
        log.info("[register]  Receive register request for email: " + user.getEmail());
        log.info("[register]  Receive register request for phone: " + user.getPhone());

        try {
          userService.insert(user);
          OTP otp = new OTP();
          otp.setEmailOrPhone(user.getPhone());
          otp.setCode(generator.generateOtp());
          otp.setTime(LocalDateTime.now());
          otpService.create(otp);
          Twilio.init(twilioAccountSid, twilioAuthToken);
          Message.creator(
                  new PhoneNumber("+"+user.getPhone()),
                  new PhoneNumber(myTwilioPhoneNumber),
                  "Kode OTP: "+otp.getCode()).create();
//            final String token = jwtTokenUtil.generateToken(userDetails);
          response = "success, otp sent";
        } catch (Exception e) {
          response = "bad credentials";
        }
        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes(StandardCharsets.UTF_8));
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        synchronized (monitor) {
          monitor.notify();
        }
      };

      channel.basicConsume(QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
      while (true) {
        synchronized (monitor) {
          try {
            monitor.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
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
