package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.*;
import com.project.emoney.service.TopUpOptionService;
import com.project.emoney.service.TransactionService;
import com.project.emoney.service.UserService;
import com.project.emoney.payload.request.TopUpRequest;
import com.project.emoney.payload.dto.TransactionDTO;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.utils.GlobalVariable;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
      //chek balance
      if (user.getBalance()<=0||user.getBalance()<topUpOption.getValue()+topUpOption.getFee()){
        return "not enough balance";
      }
      //send request to dummy server
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
    Transaction transaction = new Transaction( user.getId(), transactionRequest.getCardNumber(), topUpOption.getValue(),
        topUpOption.getFee(), status, transactionRequest.getMethod(),LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_HOURS),
        LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_HOURS+GlobalVariable.TRANSACTION_LIFETIME_HOURS));
    transactionService.insert(transaction);
  }

  public String transactionInProgress(String message) throws JsonProcessingException {
    User user = userService.getUserByEmail(message);

    List<Transaction> list = transactionService.getInProgressByUserId(user.getId());

    //create new list for transaction in-progress
    List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();

    for (Transaction transaction: list) {
        LocalDateTime expiredTime = transaction.getExpiry();
        LocalDateTime localDateTime = LocalDateTime.now();
        int compareValue = expiredTime.compareTo(localDateTime);
        if(compareValue > 0) {
          //Add to list in-progress
          transactionList.add(new TransactionDTO(transaction));
        } else {
          //Move from in-progress
          transactionService.updateStatusById(transaction.getId(), Status.FAILED);
        }
    }
    return objectMapper.writeValueAsString(transactionList);
  }

  public String transactionCompleted(String message) throws JsonProcessingException {
    String email = message;
    User user = userService.getUserByEmail(email);

    List<Transaction> list = transactionService.getAllByUserId(user.getId());

    //create new list for transaction completed
    List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();

    for (Transaction transaction: list) {
      //if in progress
      if (transaction.getStatus()==Status.IN_PROGRESS) {
        //cek whether expired
        LocalDateTime expiredTime = transaction.getExpiry();
        LocalDateTime localDateTime = LocalDateTime.now();
        int compareValue = expiredTime.compareTo(localDateTime);
        if (compareValue < 0) {
          //set failed if expired and add to list
          transaction.setStatus(Status.FAILED);
          transactionService.updateStatusById(transaction.getId(), Status.FAILED);
          transactionList.add(new TransactionDTO(transaction));
        }
      } else {
        //if not in progress, add to list
        transactionList.add(new TransactionDTO(transaction));
      }
    }
    return objectMapper.writeValueAsString(transactionList);
  }


}
