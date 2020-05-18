package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserWorkerTest {

  @Mock
  UserService userService;

  @Mock
  PasswordEncoder passwordEncoder;

  @InjectMocks
  UserWorker userWorker;

  private final ObjectMapper objectMapper = new ObjectMapper();

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

  @Test
  public void profileTest() throws JsonProcessingException {
    final User user = getUser();
    final String email = user.getEmail();
    final String expected = objectMapper.writeValueAsString(user);
    when(userService.getByEmail(email)).thenReturn(user);
    assert userWorker.profile(email).equals(expected);
  }

  @Test
  public void changePasswordTest() throws JsonProcessingException {
    final User user = getUser();
    when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());

    userWorker.password(objectMapper.writeValueAsString(user));
    verify(userService, times(1)).updatePassword(user);
  }
}
