package com.project.emoney.mapper;

import com.project.emoney.entity.OTP;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OTPMapper {

  @Insert("INSERT INTO otp (emailOrPhone, code, time) VALUES (#{emailOrPhone}, #{code}, #{time})")
  void insert(OTP otp);

  @Select("SELECT * FROM otp WHERE CODE = #{code} ORDER BY time DESC LIMIT 1")
  OTP getByCodeOrderByTimeDesc(String code);
}
