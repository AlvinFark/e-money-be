package com.project.emoney.payload.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CancelRequestTest {

    private static final long ID = 1;
    private static final String USER_EMAIL = "abigail@mail.com";

    @Test
    public void constructorTest() {
        CancelRequest cancelRequest = new CancelRequest(ID, USER_EMAIL);
        assertNotNull(cancelRequest);
        assertNotNull(cancelRequest.getId());
        assertEquals(cancelRequest.getId(), ID);
        assertNotNull(cancelRequest.getUserEmail());
        assertEquals(cancelRequest.getUserEmail(), USER_EMAIL);
    }
}