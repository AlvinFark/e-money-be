package com.project.emoney.service;

import com.project.emoney.entity.*;
import com.project.emoney.mapper.OTPMapper;
import com.project.emoney.service.impl.OTPServiceImpl;
import com.project.emoney.utils.Generator;
import com.project.emoney.utils.TwilioSMS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OTPServiceTest {

    private static final long ID = 1;
    private static final String EMAIL_OR_PHONE = "6282272068810";
    private static final String CODE = "1234";
    private static final LocalDateTime TIME = LocalDateTime.now().plusHours(6);

    @Mock
    private OTPMapper otpMapper;

    @Mock
    private Generator generator;

    @Mock
    private final TwilioSMS twilioSMS = new TwilioSMS();

    @InjectMocks
    private OTPServiceImpl otpService;

    public OTP getOTP() {
        OTP otp = new OTP();
        otp.setId(ID);
        otp.setEmailOrPhone(EMAIL_OR_PHONE);
        otp.setCode(CODE);
        otp.setTime(TIME);
        return otp;
    }

    @Test
    public void getByCodeOrderByTimeDesc() {
        final OTP expected = getOTP();
        when(otpMapper.getByCodeOrderByTimeDesc(CODE)).thenReturn(expected);
        final OTP otp = otpService.getByCodeOrderByTimeDesc(CODE);
        assert otp.equals(expected);
    }

    @Test
    public void insertOTPTest() {
        final OTP otp = getOTP();
        otpService.insert(otp);
        verify(otpMapper, times(1)).insert(otp);
    }

    @Test
    public void failedOTPSendTest() {
        final String phone = "6288888888";
        assert otpService.sendOtp(phone).equals("failed");
    }
}