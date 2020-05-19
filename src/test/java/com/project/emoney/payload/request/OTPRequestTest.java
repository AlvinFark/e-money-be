package com.project.emoney.payload.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OTPRequestTest {

    private static final String EMAIL_OR_PHONE = "abigail@mail.com";
    private static final String CODE = "3456";

    @Test
    public void constructorTest() {
        OTPRequest otpRequest = new OTPRequest(EMAIL_OR_PHONE, CODE);
        assertNotNull(otpRequest);
        assertNotNull(otpRequest.getEmailOrPhone());
        assertEquals(otpRequest.getEmailOrPhone(), EMAIL_OR_PHONE);
        assertNotNull(otpRequest.getCode());
        assertEquals(otpRequest.getCode(), CODE);
    }
}