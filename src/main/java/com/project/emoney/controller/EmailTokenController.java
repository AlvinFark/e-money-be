package com.project.emoney.controller;

import com.project.emoney.worker.EmailTokenWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EmailTokenController {

  @Autowired
  EmailTokenWorker emailTokenWorker;

  @GetMapping(value = "/verify/{token}")
  public String confirmRegistration(@PathVariable String token) {
    //send and receive MQ
//    RPCClient rpcClient = new RPCClient("verify");
//    String responseMQ = rpcClient.call(objectMapper.writeValueAsString(loginRequest));
    return emailTokenWorker.verify(token);
  }
}
