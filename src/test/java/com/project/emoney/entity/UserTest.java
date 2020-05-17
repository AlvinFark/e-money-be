package com.project.emoney.entity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    private static final long ID = 1;
    private static final String NAME = "Abigail";
    private static final String EMAIL = "abigail@mail.com";
    private static final String PHONE = "6282272068810";
    private static final String PASSWORD = "Ab19@iil";
    private static final long BALANCE = 100000;
    private static final boolean ACTIVE = false;


    @Test
    public void constructor() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
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
    }

    @Test
    public void setterName() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
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

        user.setName("Anderson");
        assertEquals(user.getName(), "Anderson");
    }

    @Test
    public void setterEmail() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
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

        user.setEmail("anderson@mail.com");
        assertEquals(user.getEmail(), "anderson@mail.com");
    }

    @Test
    public void setterPhone() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
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

        user.setPhone("6282272068812");
        assertEquals(user.getPhone(), "6282272068812");
    }

    @Test
    public void setterPassword() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
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

        user.setPassword("@nd3rs0N");
        assertEquals(user.getPassword(), "@nd3rs0N");
    }

    @Test
    public void setterActiveTrue() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, ACTIVE);
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

        user.setActive(true);
        assertEquals(user.isActive(), true);
    }

    @Test
    public void setterActiveFalse() {
        User user = new User(ID, NAME, EMAIL, PHONE, PASSWORD, BALANCE, true);
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
        assertEquals(user.isActive(), true);

        user.setActive(ACTIVE);
        assertEquals(user.isActive(), ACTIVE);
    }
}