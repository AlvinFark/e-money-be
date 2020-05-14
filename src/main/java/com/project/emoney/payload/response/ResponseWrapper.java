package com.project.emoney.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//establish response format, this one is when response has data
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
