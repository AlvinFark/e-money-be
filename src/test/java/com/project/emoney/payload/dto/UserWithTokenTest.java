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
public class UserWithTokenTest {

    private static final long ID = 1;
    private static final String NAME = "Abigail";
    private static final String EMAIL = "abigail@mail.com";
    private static final String PHONE = "6282272068810";
    private static final String PASSWORD = "Ab19@iil";
    private static final long BALANCE = 100000;
    private static final boolean ACTIVE = true;
    private static final String TOKEN = "6AXW10QFADO09ZYJ0WAQ";
    private static final String NEW_TOKEN = "VF0VM3JAI0XUOVZ5KNRP";


    public User getUser() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
        return user;
    }

    @Test
    public void constructorTest() {
        User user = getUser();
        UserWithToken userWithToken = new UserWithToken(user, TOKEN);
        assertNotNull(userWithToken);
        assertNotNull(userWithToken.getId());
        assertNotNull(userWithToken.getName());
        assertEquals(userWithToken.getName(), user.getName());
        assertNotNull(userWithToken.getEmail());
        assertEquals(userWithToken.getEmail(), user.getEmail());
        assertNotNull(userWithToken.getPhone());
        assertEquals(userWithToken.getPhone(), user.getPhone());
        assertNotNull(userWithToken.getPassword());
        assertEquals(userWithToken.getPassword(), user.getPassword());
        assertNotNull(userWithToken.getBalance());
        assertEquals(userWithToken.getBalance(), user.getBalance());
        assertNotNull(userWithToken.isActive());
        assertEquals(userWithToken.isActive(), user.isActive());
        assertNotNull(userWithToken.getToken());
        assertEquals(userWithToken.getToken(), TOKEN);
    }

    @Test
    public void constructorActiveFalseTest() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, false);
        UserWithToken userWithToken = new UserWithToken(user, TOKEN);
        assertNotNull(userWithToken);
        assertNotNull(userWithToken.getId());
        assertNotNull(userWithToken.getName());
        assertEquals(userWithToken.getName(), user.getName());
        assertNotNull(userWithToken.getEmail());
        assertEquals(userWithToken.getEmail(), user.getEmail());
        assertNotNull(userWithToken.getPhone());
        assertEquals(userWithToken.getPhone(), user.getPhone());
        assertNotNull(userWithToken.getPassword());
        assertEquals(userWithToken.getPassword(), user.getPassword());
        assertNotNull(userWithToken.getBalance());
        assertEquals(userWithToken.getBalance(), user.getBalance());
        assertNotNull(userWithToken.isActive());
        assertEquals(userWithToken.isActive(), user.isActive());
        assertNotNull(userWithToken.getToken());
        assertEquals(userWithToken.getToken(), TOKEN);
    }

    @Test
    public void setterTokenTest() {
        User user = getUser();
        UserWithToken userWithToken = new UserWithToken(user, TOKEN);
        assertNotNull(userWithToken);
        assertNotNull(userWithToken.getId());
        assertNotNull(userWithToken.getName());
        assertEquals(userWithToken.getName(), user.getName());
        assertNotNull(userWithToken.getEmail());
        assertEquals(userWithToken.getEmail(), user.getEmail());
        assertNotNull(userWithToken.getPhone());
        assertEquals(userWithToken.getPhone(), user.getPhone());
        assertNotNull(userWithToken.getPassword());
        assertEquals(userWithToken.getPassword(), user.getPassword());
        assertNotNull(userWithToken.getBalance());
        assertEquals(userWithToken.getBalance(), user.getBalance());
        assertNotNull(userWithToken.isActive());
        assertEquals(userWithToken.isActive(), user.isActive());
        assertNotNull(userWithToken.getToken());
        assertEquals(userWithToken.getToken(), TOKEN);

        userWithToken.setToken(NEW_TOKEN);
        assertEquals(userWithToken.getToken(), NEW_TOKEN);
    }



}
