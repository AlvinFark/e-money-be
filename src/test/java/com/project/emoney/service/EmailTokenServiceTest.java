package com.project.emoney.service;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.mapper.EmailTokenMapper;
import com.project.emoney.service.impl.EmailTokenServiceImpl;
import com.project.emoney.utils.GlobalVariable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EmailTokenServiceTest {

  @Mock
  EmailTokenMapper emailTokenMapper;

  @InjectMocks
  EmailTokenServiceImpl emailTokenService;

  public User getUser(){
    User user = new  User();
    user.setId(1);
    user.setName("Dwight Schrute");
    user.setEmail("dwight@dundermifflin.co");
    user.setPhone("628595839538");
    user.setPassword("password");
    user.setBalance(10000);
    user.setActive(true);
    return user;
  }

  public EmailToken getEmailToken() {
    return new EmailToken(0,1,"JYFA87AJFS8AFK2JD0A2", LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_APP_HOURS));
  }

  @Test
  public void insertEmailTokenTest() {
    final EmailToken emailToken = getEmailToken();
    final User user = getUser();
    emailTokenService.insertByUserAndToken(user,emailToken.getToken());
    verify(emailTokenMapper, times(1)).insert(emailToken);
  }
}
