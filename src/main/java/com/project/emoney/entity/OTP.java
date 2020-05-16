package com.project.emoney.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
  private long id;
  private String emailOrPhone;
  private String code;
  private LocalDateTime time;
}
