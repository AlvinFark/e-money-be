package com.project.emoney.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  long id;
  String name;
  String email;
  @JsonIgnore String password;
  long balance;
  boolean active;
}
