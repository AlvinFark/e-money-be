package com.project.emoney.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.utils.RPCClient;
import com.project.emoney.worker.EmailTokenWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmailTokenController {

  @Autowired
  EmailTokenWorker emailTokenWorker;

  @Autowired
  ObjectMapper objectMapper;

  @GetMapping(value = "/verify/{token}")
  public String confirmRegistration(@PathVariable String token) throws Exception {
    //send and receive MQ
    RPCClient rpcClient = new RPCClient("verify");
    return rpcClient.call(token);
//    return emailTokenWorker.verify(token);
  }
}
