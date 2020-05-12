package com.project.emoney.mapper;

import com.project.emoney.entity.OTP;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OTPMapper {

  @Insert("INSERT INTO otp (emailOrPhone, code, time) VALUES (#{emailOrPhone}, #{code}, #{time})")
  void create(OTP otp);
}
