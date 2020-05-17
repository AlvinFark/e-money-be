package com.project.emoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.dto.UserWrapper;
import com.project.emoney.payload.response.ResponseWrapper;
import com.project.emoney.payload.response.SimpleResponseWrapper;
import com.project.emoney.security.CurrentUser;
import com.project.emoney.utils.RPCClient;
import com.project.emoney.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

  final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private Validation validation;

  //reload profile, for checking balance etc
  @GetMapping("/profile")
  public ResponseEntity<?> loadProfile(@CurrentUser org.springframework.security.core.userdetails.User userDetails) throws Exception{
    RPCClient rpcClient = new RPCClient("profile");
    String responseMQ = rpcClient.call(userDetails.getUsername());
    User user = objectMapper.readValue(responseMQ, User.class);
    return new ResponseEntity<>(new ResponseWrapper(200, "success", new UserWrapper(user)), HttpStatus.OK);
  }

  //update password
  @PutMapping("/password")
  public ResponseEntity<?> updatePassword(@CurrentUser org.springframework.security.core.userdetails.User userDetails, @RequestBody User request) throws Exception {
    if (!validation.password(request.getPassword())){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
    }
    RPCClient rpcClient = new RPCClient("password");
    request.setEmail(userDetails.getUsername());
    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(request));
    if (responseMQ.equals("success")){
      return new ResponseEntity<>(new SimpleResponseWrapper(200, responseMQ), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(new SimpleResponseWrapper(500, responseMQ), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
