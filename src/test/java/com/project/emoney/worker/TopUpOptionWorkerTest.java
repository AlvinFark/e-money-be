package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.service.TopUpOptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TopUpOptionWorkerTest {
  @Mock
  private TopUpOptionService topUpOptionService;

  @InjectMocks
  private TopUpOptionWorker topUpOptionWorker;

  private final ObjectMapper objectMapper = new ObjectMapper();

  List<TopUpOption> getTopUpOptions(){
    List<TopUpOption> topUpOptions = new ArrayList<>();
    topUpOptions.add(new TopUpOption(1,10000,1000));
    topUpOptions.add(new TopUpOption(2,20000,1000));
    return topUpOptions;
  }

  @Test
  public void topUpOptionTest() throws JsonProcessingException {
    List<TopUpOption> topUpOptions =getTopUpOptions();
    when(topUpOptionService.getAll()).thenReturn(topUpOptions);
    assert topUpOptionWorker.getList().equals(objectMapper.writeValueAsString(topUpOptions));
  }
}
