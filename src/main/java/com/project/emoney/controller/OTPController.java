package com.project.emoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.OTPRequest;
import com.project.emoney.payload.ResponseWrapper;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.payload.UserWithToken;
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

    //validate email & convert phone & validate phone
    if (!validation.email(otpRequest.getEmailOrPhone())){
      otpRequest.setEmailOrPhone(validation.convertPhone(otpRequest.getEmailOrPhone()));
      if (!validation.phone(otpRequest.getEmailOrPhone())) {
        return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
      }
    }

    //send and receive MQ
    RPCClient rpcClient = new RPCClient("otp");
    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(otpRequest));

    //translate response mq
    if (responseMQ.equals("account already active")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(409, responseMQ), HttpStatus.CONFLICT);
    } else if (responseMQ.equals("invalid code")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
    } else if (responseMQ.equals("code expired")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(401, responseMQ), HttpStatus.UNAUTHORIZED);
    } else if (responseMQ.equals("user not found")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(404, responseMQ), HttpStatus.NOT_FOUND);
    } else {
      UserWithToken userWithToken = objectMapper.readValue(responseMQ, UserWithToken.class);
      return new ResponseEntity<>(new ResponseWrapper(202, "accepted", userWithToken), HttpStatus.ACCEPTED);
    }
  }
}