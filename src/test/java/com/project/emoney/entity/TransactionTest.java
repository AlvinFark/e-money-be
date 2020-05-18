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
public class TransactionTest {

    private static final long ID = 1;
    private static final long USER_ID = 1;
    private static final String CARD_NUMBER = "1234567890";
    private static final long VALUE = 50000;
    private static final long FEE = 1500;
    private static final LocalDateTime TIME = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS);
    private static final LocalDateTime EXPIRY = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS+GlobalVariable.TRANSACTION_LIFETIME_HOURS);
    private static final String IMAGE_PATH = "/image";

    @Test
    public void constructor() {
        Transaction transaction = new Transaction(ID, USER_ID, CARD_NUMBER, VALUE, FEE,
                Status.IN_PROGRESS, TransactionMethod.WALLET, TIME, EXPIRY, IMAGE_PATH);
        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getUserId());
        assertEquals(transaction.getUserId(), USER_ID);
        assertNotNull(transaction.getCardNumber());
        assertEquals(transaction.getCardNumber(), CARD_NUMBER);
        assertNotNull(transaction.getValue());
        assertEquals(transaction.getValue(), VALUE);
        assertNotNull(transaction.getFee());
        assertEquals(transaction.getFee(), FEE);
        assertNotNull(transaction.getStatus());
        assertEquals(transaction.getStatus(), Status.IN_PROGRESS);
        assertNotNull(transaction.getMethod());
        assertEquals(transaction.getMethod(), TransactionMethod.WALLET);
        assertNotNull(transaction.getTime());
        assertEquals(transaction.getTime(), TIME);
        assertNotNull(transaction.getExpiry());
        assertEquals(transaction.getExpiry(), EXPIRY);
        assertNotNull(transaction.getImagePath());
        assertEquals(transaction.getImagePath(), IMAGE_PATH);
    }

    @Test
    public void setterStatusCompleted() {
        Transaction transaction = new Transaction(ID, USER_ID, CARD_NUMBER, VALUE, FEE,
                Status.IN_PROGRESS, TransactionMethod.WALLET, TIME, EXPIRY, IMAGE_PATH);
        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getUserId());
        assertEquals(transaction.getUserId(), USER_ID);
        assertNotNull(transaction.getCardNumber());
        assertEquals(transaction.getCardNumber(), CARD_NUMBER);
        assertNotNull(transaction.getValue());
        assertEquals(transaction.getValue(), VALUE);
        assertNotNull(transaction.getFee());
        assertEquals(transaction.getFee(), FEE);
        assertNotNull(transaction.getStatus());
        assertEquals(transaction.getStatus(), Status.IN_PROGRESS);
        assertNotNull(transaction.getMethod());
        assertEquals(transaction.getMethod(), TransactionMethod.WALLET);
        assertNotNull(transaction.getTime());
        assertEquals(transaction.getTime(), TIME);
        assertNotNull(transaction.getExpiry());
        assertEquals(transaction.getExpiry(), EXPIRY);
        assertNotNull(transaction.getImagePath());
        assertEquals(transaction.getImagePath(), IMAGE_PATH);

        transaction.setStatus(Status.COMPLETED);
        assertEquals(transaction.getStatus(), Status.COMPLETED);
    }

    @Test
    public void setterStatusFailed() {
        Transaction transaction = new Transaction(ID, USER_ID, CARD_NUMBER, VALUE, FEE,
                Status.IN_PROGRESS, TransactionMethod.WALLET, TIME, EXPIRY, IMAGE_PATH);
        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getUserId());
        assertEquals(transaction.getUserId(), USER_ID);
        assertNotNull(transaction.getCardNumber());
        assertEquals(transaction.getCardNumber(), CARD_NUMBER);
        assertNotNull(transaction.getValue());
        assertEquals(transaction.getValue(), VALUE);
        assertNotNull(transaction.getFee());
        assertEquals(transaction.getFee(), FEE);
        assertNotNull(transaction.getStatus());
        assertEquals(transaction.getStatus(), Status.IN_PROGRESS);
        assertNotNull(transaction.getMethod());
        assertEquals(transaction.getMethod(), TransactionMethod.WALLET);
        assertNotNull(transaction.getTime());
        assertEquals(transaction.getTime(), TIME);
        assertNotNull(transaction.getExpiry());
        assertEquals(transaction.getExpiry(), EXPIRY);
        assertNotNull(transaction.getImagePath());
        assertEquals(transaction.getImagePath(), IMAGE_PATH);

        transaction.setStatus(Status.FAILED);
        assertEquals(transaction.getStatus(), Status.FAILED);
    }

    @Test
    public void setterStatusCancelled() {
        Transaction transaction = new Transaction(ID, USER_ID, CARD_NUMBER, VALUE, FEE,
                Status.IN_PROGRESS, TransactionMethod.WALLET, TIME, EXPIRY, IMAGE_PATH);
        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getUserId());
        assertEquals(transaction.getUserId(), USER_ID);
        assertNotNull(transaction.getCardNumber());
        assertEquals(transaction.getCardNumber(), CARD_NUMBER);
        assertNotNull(transaction.getValue());
        assertEquals(transaction.getValue(), VALUE);
        assertNotNull(transaction.getFee());
        assertEquals(transaction.getFee(), FEE);
        assertNotNull(transaction.getStatus());
        assertEquals(transaction.getStatus(), Status.IN_PROGRESS);
        assertNotNull(transaction.getMethod());
        assertEquals(transaction.getMethod(), TransactionMethod.WALLET);
        assertNotNull(transaction.getTime());
        assertEquals(transaction.getTime(), TIME);
        assertNotNull(transaction.getExpiry());
        assertEquals(transaction.getExpiry(), EXPIRY);
        assertNotNull(transaction.getImagePath());
        assertEquals(transaction.getImagePath(), IMAGE_PATH);

        transaction.setStatus(Status.CANCELLED);
        assertEquals(transaction.getStatus(), Status.CANCELLED);
    }

    @Test
    public void setterImagePath() {
        Transaction transaction = new Transaction(ID, USER_ID, CARD_NUMBER, VALUE, FEE,
                Status.IN_PROGRESS, TransactionMethod.WALLET, TIME, EXPIRY, "");
        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getUserId());
        assertEquals(transaction.getUserId(), USER_ID);
        assertNotNull(transaction.getCardNumber());
        assertEquals(transaction.getCardNumber(), CARD_NUMBER);
        assertNotNull(transaction.getValue());
        assertEquals(transaction.getValue(), VALUE);
        assertNotNull(transaction.getFee());
        assertEquals(transaction.getFee(), FEE);
        assertNotNull(transaction.getStatus());
        assertEquals(transaction.getStatus(), Status.IN_PROGRESS);
        assertNotNull(transaction.getMethod());
        assertEquals(transaction.getMethod(), TransactionMethod.WALLET);
        assertNotNull(transaction.getTime());
        assertEquals(transaction.getTime(), TIME);
        assertNotNull(transaction.getExpiry());
        assertEquals(transaction.getExpiry(), EXPIRY);
        assertNotNull(transaction.getImagePath());
        assertEquals(transaction.getImagePath(), "");

        transaction.setImagePath(IMAGE_PATH);
        assertEquals(transaction.getImagePath(), IMAGE_PATH);
    }
}
