package com.project.emoney.controller;


import com.project.emoney.entity.TopUpOption;
import com.project.emoney.payload.response.ResponseWrapper;
import com.project.emoney.service.TopUpOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TopUpOptionController {
    @Autowired
    private TopUpOptionService topUpOptionService;

    //create top up option, unused by client
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody TopUpOption topUpOption) {
        topUpOptionService.insert(topUpOption);
        return new ResponseEntity<>(topUpOption, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/topup-option", method = RequestMethod.GET)
    public ResponseEntity<?> selectAll() {
        //direct connection without MQ
        List<TopUpOption> list = topUpOptionService.getAll();
        return new ResponseEntity<>(new ResponseWrapper(200, "success", list), HttpStatus.OK);
    }
}
