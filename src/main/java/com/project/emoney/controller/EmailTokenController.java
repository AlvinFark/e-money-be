package com.project.emoney.controller;

import com.project.emoney.utils.RPCClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EmailTokenController {

  @GetMapping(value = "/verify/{token}")
  public String confirmRegistration(@PathVariable String token) throws Exception {
    //send and receive MQ
    RPCClient rpcClient = new RPCClient("verify");
    return rpcClient.call(token);
  }
}
