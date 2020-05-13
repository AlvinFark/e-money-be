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
  void create(OTP otp);

  @Select("SELECT * FROM OTP WHERE CODE = #{code} ORDER BY TIME DESC LIMIT 1")
  OTP getByCodeOrderByTimeDesc(String code);
}
