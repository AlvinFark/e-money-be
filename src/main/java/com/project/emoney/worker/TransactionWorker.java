package com.project.emoney.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.entity.TransactionMethod;
import com.project.emoney.entity.User;
import com.project.emoney.mybatis.TopUpOptionService;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.TopUpRequest;
import com.project.emoney.payload.TransactionRequest;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class TransactionWorker {

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  UserService userService;

  @Autowired
  TopUpOptionService topUpOptionService;

  private final OkHttpClient httpClient = new OkHttpClient();
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  public String createTransaction(String message) throws Exception {
    //init objects
    TransactionRequest transactionRequest = objectMapper.readValue(message, TransactionRequest.class);
    User user = userService.getUserByEmail(transactionRequest.getEmail());
    TopUpOption topUpOption = topUpOptionService.getById(transactionRequest.getIdTopUpOption());

    if (transactionRequest.getMethod()==TransactionMethod.WALLET){
      //cek saldo
      if (user.getBalance()<=0||user.getBalance()<topUpOption.getValue()+topUpOption.getFee()){
        return "not enough balance";
      }
      //connect ke dummy server
      TopUpRequest topUpRequest = new TopUpRequest(transactionRequest.getCardNumber(),topUpOption.getValue());
      RequestBody body = RequestBody.create(objectMapper.writeValueAsString(topUpRequest), JSON);
      Request request = new Request.Builder()
          .url("https://dummy-emoney.herokuapp.com/api/card/top-up")
          .post(body)
          .build();
      try (Response response = httpClient.newCall(request).execute()) {
        if (!response.isSuccessful()) {
          return response.message();
        } else {
          user.setBalance(user.getBalance()-topUpOption.getValue()-topUpOption.getFee());
          userService.updateBalance(user);
          return objectMapper.writeValueAsString(user);
        }
      }
    }
    return transactionRequest.toString();
  }
}
