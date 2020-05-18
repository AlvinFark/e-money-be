package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.TransactionMethod;
import com.project.emoney.entity.User;
import com.project.emoney.payload.dto.TransactionDTO;
import com.project.emoney.service.TransactionService;
import com.project.emoney.service.UserService;
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
public class PaymentWorkerTest {

  @Mock
  UserService userService;

  @Mock
  TransactionService transactionService;

  ObjectMapper objectMapper = new ObjectMapper();

  @InjectMocks
  PaymentWorker paymentWorker;

  private Transaction getTransaction(){
    return new Transaction(1,1,"8799274627849284",10000,1000,
        Status.IN_PROGRESS, TransactionMethod.MERCHANT, LocalDateTime.now(),
        LocalDateTime.now().plusDays(1),"");
  }

  private List<Transaction> getListTransaction(){
    List<Transaction> transactions = new ArrayList<>();
    Transaction transaction = getTransaction();
    transactions.add(transaction);
    Transaction transaction1 = getTransaction();
    transaction.setId(2);
    transactions.add(transaction1);
    Transaction transaction2 = getTransaction();
    transaction2.setId(3);
    transaction2.setExpiry(LocalDateTime.now().minusMinutes(1));
    return transactions;
  }

  private User getUser(){
    User user = new  User();
    user.setId(1);
    user.setName("Dwight Schrute");
    user.setEmail("dwight@dundermifflin.co");
    user.setPhone("628595839538");
    user.setPassword("password");
    user.setBalance(0);
    user.setActive(false);
    return user;
  }

  @Test
  public void invalidCardTest() throws JsonProcessingException {
    assert paymentWorker.getMerchantTransaction("111108598727648").equals("invalid virtual account number");
  }

  @Test
  public void userNotfoundCardTest() throws JsonProcessingException {
    when(userService.getByEmailOrPhone("08598727648")).thenReturn(null);
    assert paymentWorker.getMerchantTransaction("800008598727648").equals("user not found");
  }

  @Test
  public void getListTransactionCardTest() throws JsonProcessingException {
    User user = getUser();
    List<Transaction> transactions = getListTransaction();
    when(userService.getByEmailOrPhone("08598727648")).thenReturn(user);
    when(transactionService.getInProgressAndMerchantByUserId(1)).thenReturn(transactions);
    List<TransactionDTO> transactionDTOS = new ArrayList<>();
    transactionDTOS.add(new TransactionDTO(transactions.get(0)));
    transactionDTOS.add(new TransactionDTO(transactions.get(1)));
    assert paymentWorker.getMerchantTransaction("800008598727648").equals(objectMapper.writeValueAsString(transactionDTOS));
  }

  @Test
  public void transactionNotFoundTest() throws JsonProcessingException {
    when(transactionService.getById(1)).thenReturn(null);
    assert paymentWorker.confirm("1").equals("transaction not found");
  }

  @Test
  public void transactionCompletedTest() throws JsonProcessingException {
    Transaction transaction = getTransaction();
    transaction.setStatus(Status.FAILED);
    when(transactionService.getById(1)).thenReturn(transaction);
    assert paymentWorker.confirm("1").equals("can't confirm completed transaction");
  }
}
