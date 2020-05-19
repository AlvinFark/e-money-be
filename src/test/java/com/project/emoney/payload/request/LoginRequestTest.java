package com.project.emoney.payload.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginRequestTest {

    private static final String EMAIL_OR_PHONE = "6282272068810";
    private static final String PASSWORD = "Abig4il!";

    @Test
    public void constructorTest() {
        LoginRequest loginRequest = new LoginRequest(EMAIL_OR_PHONE, PASSWORD);
        assertNotNull(loginRequest);
        assertNotNull(loginRequest.getEmailOrPhone());
        assertEquals(loginRequest.getEmailOrPhone(), EMAIL_OR_PHONE);
        assertNotNull(loginRequest.getPassword());
        assertEquals(loginRequest.getPassword(), PASSWORD);
    }
}