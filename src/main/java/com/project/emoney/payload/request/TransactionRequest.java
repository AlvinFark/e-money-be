package com.project.emoney.payload.request;

import com.project.emoney.entity.TransactionMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//need to get that idTopUpOption param because transaction use fee and value instead
//using fee and value so that the history won't change if top up option changed
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
  private String cardNumber;
  private long idTopUpOption;
  private TransactionMethod method;
  private String email;
}
