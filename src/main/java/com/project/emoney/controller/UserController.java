package com.project.emoney.controller;

import com.project.emoney.mybatis.UserService;
import com.project.emoney.payload.ResponseWrapper;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> loadProfile(@CurrentUser User userDetails){
//        com.project.emoney.entity.User user = userService.ge
        return new ResponseEntity<>(new ResponseWrapper(201, "success", userDetails), HttpStatus.OK);
    }

}
