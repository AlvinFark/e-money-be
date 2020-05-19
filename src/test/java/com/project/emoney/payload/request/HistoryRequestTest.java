package com.project.emoney.payload.request;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HistoryRequestTest {

    private static final int PAGE = 1;
    private static final String USERNAME = "abigail@mail.com";

    @Test
    public void constructorTest() {
        HistoryRequest historyRequest = new HistoryRequest(USERNAME, PAGE);
        assertNotNull(historyRequest);
        assertNotNull(historyRequest.getUsername());
        assertEquals(historyRequest.getUsername(), USERNAME);
        assertNotNull(historyRequest.getPage());
        assertEquals(historyRequest.getPage(), PAGE);
    }
}