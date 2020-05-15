package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.*;
import com.project.emoney.payload.request.CancelRequest;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import com.project.emoney.service.AsyncAdapterService;
import com.project.emoney.service.TopUpOptionService;
import com.project.emoney.service.TransactionService;
import com.project.emoney.service.UserService;
import com.project.emoney.payload.request.TopUpRequest;
import com.project.emoney.payload.dto.TransactionDTO;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.utils.GlobalVariable;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class TransactionWorker {

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  UserService userService;

  @Autowired
  TopUpOptionService topUpOptionService;

  @Autowired
  TransactionService transactionService;

  @Autowired
  AsyncAdapterService asyncAdapterService;

  private final OkHttpClient httpClient = new OkHttpClient();
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  public String createTransaction(String message) throws Exception {
    //init objects
    TransactionRequest transactionRequest = objectMapper.readValue(message, TransactionRequest.class);
    CompletableFuture<User> userCompletableFuture = asyncAdapterService.getUserByEmail(transactionRequest.getEmail());
    CompletableFuture<TopUpOption> topUpOptionCompletableFuture = asyncAdapterService.getTopUpOptionById(transactionRequest.getIdTopUpOption());
    User user = userCompletableFuture.get();
    TopUpOption topUpOption = topUpOptionCompletableFuture.get();

    CompletableFuture.allOf(userCompletableFuture,topUpOptionCompletableFuture);
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
          user.setBalance(user.getBalance()-topUpOption.getValue()-topUpOption.getFee());
          CompletableFuture<Void> voidCompletableFutureUser = asyncAdapterService.updateUserBalance(user);
          CompletableFuture<Void> voidCompletableFutureTransaction = asyncAdapterService.saveTransaction(transactionRequest, user, topUpOption, Status.COMPLETED);
          CompletableFuture.allOf(voidCompletableFutureTransaction,voidCompletableFutureUser);
          return objectMapper.writeValueAsString(user);
        }
      }
    }
    transactionService.saveTransaction(transactionRequest, user, topUpOption, Status.IN_PROGRESS);
    return "success";
  }

  public String transactionInProgress(String message) throws JsonProcessingException {
    User user = userService.getUserByEmail(message);

    List<Transaction> list = transactionService.getInProgressByUserId(user.getId());

    //create new list for transaction in-progress
    List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();

    for (Transaction transaction: list) {
        LocalDateTime expiredTime = transaction.getExpiry();
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_APP_HOURS);
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

  public String transactionCompleted(String email) throws JsonProcessingException {
    User user = userService.getUserByEmail(email);

    List<Transaction> list = transactionService.getAllByUserId(user.getId());

    //create new list for transaction completed
    List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();

    for (Transaction transaction: list) {
      //if in progress
      if (transaction.getStatus()==Status.IN_PROGRESS) {
        //cek whether expired
        LocalDateTime expiredTime = transaction.getExpiry();
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_APP_HOURS);
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

  public String cancel(String message) throws JsonProcessingException {
    CancelRequest cancelRequest = objectMapper.readValue(message, CancelRequest.class);
    User user = userService.getUserByEmail(cancelRequest.getUserEmail());

    try {
      Transaction transaction = transactionService.getById(cancelRequest.getId());

      //validation if user cancel for other's transaction
      if (transaction.getUserId() != user.getId()) {
        return "transaction belong to another user";
      }

      //can only cancel in progress transaction
      if (transaction.getStatus() != Status.IN_PROGRESS) {
        return "can't cancel completed transaction";
      }

      transactionService.updateStatusById(transaction.getId(), Status.CANCELLED);
      return "success";
    } catch (Exception e) {
      return "transaction not found";
    }
  }
}
