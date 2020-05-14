package com.project.emoney.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//response without data, so that it won't send null to client
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponseWrapper {
  int code;
  String message;
}