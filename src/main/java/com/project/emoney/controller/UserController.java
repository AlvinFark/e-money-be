package com.project.emoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.ResponseWrapper;
import com.project.emoney.security.CurrentUser;
import com.project.emoney.utils.RPCClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/profile")
    public ResponseEntity<?> loadProfile(@CurrentUser org.springframework.security.core.userdetails.User userDetails) throws Exception{
        RPCClient rpcClient = new RPCClient("profile");
        String responseMQ = rpcClient.call(userDetails.getUsername());
        User user = objectMapper.readValue(responseMQ, User.class);
        return new ResponseEntity<>(new ResponseWrapper(201, "success", user), HttpStatus.OK);
    }

}
