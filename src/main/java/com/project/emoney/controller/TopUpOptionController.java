package com.project.emoney.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.entity.User;
import com.project.emoney.mybatis.TopUpOptionService;
import com.project.emoney.payload.ResponseWrapper;
import com.project.emoney.payload.SimpleResponseWrapper;
import com.project.emoney.security.CurrentUser;
import com.project.emoney.utils.RPCClient;
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

    ObjectMapper objectMapper = new ObjectMapper();

    //CREATE TOP UP OPTION
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody TopUpOption topUpOption) {

        topUpOptionService.createTopUpOption(topUpOption);

        return new ResponseEntity<>(topUpOption, HttpStatus.CREATED);
    }

    //SELECT ALL TOP UP OPTION
    @RequestMapping(value = "/api/topup-option", method = RequestMethod.GET)
    public ResponseEntity<?> selectAll() throws Exception {
        //send and receive MQ
        RPCClient rpcClient = new RPCClient("topup-option");
        String responseMQ = rpcClient.call("");
        List<TopUpOption> topUpOptions = objectMapper.readValue(responseMQ, new TypeReference<List<TopUpOption>>() {});
        return new ResponseEntity<>(new ResponseWrapper(200, "success", topUpOptions), HttpStatus.OK);
    }
}
