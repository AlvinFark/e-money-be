package com.project.emoney.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//same ase login request but for otp request
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTPRequest {
  private String emailOrPhone;
  private String code;
}
