package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.User;
import com.project.emoney.payload.dto.UserWithToken;
import com.project.emoney.payload.request.LoginRequest;
import com.project.emoney.security.JwtTokenUtil;
import com.project.emoney.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AuthWorkerTest {

  @Mock
  private UserService userService;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthWorker authWorker;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final String emailOrPhone = "dwight@dundermifflin.co";
  private static final String password = "P@ssw0rd";

  private LoginRequest getLoginRequest() {
    return new LoginRequest(emailOrPhone,password);
  }

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

  @Test
  public void loginUnregisteredTest() throws JsonProcessingException {
    final LoginRequest loginRequest = getLoginRequest();
    when(userService.getByEmailOrPhone(emailOrPhone)).thenReturn(null);
    assert authWorker.login(objectMapper.writeValueAsString(loginRequest)).equals("email or phone not registered");
  }

  @Test
  public void loginWrongPasswordTest() throws JsonProcessingException {
    final LoginRequest loginRequest = getLoginRequest();
    User user = getUser();
    when(userService.getByEmailOrPhone(emailOrPhone)).thenReturn(user);
    lenient().when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(emailOrPhone,password))).thenThrow(new BadCredentialsException(""));
    assert authWorker.login(objectMapper.writeValueAsString(loginRequest)).equals("wrong password");
  }
}
