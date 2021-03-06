package com.project.emoney.payload.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.emoney.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//add token to user on data that sent to android, also hide encrypted password and active status
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"password", "active"})
public class UserWithToken extends User {
  private String token;

  public UserWithToken(User user, String token){
    super(user.getId(),user.getName(),user.getEmail(),user.getPhone(),user.getPassword(),user.getBalance(),user.isActive());
    this.token = token;
  }
}
