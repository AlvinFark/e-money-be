package com.project.emoney.payload.dto;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.TransactionMethod;
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
public class TransactionDTOTest {

    private static final long ID = 1;
    private static final long USER_ID = 1;
    private static final String CARD_NUMBER = "1234567890";
    private static final long VALUE = 50000;
    private static final long FEE = 1500;
    private static final LocalDateTime TIME = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS);
    private static final LocalDateTime EXPIRY = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS+GlobalVariable.TRANSACTION_LIFETIME_HOURS);
    private static final String IMAGE_PATH = "/image";

    public Transaction getTransaction() {
        Transaction transaction = new Transaction(ID, USER_ID, CARD_NUMBER, VALUE, FEE,
                Status.IN_PROGRESS, TransactionMethod.WALLET, TIME, EXPIRY, IMAGE_PATH);
        return transaction;
    }

    @Test
    public void constructorTest() {
        Transaction transaction = getTransaction();
        TransactionDTO transactionDTO = new TransactionDTO(transaction);
        assertNotNull(transactionDTO);
        assertNotNull(transactionDTO.getId());
        assertNotNull(transactionDTO.getUserId());
        assertEquals(transactionDTO.getUserId(), transaction.getUserId());
        assertNotNull(transactionDTO.getCardNumber());
        assertEquals(transactionDTO.getCardNumber(), transaction.getCardNumber());
        assertNotNull(transactionDTO.getValue());
        assertEquals(transactionDTO.getValue(), transaction.getValue());
        assertNotNull(transactionDTO.getFee());
        assertEquals(transactionDTO.getFee(), transaction.getFee());
        assertNotNull(transactionDTO.getStatus());
        assertEquals(transactionDTO.getStatus(), transaction.getStatus());
        assertNotNull(transactionDTO.getMethod());
        assertEquals(transactionDTO.getMethod(), transaction.getMethod());
    }

    @Test
    public void setterTimeTest() {
        Transaction transaction = getTransaction();
        TransactionDTO transactionDTO = new TransactionDTO(transaction);
        assertNotNull(transactionDTO);
        assertNotNull(transactionDTO.getId());
        assertNotNull(transactionDTO.getUserId());
        assertEquals(transactionDTO.getUserId(), transaction.getUserId());
        assertNotNull(transactionDTO.getCardNumber());
        assertEquals(transactionDTO.getCardNumber(), transaction.getCardNumber());
        assertNotNull(transactionDTO.getValue());
        assertEquals(transactionDTO.getValue(), transaction.getValue());
        assertNotNull(transactionDTO.getFee());
        assertEquals(transactionDTO.getFee(), transaction.getFee());
        assertNotNull(transactionDTO.getStatus());
        assertEquals(transactionDTO.getStatus(), transaction.getStatus());
        assertNotNull(transactionDTO.getMethod());
        assertEquals(transactionDTO.getMethod(), transaction.getMethod());

        String new_time = transaction.getTime().toString().substring(0,10)+' '+transaction.getTime().toString().substring(11);
        transactionDTO.setTime(new_time);
        assertEquals(transactionDTO.getTime(), new_time);
    }

    @Test
    public void setterExpiryTest() {
        Transaction transaction = getTransaction();
        TransactionDTO transactionDTO = new TransactionDTO(transaction);
        assertNotNull(transactionDTO);
        assertNotNull(transactionDTO.getId());
        assertNotNull(transactionDTO.getUserId());
        assertEquals(transactionDTO.getUserId(), transaction.getUserId());
        assertNotNull(transactionDTO.getCardNumber());
        assertEquals(transactionDTO.getCardNumber(), transaction.getCardNumber());
        assertNotNull(transactionDTO.getValue());
        assertEquals(transactionDTO.getValue(), transaction.getValue());
        assertNotNull(transactionDTO.getFee());
        assertEquals(transactionDTO.getFee(), transaction.getFee());
        assertNotNull(transactionDTO.getStatus());
        assertEquals(transactionDTO.getStatus(), transaction.getStatus());
        assertNotNull(transactionDTO.getMethod());
        assertEquals(transactionDTO.getMethod(), transaction.getMethod());

        String new_expiry = transaction.getExpiry().toString().substring(0,10)+' '+transaction.getExpiry().toString().substring(11);
        transactionDTO.setExpiry(new_expiry);
        assertEquals(transactionDTO.getExpiry(), new_expiry);
    }
}
