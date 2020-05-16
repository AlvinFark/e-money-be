package com.project.emoney.service;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.entity.User;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.security.JwtUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncAdapterService {

  @Autowired
  private OTPService otpService;

  @Autowired
  private EmailTokenService emailTokenService;

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUserDetailsService userDetailsService;

  @Autowired
  private TopUpOptionService topUpOptionService;

  @Autowired
  private TransactionService transactionService;

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

  @Async("asyncExecutor")
  public CompletableFuture<User> getUserByEmailOrPhone(String emailOrPhone) {
    log.info("getting user by email or phone started");
    User user = userService.getByEmailOrPhone(emailOrPhone);
    log.info("getting user by email or phone success");
    return CompletableFuture.completedFuture(user);
  }

  @Async("asyncExecutor")
  public CompletableFuture<User> getUserByEmail(String email) {
    log.info("getting user by email started");
    User user = userService.getByEmail(email);
    log.info("getting user by email success");
    return CompletableFuture.completedFuture(user);
  }

  @Async("asyncExecutor")
  public CompletableFuture<UserDetails> loadUserDetailsByUsername(String username) {
    log.info("loading user details by username started");
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    log.info("loading user details by username success");
    return CompletableFuture.completedFuture(userDetails);
  }

  @Async("asyncExecutor")
  public CompletableFuture<TopUpOption> getTopUpOptionById(long id){
    log.info("getting top up option started");
    TopUpOption topUpOption = topUpOptionService.getById(id);
    log.info("getting top up option success");
    return CompletableFuture.completedFuture(topUpOption);
  }

  @Async("asyncExecutor")
  public CompletableFuture<Void> saveTransaction(TransactionRequest transactionRequest, User user, TopUpOption topUpOption, Status status) {
    log.info("saving transaction started");
    transactionService.insertByTransactionRequestAndUserAndTopUpOptionAndStatus(transactionRequest,user,topUpOption,status);
    log.info("saving transaction success");
    return CompletableFuture.completedFuture(null);
  }

  @Async("asyncExecutor")
  public CompletableFuture<Void> updateUserBalance(User userRequest) {
    log.info("updating user balance started");
    userService.updateBalance(userRequest);
    log.info("updating user balance success");
    return CompletableFuture.completedFuture(null);
  }
}
