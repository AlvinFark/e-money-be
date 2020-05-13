package com.project.emoney.security;

import com.project.emoney.entity.User;
import com.project.emoney.mybatis.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class JwtUserDetailsService implements UserDetailsService {

  @Autowired
  UserService userService;

  @Autowired
  private PasswordEncoder bcryptEncoder;

  @Override
  public UserDetails loadUserByUsername(String s) {
    User user = userService.getUserByEmailOrPhone(s);
    if (user == null) {
      throw new UsernameNotFoundException("User not found with email or phone: " + s);
    }
    return new org.springframework.security.core.userdetails.User(String.valueOf(user.getEmail()), user.getPassword(), new ArrayList<>());
  }

  public User save(User user) {
    user.setPassword(bcryptEncoder.encode(user.getPassword()));
    return userService.insert(user);
  }
}
