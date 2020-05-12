package com.project.emoney.controller;

import com.project.emoney.entity.User;
import com.project.emoney.mybatis.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    //SELECT USER BY ID
    @RequestMapping(value = "/getUserById/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserById(@PathVariable("id") long id) {

        User user = userService.getUserById(id);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
