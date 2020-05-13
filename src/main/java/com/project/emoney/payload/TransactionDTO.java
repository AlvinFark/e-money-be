package com.project.emoney.payload;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.TransactionMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
  long id;
  long userId;
  String cardNumber;
  long value;
  long fee;
  Status status;
  TransactionMethod method;
  String time;
  String expiry;

  public TransactionDTO(Transaction transaction) {
    this.id = transaction.getId();
    this.userId = transaction.getUserId();
    this.cardNumber = transaction.getCardNumber();
    this.value = transaction.getValue();
    this.fee = transaction.getFee();
    this.status = transaction.getStatus();
    this.method = transaction.getMethod();
    this.time = transaction.getTime().toString().substring(0,10)+' '+transaction.getTime().toString().substring(11);
    this.expiry = transaction.getExpiry().toString().substring(0,10)+' '+transaction.getExpiry().toString().substring(11);
  }
}

