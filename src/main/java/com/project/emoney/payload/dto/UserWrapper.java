package com.project.emoney.payload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.emoney.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//same as user with token, but without token
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"password", "active"})
public class UserWrapper extends User {
  public UserWrapper(User user){
    super(user.getId(),user.getName(),user.getEmail(),user.getPhone(),user.getPassword(),user.getBalance(),user.isActive());
  }
}