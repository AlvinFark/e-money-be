package com.project.emoney.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.ResponseWrapper;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.payload.LoginRequest;
import com.project.emoney.payload.UserWithToken;
import com.project.emoney.security.JwtUserDetailsService;
import com.project.emoney.utils.RPCClient;
import com.project.emoney.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthController {

  @Autowired
  private JwtUserDetailsService userDetailsService;

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  Validation validation;

  @RequestMapping(value = "/api/register", method = RequestMethod.POST)
  public ResponseEntity<?> saveUser(@RequestBody User user){
    userDetailsService.save(user);
    return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.OK);
  }

  @RequestMapping(value = "/api/login", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
    //validate password
    if (!validation.password(loginRequest.getPassword())){
      return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
    }

    //validate email & convert phone & validate phone
    if (!validation.email(loginRequest.getEmailOrPhone())){
      loginRequest.setEmailOrPhone(validation.convertPhone(loginRequest.getEmailOrPhone()));
      if (!validation.phone(loginRequest.getEmailOrPhone())) {
        return new ResponseEntity<>(new SimpleResponseWrapper(400, "invalid credentials"), HttpStatus.BAD_REQUEST);
      }
    }

    //send and receive MQ
    RPCClient rpcClient = new RPCClient("login");
    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(loginRequest));

    //translate MQ response
    if (responseMQ.equals("bad credentials")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
    } else if (responseMQ.equals("inactive account, otp sent")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(403, responseMQ), HttpStatus.FORBIDDEN);
    } else if (responseMQ.equals("unverified number, can't send otp")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(500, responseMQ), HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      UserWithToken userWithToken = objectMapper.readValue(responseMQ, UserWithToken.class);
      return new ResponseEntity<>(new ResponseWrapper(202, "accepted", userWithToken), HttpStatus.ACCEPTED);
    }
  }
}