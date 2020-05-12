package com.project.emoney.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.payload.LoginRequest;
import com.project.emoney.security.JwtUserDetailsService;
import com.project.emoney.utils.RPCClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@CrossOrigin
public class AuthController {

  @Autowired
  private JwtUserDetailsService userDetailsService;

  ObjectMapper objectMapper = new ObjectMapper();

  String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$";
  String emailRegex = ".+@.+\\..+";

  @RequestMapping(value = "/api/register", method = RequestMethod.POST)
  public ResponseEntity<?> saveUser(@RequestBody User user){
    userDetailsService.save(user);
    return new ResponseEntity<>(new SimpleResponseWrapper(201, "success"), HttpStatus.OK);
  }

  @RequestMapping(value = "/api/login", method = RequestMethod.POST)
  public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
    RPCClient rpcClient = new RPCClient("login");
    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(loginRequest));
    if (responseMQ.equals("success")){
      return new ResponseEntity<>(new SimpleResponseWrapper(201, responseMQ), HttpStatus.OK);
    } else if (responseMQ.equals("bad credentials")) {
      return new ResponseEntity<>(new SimpleResponseWrapper(400, responseMQ), HttpStatus.BAD_REQUEST);
    } else {
      return new ResponseEntity<>(new SimpleResponseWrapper(401, responseMQ), HttpStatus.UNAUTHORIZED);
    }
  }
}