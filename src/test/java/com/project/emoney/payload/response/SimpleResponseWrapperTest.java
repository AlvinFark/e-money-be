package com.project.emoney.payload.response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleResponseWrapperTest {

    private static final int CODE = 404;
    private static final String MESSAGE = "Not Found";

    @Test
    public void constructorTest() {
        SimpleResponseWrapper simpleResponseWrapper = new SimpleResponseWrapper(CODE, MESSAGE);
        assertNotNull(simpleResponseWrapper);
        assertNotNull(simpleResponseWrapper.getCode());
        assertEquals(simpleResponseWrapper.getCode(), CODE);
        assertNotNull(simpleResponseWrapper.getMessage());
        assertEquals(simpleResponseWrapper.getMessage(), MESSAGE);
    }
}

