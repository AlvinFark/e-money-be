package com.project.emoney.entity;

import com.project.emoney.utils.GlobalVariable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EmailToken {
  private long id;
  private long userId;
  private String token;
  private LocalDateTime time;

  public EmailToken(String token, User user) {
    this.token = token;
    this.userId = user.getId();
    this.time = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS);
  }
}
