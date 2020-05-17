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
public class EmailTokenTest {

    private static final long ID = 1;
    private static final long USER_ID = 1;
    private static final String TOKEN = "6AXW10QFADO09ZYJ0WAQ";
    private static final String NEW_TOKEN = "VF0VM3JAI0XUOVZ5KNRP";
    private static final LocalDateTime TIME = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS);
    private static final LocalDateTime NEW_TIME = LocalDateTime.now().plusHours(8);


    @Test
    public void constructor() {
        EmailToken emailToken = new EmailToken(ID, USER_ID, TOKEN, TIME);
        assertNotNull(emailToken);
        assertNotNull(emailToken.getId());
        assertNotNull(emailToken.getUserId());
        assertEquals(emailToken.getUserId(), USER_ID);
        assertNotNull(emailToken.getToken());
        assertEquals(emailToken.getToken(), TOKEN);
        assertNotNull(emailToken.getTime());
        assertEquals(emailToken.getTime(), TIME);
    }

    @Test
    public void setterToken() {
        EmailToken emailToken = new EmailToken(ID, USER_ID, TOKEN, TIME);
        assertNotNull(emailToken);
        assertNotNull(emailToken.getId());
        assertNotNull(emailToken.getUserId());
        assertEquals(emailToken.getUserId(), USER_ID);
        assertNotNull(emailToken.getToken());
        assertEquals(emailToken.getToken(), TOKEN);
        assertNotNull(emailToken.getTime());
        assertEquals(emailToken.getTime(), TIME);

        emailToken.setToken(NEW_TOKEN);
        assertEquals(emailToken.getToken(), NEW_TOKEN);
    }

    @Test
    public void setterTime() {
        EmailToken emailToken = new EmailToken(ID, USER_ID, TOKEN, TIME);
        assertNotNull(emailToken);
        assertNotNull(emailToken.getId());
        assertNotNull(emailToken.getUserId());
        assertEquals(emailToken.getUserId(), USER_ID);
        assertNotNull(emailToken.getToken());
        assertEquals(emailToken.getToken(), TOKEN);
        assertNotNull(emailToken.getTime());
        assertEquals(emailToken.getTime(), TIME);

        emailToken.setTime(NEW_TIME);
        assertEquals(emailToken.getTime(), NEW_TIME);
    }


}