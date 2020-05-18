package com.project.emoney.service.impl;

import com.project.emoney.entity.OTP;
import com.project.emoney.mapper.OTPMapper;
import com.project.emoney.service.OTPService;
import com.project.emoney.utils.Generator;
import com.project.emoney.utils.GlobalVariable;
import com.project.emoney.utils.TwilioSMS;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.exception.AuthenticationException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OTPServiceImpl implements OTPService {

  @Autowired
  private OTPMapper otpMapper;

  @Autowired
  private Generator generator;

  @Value("${phoneNumber}") private String myTwilioPhoneNumber;
  @Value("${twilioAccountSid}") private String twilioAccountSid;
  @Value("${twilioAuthToken}") private String twilioAuthToken;

  @Override
  public void insert(OTP otp) {
    otpMapper.insert(otp);
  }

  @Override
  public OTP getByCodeOrderByTimeDesc(String code) {
    return otpMapper.getByCodeOrderByTimeDesc(code);
  }

  @Override
  public String sendOtp(String phone) {
    try {
      //initialize otp details
      OTP otp = new OTP();
      otp.setEmailOrPhone(phone);
      otp.setCode(generator.generateOtp());
      //add 7 hours to calibrate it with server
      otp.setTime(LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS));
      TwilioSMS twilioSMS = new TwilioSMS();
      twilioSMS.send(myTwilioPhoneNumber, twilioAccountSid, twilioAuthToken, phone, otp.getCode());
      insert(otp);
      return "success";
    } catch (ApiException | AuthenticationException e) {
      //twilio free can only send to verified number
      return "failed";
    }
  }
}
