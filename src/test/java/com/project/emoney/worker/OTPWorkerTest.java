package com.project.emoney.worker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.OTP;
import com.project.emoney.entity.User;
import com.project.emoney.payload.request.OTPRequest;
import com.project.emoney.service.AsyncServiceAdapter;
import com.project.emoney.service.OTPService;
import com.project.emoney.utils.GlobalVariable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OTPWorkerTest {

  @Mock
  OTPService otpService;

  @Mock
  AsyncServiceAdapter asyncServiceAdapter;

  @InjectMocks
  OTPWorker otpWorker;

  ObjectMapper objectMapper = new ObjectMapper();
  private static final String code = "9999";
  private static final String phone = "628595839538";

  public User getUser(){
    User user = new  User();
    user.setId(1);
    user.setName("Dwight Schrute");
    user.setEmail("dwight@dundermifflin.co");
    user.setPhone("628595839538");
    user.setPassword("password");
    user.setBalance(0);
    user.setActive(false);
    return user;
  }

  public OTPRequest getOtpRequest(){
    return new OTPRequest(phone,code);
  }

  public OTP getOtp(){
    return new OTP(1,phone,code, LocalDateTime.now());
  }

  @Test
  public void otpAlreadyActiveTest() throws JsonProcessingException {
    User user = getUser();
    user.setActive(true);
    final OTPRequest otpRequest = getOtpRequest();
    when(asyncServiceAdapter.getUserByEmailOrPhone(phone)).thenReturn(CompletableFuture.completedFuture(user));
    when(asyncServiceAdapter.loadUserDetailsByUsername(phone)).thenReturn(CompletableFuture.completedFuture(null));
    assert otpWorker.send(objectMapper.writeValueAsString(otpRequest)).equals("account already active");
  }

  @Test
  public void invalidOtpCode() throws JsonProcessingException {
    User user = getUser();
    final OTPRequest otpRequest = getOtpRequest();
    otpRequest.setCode("0000");
    when(asyncServiceAdapter.getUserByEmailOrPhone(phone)).thenReturn(CompletableFuture.completedFuture(user));
    when(asyncServiceAdapter.loadUserDetailsByUsername(phone)).thenReturn(CompletableFuture.completedFuture(null));
    assert otpWorker.send(objectMapper.writeValueAsString(otpRequest)).equals("invalid code");
  }
}
