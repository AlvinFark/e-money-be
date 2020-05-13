package com.project.emoney.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

@Data
@AllArgsConstructor
public class EmailToken {
  private static final int EXPIRATION = 60 * 24;

  long id;
  long userId;
  String token;
  Date createdDate;
  Date expiryDate;

  @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  public EmailToken() {
    super();
  }

  public EmailToken(final String token) {
    super();

    this.token = token;
    this.expiryDate = calculateExpiryDate(EXPIRATION);
  }

  public EmailToken(final String token, final User user) {
    super();
    Calendar calendar = Calendar.getInstance();

    this.token = token;
    this.user = user;
    this.createdDate = new Date(calendar.getTime().getTime());
    this.expiryDate = calculateExpiryDate(EXPIRATION);
  }

  private Date calculateExpiryDate(int expiryTimeInMinutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Timestamp(calendar.getTime().getTime()));
    calendar.add(Calendar.MINUTE, expiryTimeInMinutes);
    return new Date(calendar.getTime().getTime());
  }
}
