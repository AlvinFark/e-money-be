package com.project.emoney.payload;

import com.project.emoney.entity.TransactionMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
  String cardNumber;
  long idTopUpOption;
  TransactionMethod method;
  String email;
}
