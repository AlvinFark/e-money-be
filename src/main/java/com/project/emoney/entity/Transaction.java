package com.project.emoney.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  private long id;
  private long userId;
  private String cardNumber;
  private long value;
  private long fee;
  private Status status;
  private TransactionMethod method;
  private LocalDateTime time;
  private LocalDateTime expiry;
  private String imagePath;

  public Transaction(long userId, String cardNumber, long value, long fee, Status status, TransactionMethod method, LocalDateTime time, LocalDateTime expiry) {
    this.userId = userId;
    this.cardNumber = cardNumber;
    this.value = value;
    this.fee = fee;
    this.status = status;
    this.method = method;
    this.time = time;
    this.expiry = expiry;
  }
}
