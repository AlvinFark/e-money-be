package com.project.emoney.payload.request;

import com.project.emoney.entity.TransactionMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionRequestTest {

    private static final String EMAIL = "abigail@mail.com";
    private static final String CARD_NUMBER = "1234567890";
    private static final long ID_TOP_UP_OPTION = 1;
    private static final TransactionMethod METHOD = TransactionMethod.WALLET;



    @Test
    public void constructorTest() {
        TransactionRequest transactionRequest = new TransactionRequest(CARD_NUMBER, ID_TOP_UP_OPTION, METHOD, EMAIL);
        assertNotNull(transactionRequest);
        assertNotNull(transactionRequest.getCardNumber());
        assertEquals(transactionRequest.getCardNumber(), CARD_NUMBER);
        assertNotNull(transactionRequest.getIdTopUpOption());
        assertEquals(transactionRequest.getIdTopUpOption(), ID_TOP_UP_OPTION);
        assertNotNull(transactionRequest.getMethod());
        assertEquals(transactionRequest.getMethod(), METHOD);
        assertNotNull(transactionRequest.getEmail());
        assertEquals(transactionRequest.getEmail(), EMAIL);
    }
}
