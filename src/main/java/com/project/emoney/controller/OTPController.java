package com.project.emoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.payload.dto.UserWithToken;
import com.project.emoney.payload.request.OTPRequest;
import com.project.emoney.payload.response.ResponseWrapper;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import com.project.emoney.utils.RPCClient;
import com.project.emoney.utils.Validation;
import com.project.emoney.worker.OTPWorker;
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

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private Validation validation;

  @Autowired
  private OTPWorker otpWorker;

  @RequestMapping(value = "/otp", method = RequestMethod.POST)
  public ResponseEntity<?> create(@RequestBody OTPRequest otpRequest) throws Exception {
    //validate otp format
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
//    RPCClient rpcClient = new RPCClient("otp");
//    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(otpRequest));
    String responseMQ = otpWorker.send(objectMapper.writeValueAsString(otpRequest));

    //translate response mq
    switch (responseMQ) {
      case "account already active":
        return new ResponseEntity<>(new SimpleResponseWrapper(409, responseMQ), HttpStatus.CONFLICT);
      case "invalid code":
        return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
      case "code expired":
        return new ResponseEntity<>(new SimpleResponseWrapper(401, responseMQ), HttpStatus.UNAUTHORIZED);
      case "user not found":
        return new ResponseEntity<>(new SimpleResponseWrapper(404, responseMQ), HttpStatus.NOT_FOUND);
      default:
        UserWithToken userWithToken = objectMapper.readValue(responseMQ, UserWithToken.class);
        return new ResponseEntity<>(new ResponseWrapper(202, "accepted", userWithToken), HttpStatus.ACCEPTED);
    }
  }
}