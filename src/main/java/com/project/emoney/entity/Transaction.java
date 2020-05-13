package com.project.emoney.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
  long id;
  long userId;
  String cardNumber;
  long value;
  long fee;
  Status status;
  TransactionMethod method;
  LocalDateTime time;
  LocalDateTime expiry;

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
