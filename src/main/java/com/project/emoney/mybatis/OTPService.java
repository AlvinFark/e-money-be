package com.project.emoney.mybatis;

import com.project.emoney.entity.OTP;
import org.springframework.stereotype.Service;

public interface OTPService {
  void create(OTP otp);
  OTP getByCodeOrderByTimeDesc(String code);
}
