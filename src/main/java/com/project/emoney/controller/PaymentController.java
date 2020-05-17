package com.project.emoney.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.dto.TransactionDTO;
import com.project.emoney.payload.response.ResponseWrapper;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import com.project.emoney.utils.RPCClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

  ObjectMapper objectMapper = new ObjectMapper();

  @GetMapping("/va/{virtualAccount}")
  public ResponseEntity<?> getMerchantTransaction(@PathVariable String virtualAccount, @RequestHeader("Authorization") String authorization) throws Exception {
    if (authorization == null || !authorization.toLowerCase().startsWith("basic")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(401, "no authorization"), HttpStatus.UNAUTHORIZED);
    }
    // Authorization: Basic base64credentials
    String base64Credentials = authorization.substring("Basic".length()).trim();
    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
    // credentials = username:password
    final String[] values = credentials.split(":", 2);
    if (!values[0].equals("superalfa")||!values[1].equals("verycomplexpassword")){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "wrong authorization"), HttpStatus.BAD_REQUEST);
    }

    RPCClient rpcClient = new RPCClient("merchant");
    String responseMQ = rpcClient.call(virtualAccount);
    if (responseMQ.equals("invalid virtual account number")){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
    }
    if (responseMQ.equals("user not found")){
      return new ResponseEntity<>(new SimpleResponseWrapper(404, responseMQ), HttpStatus.NOT_FOUND);
    }
    List<TransactionDTO> list = objectMapper.readValue(responseMQ, new TypeReference<List<TransactionDTO>>() {});
    return new ResponseEntity<>(new ResponseWrapper(200, "success", list), HttpStatus.OK);
  }

  @PostMapping("/confirm/{id}")
  public ResponseEntity<?> confirm(@PathVariable long id, @RequestHeader("Authorization") String authorization) throws Exception {
    if (authorization == null || !authorization.toLowerCase().startsWith("basic")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(401, "no authorization"), HttpStatus.UNAUTHORIZED);
    }
    // Authorization: Basic base64credentials
    String base64Credentials = authorization.substring("Basic".length()).trim();
    byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
    String credentials = new String(credDecoded, StandardCharsets.UTF_8);
    // credentials = username:password
    final String[] values = credentials.split(":", 2);
    if (!values[0].equals("superalfa")||!values[1].equals("verycomplexpassword")){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "wrong authorization"), HttpStatus.BAD_REQUEST);
    }

    RPCClient rpcClient = new RPCClient("confirmpayment");
    String responseMQ = rpcClient.call(String.valueOf(id));
    switch (responseMQ){
      case "success":
      return new ResponseEntity<>(new SimpleResponseWrapper(200, responseMQ), HttpStatus.OK);
      case "transaction not found":
      return new ResponseEntity<>(new SimpleResponseWrapper(404, responseMQ), HttpStatus.NOT_FOUND);
      case "can't confirm completed transaction":
      return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
      default:
      return new ResponseEntity<>(new SimpleResponseWrapper(500, responseMQ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
