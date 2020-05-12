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
  @JsonIgnore long userId;
  long cardNumber;
  int value;
  int fee;
  Status status;
  TransactionMethod method;
  LocalDateTime time;
  LocalDateTime expiry;
}
