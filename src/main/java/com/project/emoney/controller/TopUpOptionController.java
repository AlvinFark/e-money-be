package com.project.emoney.controller;


import com.project.emoney.entity.TopUpOption;
import com.project.emoney.mybatis.TopUpOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/top-up")
public class TopUpOptionController {
    @Autowired
    private TopUpOptionService topUpOptionService;

    //CREATE TOP UP OPTION
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody TopUpOption topUpOption) {

        topUpOptionService.createTopUpOption(topUpOption);

        return new ResponseEntity<>(topUpOption, HttpStatus.CREATED);
    }

    //SELECT ALL TOP UP OPTION
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public ResponseEntity<?> selectAll() {

        List<TopUpOption> list = topUpOptionService.getListTopUpOption();

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
