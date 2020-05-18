package com.project.emoney.worker;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import com.project.emoney.service.EmailTokenService;
import com.project.emoney.service.UserService;
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
public class EmailTokenWorkerTest {
  @Mock
  EmailTokenService emailTokenService;

  @Mock
  UserService userService;

  @InjectMocks
  EmailTokenWorker emailTokenWorker;

  private static final String token = "78FSHI72SF973FA3AFJ";

  private User getUser(){
    User user = new  User();
    user.setId(1);
    user.setName("Dwight Schrute");
    user.setEmail("dwight@dundermifflin.co");
    user.setPhone("628595839538");
    user.setPassword("password");
    user.setBalance(0);
    user.setActive(false);
    return user;
  }

  private EmailToken getEmailToken() {
    return new EmailToken(1,1,token, LocalDateTime.now());
  }

  @Test
  public void invalidTokenTest() {
    when(emailTokenService.getByToken(token)).thenReturn(null);
    assert emailTokenWorker.verify(token).equals("invalid code");
  }

  @Test
  public void expiredTokenTest() {
    EmailToken emailToken = getEmailToken();
    emailToken.setTime(emailToken.getTime().minusDays(1));
    when(emailTokenService.getByToken(token)).thenReturn(emailToken);
    assert emailTokenWorker.verify(token).equals("expired code");
  }

  @Test
  public void verifySuccessTest() {
    EmailToken emailToken = getEmailToken();
    User user = getUser();
    when(emailTokenService.getByToken(token)).thenReturn(emailToken);
    when(userService.getById(1)).thenReturn(user);
    System.out.println(emailTokenWorker.verify(token));
    assert emailTokenWorker.verify(token).equals("success");
  }
}
