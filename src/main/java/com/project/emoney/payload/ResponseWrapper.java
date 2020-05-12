package com.project.emoney.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper {
  int code;
  String message;
  Object data;

  public ResponseWrapper(int code, String message) {
    this.code = code;
    this.message = message;
  }
}
