package com.project.emoney.payload.dto;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.TransactionMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//help jackson serialize and deserialize localDateTime type
//for some reason it won't deserialize it in worker services
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
  private long id;
  private long userId;
  private String cardNumber;
  private long value;
  private long fee;
  private Status status;
  private TransactionMethod method;
  private String time;
  private String expiry;

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

