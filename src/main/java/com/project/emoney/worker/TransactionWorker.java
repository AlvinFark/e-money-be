package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.*;
import com.project.emoney.mybatis.TopUpOptionService;
import com.project.emoney.mybatis.TransactionService;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.TopUpRequest;
import com.project.emoney.payload.TransactionRequest;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TransactionWorker {

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  UserService userService;

  @Autowired
  TopUpOptionService topUpOptionService;

  @Autowired
  TransactionService transactionService;

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
          saveTransaction(transactionRequest, user, topUpOption, Status.COMPLETED);
          user.setBalance(user.getBalance()-topUpOption.getValue()-topUpOption.getFee());
          userService.updateBalance(user);
          return objectMapper.writeValueAsString(user);
        }
      }
    }
    saveTransaction(transactionRequest, user, topUpOption, Status.IN_PROGRESS);
    return "success";
  }

  private void saveTransaction(TransactionRequest transactionRequest, User user, TopUpOption topUpOption, Status status) {
    Transaction transaction = new Transaction( user.getId(), transactionRequest.getCardNumber(), topUpOption.getValue(), topUpOption.getFee(),
        status, transactionRequest.getMethod(), LocalDateTime.now().plusHours(7), LocalDateTime.now().plusHours(31));
    transactionService.insert(transaction);
  }

  public String transactionInProgress(String message) throws JsonProcessingException {
    String email = message;
    User user = userService.getUserByEmail(email);

    List<Transaction> list = transactionService.getInProgress(user.getId());
    return objectMapper.writeValueAsString(list);
  }

  public String transactionCompleted(String message) throws JsonProcessingException {
    String email = message;
    User user = userService.getUserByEmail(email);

    List<Transaction> list = transactionService.getCompleted(user.getId());
    return objectMapper.writeValueAsString(list);
  }


}
