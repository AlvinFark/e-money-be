package com.project.emoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.ResponseWrapper;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.payload.TransactionRequest;
import com.project.emoney.payload.UserWrapper;
import com.project.emoney.security.CurrentUser;
import com.project.emoney.utils.RPCClient;
import com.project.emoney.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  Validation validation;

  @PostMapping
  public ResponseEntity<?> createTransaction(
      @CurrentUser org.springframework.security.core.userdetails.User userDetails,
      @RequestBody Map<String,Object> request) throws Exception {

    //validate method transaksi apa sesuai enum
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

    transactionRequest.setEmail(userDetails.getUsername());
    RPCClient rpcClient = new RPCClient("transaction");
    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(transactionRequest));
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
}
