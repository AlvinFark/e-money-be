package com.project.emoney.payload;


import com.project.emoney.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithToken extends User {
  String token;

  public UserWithToken(User user, String token){
    super(user.getId(),user.getName(),user.getEmail(),user.getPhone(),user.getPassword(),user.getBalance(),user.isActive());
    this.token = token;
  }
}
