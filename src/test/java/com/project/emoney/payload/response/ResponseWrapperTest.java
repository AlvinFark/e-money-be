package com.project.emoney.payload.response;

import com.project.emoney.entity.User;
import com.project.emoney.payload.request.CancelRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ResponseWrapperTest {

    private static final int CODE = 201;
    private static final String MESSAGE = "User Registered Successfully";
    private static final long ID = 1;
    private static final String NAME = "Abigail";
    private static final String EMAIL = "abigail@mail.com";
    private static final String PHONE = "6282272068810";
    private static final String PASSWORD = "Ab19@iil";
    private static final long BALANCE = 100000;
    private static final boolean ACTIVE = false;


    public User getUser() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
        return user;
    }

    @Test
    public void constructorTest() {
        User user = getUser();
        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getName());
        assertEquals(user.getName(), NAME);
        assertNotNull(user.getEmail());
        assertEquals(user.getEmail(), EMAIL);
        assertNotNull(user.getPhone());
        assertEquals(user.getPhone(), PHONE);
        assertNotNull(user.getPassword());
        assertEquals(user.getPassword(), PASSWORD);
        assertNotNull(user.getBalance());
        assertEquals(user.getBalance(), BALANCE);
        assertNotNull(user.isActive());
        assertEquals(user.isActive(), ACTIVE);

        ResponseWrapper responseWrapper = new ResponseWrapper(CODE, MESSAGE, user);
        assertNotNull(responseWrapper);
        assertNotNull(responseWrapper.getCode());
        assertEquals(responseWrapper.getCode(), CODE);
        assertNotNull(responseWrapper.getMessage());
        assertEquals(responseWrapper.getMessage(), MESSAGE);
        assertNotNull(responseWrapper.getData());
        assertEquals(responseWrapper.getData(), user);
    }
}
