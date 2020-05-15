package com.project.emoney.entity;

import com.project.emoney.utils.GlobalVariable;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

@Data
@AllArgsConstructor
public class EmailToken {
  long id;
  long userId;
  String token;
  LocalDateTime time;

  public EmailToken(String token, User user) {
    this.token = token;
    this.userId = user.getId();
    this.time = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS);
  }
}
