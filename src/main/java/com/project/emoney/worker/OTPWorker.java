package com.project.emoney.worker;

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
  public void send() {
    final String QUEUE_NAME = "otp";

    final URI rabbitMqUrl;
    try {
      rabbitMqUrl = new URI(System.getenv("CLOUDAMQP_URL"));
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername(rabbitMqUrl.getUserInfo().split(":")[0]);
    factory.setPassword(rabbitMqUrl.getUserInfo().split(":")[1]);
    factory.setHost(rabbitMqUrl.getHost());
    factory.setPort(rabbitMqUrl.getPort());
    factory.setVirtualHost(rabbitMqUrl.getPath().substring(1));

    try (Connection connection = factory.newConnection();
         Channel channel = connection.createChannel()) {
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queuePurge(QUEUE_NAME);

      channel.basicQos(1);

      log.info("[otp]  Awaiting otp verification requests");

      Object monitor = new Object();
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
            .Builder()
            .correlationId(delivery.getProperties().getCorrelationId())
            .build();

        String response = "";
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        OTPRequest otpRequest = objectMapper.readValue(message, OTPRequest.class);
        log.info("[otp]  Receive otp verification request for email or phone: " + otpRequest.getEmailOrPhone());

        try {
          final UserDetails userDetails = userDetailsService.loadUserByUsername(otpRequest.getEmailOrPhone());
          User user = userService.getUserByEmail(userDetails.getUsername());
          if (user.isActive()){
            response = "account already active";
          //cek master key, master key untuk kebutuhan fe qa
          } else if (otpRequest.getCode().equals("6666")) {
            response = objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
          } else {
            OTP otp = otpService.getByCodeOrderByTimeDesc(otpRequest.getCode());
            //cek jika otp null atau otp bukan punya user tersebut
            if (otp==null||(!(otp.getEmailOrPhone().equals(user.getEmail())||otp.getEmailOrPhone().equals(user.getPassword())))) {
              response = "invalid code";
            //bukan master key dan lebih dari batas waktu
            } else if (otp.getTime().plusMinutes(2).isAfter(LocalDateTime.now())) {
              System.out.println(otp.getTime().plusMinutes(2));
              System.out.println(LocalDateTime.now());
              response = "code expired";
            } else {
              response = objectMapper.writeValueAsString(new UserWithToken(user, jwtTokenUtil.generateToken(userDetails)));
              System.out.println(otp.getTime().plusMinutes(2));
              System.out.println(LocalDateTime.now());
            }
          }
        } catch (UsernameNotFoundException e){
          response = "user not found";
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
}
