package com.project.emoney.service;

import com.project.emoney.entity.*;
import com.project.emoney.mapper.TransactionMapper;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.service.impl.TransactionServiceImpl;
import com.project.emoney.utils.GlobalVariable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    private static final long ID = 1;
    private static final long USER_ID = 1;
    private static final String CARD_NUMBER = "1234567890";
    private static final long VALUE = 50000;
    private static final long FEE = 1500;
    private static final LocalDateTime TIME = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS);
    private static final LocalDateTime EXPIRY = LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS+GlobalVariable.TRANSACTION_LIFETIME_HOURS);
    private static final String IMAGE_PATH = "/image";

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    public Transaction getTransaction() {
        Transaction transaction = new Transaction(ID, USER_ID, CARD_NUMBER, VALUE, FEE,
                Status.IN_PROGRESS, TransactionMethod.WALLET, TIME, EXPIRY, IMAGE_PATH);
        return transaction;
    }

    public TransactionRequest getTransactionRequest() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setIdTopUpOption(1);
        transactionRequest.setEmail("abigail@mail.com");
        transactionRequest.setCardNumber(CARD_NUMBER);
        transactionRequest.setMethod(TransactionMethod.WALLET);

        return transactionRequest;
    }

    public User getUser(){
        User user = new  User();
        user.setId(1);
        user.setName("Abigail");
        user.setEmail("abigail@mail.com");
        user.setPhone("628595839538");
        user.setPassword("password");
        user.setBalance(100000);
        user.setActive(true);
        return user;
    }

    public TopUpOption getTopUpOption() {
        TopUpOption topUpOption = new TopUpOption();
        topUpOption.setId(1);
        topUpOption.setValue(20000);
        topUpOption.setFee(1500);
        return topUpOption;
    }

    @Test
    public void getTransactionByIdTest() {
        final Transaction expected = getTransaction();
        when(transactionMapper.getById(ID)).thenReturn(expected);
        final Transaction transaction = transactionService.getById(ID);
        assert transaction.equals(expected);
    }

    @Test
    public void getInProgressAndMerchantByUserId() {
        List<Transaction> expected = new ArrayList<Transaction>();
        expected.add(getTransaction());
        when(transactionMapper.getInProgressAndMerchantByUserId(USER_ID)).thenReturn(expected);

        final List<Transaction> transactionList = transactionService.getInProgressAndMerchantByUserId(USER_ID);
        assert transactionList.equals(expected);
    }

    @Test
    public void getListInProgressByUserIdTest() {
        final List<Transaction> expected = new ArrayList<Transaction>();
        when(transactionMapper.getInProgressByUserId(USER_ID)).thenReturn(expected);

        final List<Transaction> transactionList = transactionService.getInProgressByUserId(USER_ID);
        assert transactionList.equals(expected);
    }

    @Test
    public void getListAllByUserIdTest() {
        final List<Transaction> expected = new ArrayList<Transaction>();
        when(transactionMapper.getAllByUserId(USER_ID)).thenReturn(expected);

        final List<Transaction> transactionList = transactionService.getAllByUserId(USER_ID);
        assert transactionList.equals(expected);
    }

    @Test
    public void insertTransactionTest() {
        final Transaction transaction = getTransaction();
        transactionService.insert(transaction);
        verify(transactionMapper, times(1)).insert(transaction);
    }

    @Test
    public void updateStatusByIdTest() {
        final Status status = Status.COMPLETED;
        transactionService.updateStatusById(ID, status);
        verify(transactionMapper, times(1)).setStatusById(ID, status);
    }

    @Test
    public void setExtensionAndStatusByIdTest() {
        final Status status = Status.COMPLETED;
        transactionService.setExtensionAndStatusById(ID, IMAGE_PATH, status);
        verify(transactionMapper, times(1)).setExtensionById(ID, IMAGE_PATH, status);
    }


    @Test
    public void insertByTransactionRequestAndUserAndTopUpOptionAndStatusTest() {
        final Status status = Status.IN_PROGRESS;
        final TransactionRequest request = getTransactionRequest();
        final User user = getUser();
        final TopUpOption topUpOption = getTopUpOption();
        transactionService.insertByTransactionRequestAndUserAndTopUpOptionAndStatus(request, user, topUpOption, status);
        final Transaction transaction = new Transaction(user.getId(), request.getCardNumber(), topUpOption.getValue(),
                topUpOption.getFee(), status, request.getMethod(), TIME, EXPIRY);
        transactionService.insert(transaction);
        verify(transactionMapper, times(1)).insert(transaction);
    }
}
