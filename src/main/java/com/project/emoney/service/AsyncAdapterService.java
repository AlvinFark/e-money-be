package com.project.emoney.service;

import com.project.emoney.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncAdapterService {

  @Autowired
  private OTPService otpService;

  @Autowired
  private EmailTokenService emailTokenService;

  private static final Logger log = LoggerFactory.getLogger(AsyncAdapterService.class);

  @Async("asyncExecutor")
  public CompletableFuture<String> sendEmail(User user) {
    //generate and save to db
    log.info("sending email started");
    String result = emailTokenService.sendEmail(user);
    log.info("sending email "+result);
    return CompletableFuture.completedFuture(result);
  }

  @Async("asyncExecutor")
  public CompletableFuture<String> sendOtp(String phone) {
    log.info("sending otp started");
    String result = otpService.sendOtp(phone);
    log.info("sending otp "+result);
    return CompletableFuture.completedFuture(result);
  }

}
