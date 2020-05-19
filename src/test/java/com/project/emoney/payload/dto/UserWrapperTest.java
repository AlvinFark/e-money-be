package com.project.emoney.payload.dto;

import com.project.emoney.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserWrapperTest {

    private static final long ID = 1;
    private static final String NAME = "Abigail";
    private static final String EMAIL = "abigail@mail.com";
    private static final String PHONE = "6282272068810";
    private static final String PASSWORD = "Ab19@iil";
    private static final long BALANCE = 100000;
    private static final boolean ACTIVE = true;

    public User getUser() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
        return user;
    }

    @Test
    public void constructorTest() {
        User user = getUser();
        UserWrapper userWrapper = new UserWrapper(user);
        assertNotNull(userWrapper);
        assertNotNull(userWrapper.getId());
        assertNotNull(userWrapper.getName());
        assertEquals(userWrapper.getName(), user.getName());
        assertNotNull(userWrapper.getEmail());
        assertEquals(userWrapper.getEmail(), user.getEmail());
        assertNotNull(userWrapper.getPhone());
        assertEquals(userWrapper.getPhone(), user.getPhone());
        assertNotNull(userWrapper.getPassword());
        assertEquals(userWrapper.getPassword(), user.getPassword());
        assertNotNull(userWrapper.getBalance());
        assertEquals(userWrapper.getBalance(), user.getBalance());
        assertNotNull(userWrapper.isActive());
        assertEquals(userWrapper.isActive(), user.isActive());
    }
}
