package com.project.emoney.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.service.TopUpOptionService;
import com.project.emoney.payload.response.ResponseWrapper;
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

    //create top up option, unused by client
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody TopUpOption topUpOption) {
        topUpOptionService.createTopUpOption(topUpOption);
        return new ResponseEntity<>(topUpOption, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/topup-option", method = RequestMethod.GET)
    public ResponseEntity<?> selectAll() throws Exception {
        //send and receive MQ, no message, empty string just to notify MQ
        RPCClient rpcClient = new RPCClient("topup-option");
        String responseMQ = rpcClient.call("");
        //deserialize and return list
        List<TopUpOption> topUpOptions = objectMapper.readValue(responseMQ, new TypeReference<List<TopUpOption>>() {});
        return new ResponseEntity<>(new ResponseWrapper(200, "success", topUpOptions), HttpStatus.OK);
    }
}
