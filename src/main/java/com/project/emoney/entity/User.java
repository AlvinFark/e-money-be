package com.project.emoney.entity;

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
  String phone;
  String password;
  long balance;
  boolean active;
}
