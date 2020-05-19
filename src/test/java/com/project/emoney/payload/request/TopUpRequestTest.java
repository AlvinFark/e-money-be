package com.project.emoney.payload.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TopUpRequestTest {

    private static final long VALUE = 25000l;
    private static final String NUMBER = "6282272068810";

    @Test
    public void constructorTest() {
        TopUpRequest topUpRequest = new TopUpRequest(NUMBER, VALUE);
        assertNotNull(topUpRequest);
        assertNotNull(topUpRequest.getNumber());
        assertEquals(topUpRequest.getNumber(), NUMBER);
        assertNotNull(topUpRequest.getValue());
        assertEquals(topUpRequest.getValue(), VALUE);
    }
}
