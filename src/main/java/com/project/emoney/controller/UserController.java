package com.project.emoney.controller;

import com.project.emoney.entity.User;
import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.SimpleResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> loadProfile(@RequestHeader("Authorization") String token){
        return new ResponseEntity<>(new SimpleResponseWrapper(201, token), HttpStatus.OK);
    }

}
