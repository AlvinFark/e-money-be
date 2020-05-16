package com.project.emoney.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.dto.TransactionDTO;
import com.project.emoney.payload.dto.UserWrapper;
import com.project.emoney.payload.request.CancelRequest;
import com.project.emoney.payload.request.HistoryRequest;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.payload.response.ResponseWrapper;
import com.project.emoney.payload.response.SimpleResponseWrapper;
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

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private Validation validation;

  @Autowired
  private TransactionWorker transactionWorker;

  @DeleteMapping("/{id}")
  public ResponseEntity<?> cancel(
      @CurrentUser org.springframework.security.core.userdetails.User userDetails,
      @PathVariable long id) throws Exception {
    CancelRequest cancelRequest = new CancelRequest(id, userDetails.getUsername());
//    RPCClient rpcClient = new RPCClient("cancelTransaction");
//    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(cancelRequest));
    String responseMQ = transactionWorker.cancel(objectMapper.writeValueAsString(cancelRequest));
    switch (responseMQ) {
      case "success":
        return new ResponseEntity<>(new SimpleResponseWrapper(200, responseMQ), HttpStatus.OK);
      case "can't cancel completed transaction":
        return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
      case "transaction not found":
        return new ResponseEntity<>(new SimpleResponseWrapper(404, responseMQ), HttpStatus.NOT_FOUND);
      default:
        return new ResponseEntity<>(new SimpleResponseWrapper(401, responseMQ), HttpStatus.UNAUTHORIZED);
    }
  }

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
      switch (responseMQ) {
        case "success":
          return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.CREATED);
        case "not enough balance":
          return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.valueOf(400));
        case "top up option not found":
          return new ResponseEntity<>(new SimpleResponseWrapper(404, responseMQ), HttpStatus.NOT_FOUND);
        default:
          return new ResponseEntity<>(new SimpleResponseWrapper(401, responseMQ), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  //get all in progress transaction from current user
  @GetMapping("/in-progress/{page}")
  public ResponseEntity<?> getInProgress(@CurrentUser org.springframework.security.core.userdetails.User userDetails, @PathVariable int page) throws Exception{
//    RPCClient rpcClient = new RPCClient("in-progress");
    HistoryRequest historyRequest = new HistoryRequest(userDetails.getUsername(),page);

//    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(historyRequest));
    String responseMQ = transactionWorker.transactionInProgress(objectMapper.writeValueAsString(historyRequest));
    List<TransactionDTO> list = objectMapper.readValue(responseMQ, new TypeReference<List<TransactionDTO>>() {});
    return new ResponseEntity<>(new ResponseWrapper(200, "success", list), HttpStatus.OK);
  }

  //get all completed transaction from current user
  @GetMapping("/completed/{page}")
  public ResponseEntity<?> getCompleted(@CurrentUser org.springframework.security.core.userdetails.User userDetails, @PathVariable int page) throws Exception{
//    RPCClient rpcClient = new RPCClient("completed");
    HistoryRequest historyRequest = new HistoryRequest(userDetails.getUsername(),page);

//    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(historyRequest));
    String responseMQ = transactionWorker.transactionCompleted(objectMapper.writeValueAsString(historyRequest));
    List<TransactionDTO> list = objectMapper.readValue(responseMQ, new TypeReference<List<TransactionDTO>>() {});
    return new ResponseEntity<>(new ResponseWrapper(200, "success", list), HttpStatus.OK);
  }
}
