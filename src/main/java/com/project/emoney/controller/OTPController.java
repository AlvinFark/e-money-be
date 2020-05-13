package com.project.emoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.OTPRequest;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.utils.RPCClient;
import com.project.emoney.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OTPController {

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  Validation validation;

  @RequestMapping(value = "/otp", method = RequestMethod.POST)
  public ResponseEntity<?> create(@RequestBody OTPRequest otpRequest) throws Exception {
    //validasi otp
    if (!validation.otp(otpRequest.getCode())){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid code"), HttpStatus.BAD_REQUEST);
    }
    //send and receive MQ
    RPCClient rpcClient = new RPCClient("otp");
    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(otpRequest));
    return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
  }
}