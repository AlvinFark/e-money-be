package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.User;
import com.project.emoney.payload.dto.TransactionDTO;
import com.project.emoney.payload.request.TopUpRequest;
import com.project.emoney.service.TransactionService;
import com.project.emoney.service.UserService;
import com.project.emoney.utils.GlobalVariable;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentWorker {

  @Autowired
  UserService userService;

  @Autowired
  TransactionService transactionService;

  private final OkHttpClient httpClient = new OkHttpClient();
  ObjectMapper objectMapper = new ObjectMapper();
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  public String getMerchantTransaction(String message) throws JsonProcessingException {
    if (!message.startsWith("8000")){
      return "invalid virtual account number";
    }

    User user = userService.getByEmailOrPhone(message.substring(4));
    if (user==null){
      return "user not found";
    }

    List<Transaction> list = transactionService.getInProgressAndMerchantByUserId(user.getId());
    List<TransactionDTO> transactionList = new ArrayList<TransactionDTO>();

    for (Transaction transaction: list) {
      LocalDateTime expiredTime = transaction.getExpiry();
      LocalDateTime localDateTime = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_APP_HOURS);
      int compareValue = expiredTime.compareTo(localDateTime);
      if (compareValue > 0) {
        //Add to list in-progress
        transactionList.add(new TransactionDTO(transaction));
      } else {
        //Move from in-progress
        transactionService.updateStatusById(transaction.getId(), Status.FAILED);
      }
    }
    return objectMapper.writeValueAsString(transactionList);
  }

  public String confirm(String message) throws JsonProcessingException {
    Transaction transaction = transactionService.getById(Long.parseLong(message));
    if (transaction==null){
      return "transaction not found";
    }
    if (transaction.getStatus()==Status.FAILED||transaction.getStatus()==Status.CANCELLED||transaction.getStatus()==Status.COMPLETED){
      return "can't confirm completed transaction";
    }
    //send request to dummy server
    TopUpRequest topUpRequest = new TopUpRequest(transaction.getCardNumber(),transaction.getValue());
    RequestBody body = RequestBody.create(objectMapper.writeValueAsString(topUpRequest), JSON);
    Request request = new Request.Builder()
        .url("https://dummy-emoney.herokuapp.com/api/card/top-up")
        .post(body)
        .build();
    try (Response response = httpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        return response.message();
      } else {
        transactionService.updateStatusById(transaction.getId(),Status.COMPLETED);
        return "success";
      }
    } catch (Exception e) {
      return "can't reach 3rd party server, try again";
    }
  }
}
