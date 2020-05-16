package com.project.emoney.service;

import com.project.emoney.entity.OTP;

public interface OTPService {
  void insert(OTP otp);
  OTP getByCodeOrderByTimeDesc(String code);
  String sendOtp(String phone);
}
