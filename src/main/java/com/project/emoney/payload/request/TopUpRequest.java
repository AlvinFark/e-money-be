package com.project.emoney.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//request to be send to 3rd party server
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopUpRequest {
  private String number;
  private long value;
}
