package com.project.emoney.entity;

import com.project.emoney.utils.GlobalVariable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OTPTest {

    private static final long ID = 1;
    private static final String EMAIL_OR_PHONE = "6282272068810";
    private static final String PHONE = "6282272068812";
    private static final String CODE = "1234";
    private static final String NEW_CODE = "5678";
    private static final LocalDateTime TIME = LocalDateTime.now().plusHours(6);
    private static final LocalDateTime NEW_TIME = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS);

    @Test
    public void constructor() {
        OTP otp = new OTP(ID, EMAIL_OR_PHONE, CODE, TIME);
        assertNotNull(otp);
        assertNotNull(otp.getId());
        assertNotNull(otp.getEmailOrPhone());
        assertEquals(otp.getEmailOrPhone(), EMAIL_OR_PHONE);
        assertNotNull(otp.getCode());
        assertEquals(otp.getCode(), CODE);
        assertNotNull(otp.getTime());
        assertEquals(otp.getTime(), TIME);
    }

    @Test
    public void setterPhone() {
        OTP otp = new OTP(ID, EMAIL_OR_PHONE, CODE, TIME);
        assertNotNull(otp);
        assertNotNull(otp.getId());
        assertNotNull(otp.getEmailOrPhone());
        assertEquals(otp.getEmailOrPhone(), EMAIL_OR_PHONE);
        assertNotNull(otp.getCode());
        assertEquals(otp.getCode(), CODE);
        assertNotNull(otp.getTime());
        assertEquals(otp.getTime(), TIME);

        otp.setEmailOrPhone(PHONE);
        assertEquals(otp.getEmailOrPhone(), PHONE);
    }

    @Test
    public void setterCode() {
        OTP otp = new OTP(ID, EMAIL_OR_PHONE, CODE, TIME);
        assertNotNull(otp);
        assertNotNull(otp.getId());
        assertNotNull(otp.getEmailOrPhone());
        assertEquals(otp.getEmailOrPhone(), EMAIL_OR_PHONE);
        assertNotNull(otp.getCode());
        assertEquals(otp.getCode(), CODE);
        assertNotNull(otp.getTime());
        assertEquals(otp.getTime(), TIME);

        otp.setCode(NEW_CODE);
        assertEquals(otp.getCode(), NEW_CODE);
    }

    @Test
    public void setterTime() {
        OTP otp = new OTP(ID, EMAIL_OR_PHONE, CODE, TIME);
        assertNotNull(otp);
        assertNotNull(otp.getId());
        assertNotNull(otp.getEmailOrPhone());
        assertEquals(otp.getEmailOrPhone(), EMAIL_OR_PHONE);
        assertNotNull(otp.getCode());
        assertEquals(otp.getCode(), CODE);
        assertNotNull(otp.getTime());
        assertEquals(otp.getTime(), TIME);

        otp.setTime(NEW_TIME);
        assertEquals(otp.getTime(), NEW_TIME);
    }
}