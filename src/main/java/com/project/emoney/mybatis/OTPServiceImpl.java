package com.project.emoney.mybatis;

import com.project.emoney.entity.OTP;
import com.project.emoney.mapper.OTPMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OTPServiceImpl implements OTPService {

  @Autowired
  OTPMapper otpMapper;

  @Override
  public void create(OTP otp) {
    otpMapper.create(otp);
  }

  @Override
  public OTP getByCodeOrderByTimeDesc(String code) {
    return otpMapper.getByCodeOrderByTimeDesc(code);
  }
}
