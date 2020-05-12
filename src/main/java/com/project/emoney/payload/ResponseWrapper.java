package com.project.emoney.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper extends SimpleResponseWrapper {
  Object data;

  public ResponseWrapper(int code, String message, Object data) {
    super(code, message);
    this.data = data;
  }
}
