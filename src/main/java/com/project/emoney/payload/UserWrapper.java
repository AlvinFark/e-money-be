package com.project.emoney.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.emoney.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties({"password", "active"})
public class UserWrapper extends User {
  public UserWrapper(User user){
    super(user.getId(),user.getName(),user.getEmail(),user.getPhone(),user.getPassword(),user.getBalance(),user.isActive());
  }
}