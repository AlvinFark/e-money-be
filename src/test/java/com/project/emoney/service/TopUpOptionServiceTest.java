package com.project.emoney.service;

import com.project.emoney.entity.TopUpOption;
import com.project.emoney.mapper.TopUpOptionMapper;
import com.project.emoney.service.impl.TopUpOptionServiceImpl;
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
public class TopUpOptionServiceTest {

  private static final long ID = 1L;
  private static final long VALUE = 10000L;
  private static final long FEE = 1500L;

  @Mock
  TopUpOptionMapper topUpOptionMapper;

  @InjectMocks
  TopUpOptionServiceImpl topUpOptionService;

  public TopUpOption getTopUpOption(){
    return new  TopUpOption(ID, VALUE, FEE);
  }

  @Test
  public void insertTopUpOptionTest(){
    final TopUpOption topUpOption = getTopUpOption();
    topUpOptionService.insert(topUpOption);
    verify(topUpOptionMapper, times(1)).insert(topUpOption);
  }

  @Test
  public void getAllTopUpOptionTest(){
    List<TopUpOption> expected = new ArrayList<TopUpOption>();
    expected.add(getTopUpOption());
    when(topUpOptionMapper.getAll()).thenReturn(expected);

    final List<TopUpOption> list = topUpOptionService.getAll();
    assert list.equals(expected);
  }

  @Test
  public void getTopUpOptionByIdTest() {
    final TopUpOption expected = getTopUpOption();
    when(topUpOptionMapper.getById(ID)).thenReturn(expected);

    assert topUpOptionService.getById(ID).equals(expected);
  }
}
