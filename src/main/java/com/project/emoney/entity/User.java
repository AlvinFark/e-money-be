package com.project.emoney.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private long id;
  private String name;
  private String email;
  private String phone;
  private String password;
  private long balance;
  private boolean active;
}
