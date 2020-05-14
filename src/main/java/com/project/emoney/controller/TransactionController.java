package com.project.emoney.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.response.ResponseWrapper;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import com.project.emoney.payload.dto.TransactionDTO;
import com.project.emoney.payload.dto.UserWrapper;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.security.CurrentUser;
import com.project.emoney.utils.RPCClient;
import com.project.emoney.utils.Validation;
import com.project.emoney.worker.TransactionWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  Validation validation;

  @Autowired
  TransactionWorker transactionWorker;

  @PostMapping
  public ResponseEntity<?> createTransaction(
      @CurrentUser org.springframework.security.core.userdetails.User userDetails,
      @RequestBody Map<String,Object> request) throws Exception {

    //validate transaction method, only listed at enum allowed
    TransactionRequest transactionRequest;
    try {
      transactionRequest = objectMapper.convertValue(request, TransactionRequest.class);
    } catch (Exception e) {
      return new ResponseEntity<>(new SimpleResponseWrapper(422, "bad transaction method"), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    //validate invalid card number format
    if (!validation.card(transactionRequest.getCardNumber())){
      return new ResponseEntity<>(new ResponseWrapper(401, "success", transactionRequest), HttpStatus.OK);
    }

    //set email by extracting from token
    transactionRequest.setEmail(userDetails.getUsername());

    //send and receive from MQ
//    RPCClient rpcClient = new RPCClient("transaction");
//    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(transactionRequest));
    String responseMQ = transactionWorker.createTransaction(objectMapper.writeValueAsString(transactionRequest));

    //translate MQ response
    try {
      User user = objectMapper.readValue(responseMQ, User.class);
      return new ResponseEntity<>(new ResponseWrapper(201, "success", new UserWrapper(user)), HttpStatus.CREATED);
    } catch (Exception e) {
      if (responseMQ.equals("success")) {
        return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.CREATED);
      }
      if (responseMQ.equals("not enough balance")){
        return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.valueOf(400));
      }
      return new ResponseEntity<>(new SimpleResponseWrapper(401, responseMQ), HttpStatus.UNAUTHORIZED);
    }
  }

  //get all in progress transaction from current user
  @GetMapping("/in-progress")
  public ResponseEntity<?> getInProgress(@CurrentUser org.springframework.security.core.userdetails.User userDetails) throws Exception{
//    RPCClient rpcClient = new RPCClient("in-progress");
//    String responseMQ = rpcClient.call(userDetails.getUsername());
    String responseMQ = transactionWorker.transactionInProgress(userDetails.getUsername());

    List<TransactionDTO> list = objectMapper.readValue(responseMQ, new TypeReference<List<TransactionDTO>>() {});
    return new ResponseEntity<>(new ResponseWrapper(200, "success", list), HttpStatus.OK);
  }

  //get all completed transaction from current user
  @GetMapping("/completed")
  public ResponseEntity<?> getCompleted(@CurrentUser org.springframework.security.core.userdetails.User userDetails) throws Exception{
//    RPCClient rpcClient = new RPCClient("completed");
//    String responseMQ = rpcClient.call(userDetails.getUsername());
    String responseMQ = transactionWorker.transactionCompleted(userDetails.getUsername());

    List<TransactionDTO> list = objectMapper.readValue(responseMQ, new TypeReference<List<TransactionDTO>>() {});
    return new ResponseEntity<>(new ResponseWrapper(200, "success", list), HttpStatus.OK);
  }
}
