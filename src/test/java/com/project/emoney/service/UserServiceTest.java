package com.project.emoney.service;
;
import com.project.emoney.entity.User;
import com.project.emoney.mapper.UserMapper;
import com.project.emoney.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

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
  public void getUserByIdTest() {
    final long id = 1;
    final User expected = getUser();
    when(userMapper.getUserById(id)).thenReturn(expected);
    final User user = userService.getUserById(id);
    assert user.equals(expected);
  }

  @Test
  public void getUserByEmailOrPhoneTest() {
    final String email = "dwight@dundermifflin.co";
    final String phone = "628595839538";
    final User expected = getUser();
    when(userMapper.getUserByEmailOrPhone(email)).thenReturn(expected);
    when(userMapper.getUserByEmailOrPhone(phone)).thenReturn(expected);
    final User userEmail = userService.getUserByEmailOrPhone(email);
    final User userPhone = userService.getUserByEmailOrPhone(phone);
    assert userEmail.equals(expected);
    assert userPhone.equals(expected);
  }

  @Test
  public void insertTest() {
    final User user = getUser();
    userService.insert(user);
    verify(userMapper, times(1)).insert(user);
  }

  @Test
  public void getUserByEmailTest() {
    final String email = "dwight@dundermifflin.co";
    final User expected = getUser();
    when(userMapper.getUserByEmail(email)).thenReturn(expected);
    final User user = userService.getUserByEmail(email);
    assert user.equals(expected);
  }

  @Test
  public void setActiveTest() {
    final String email = "dwight@dundermifflin.co";
    userService.setActiveByEmail(email);
    verify(userMapper, times(1)).setActiveByEmail(email);
  }

  @Test
  public void updateBalanceTest() {
    final User user = getUser();
    userService.updateBalance(user);
    verify(userMapper, times(1)).updateBalance(user);
  }

  @Test
  public void updatePasswordTest() {
    final User user = getUser();
    userService.updatePassword(user);
    verify(userMapper, times(1)).updatePassword(user);
  }

  @Test
  public void activateUserTest() {
    final User user = getUser();
    userService.activateUser(user);
    verify(userMapper, times(1)).activateUser(user);
  }
}
