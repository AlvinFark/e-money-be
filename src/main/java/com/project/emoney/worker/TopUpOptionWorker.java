package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.service.TopUpOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopUpOptionWorker {
  @Autowired
  private TopUpOptionService topUpOptionService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public String getList() throws JsonProcessingException {
    return objectMapper.writeValueAsString(topUpOptionService.getAll());
  }
}
