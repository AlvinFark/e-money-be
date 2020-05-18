package com.project.emoney.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.*;
import com.project.emoney.payload.dto.TransactionDTO;
import com.project.emoney.payload.request.CancelRequest;
import com.project.emoney.payload.request.HistoryRequest;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.service.AsyncServiceAdapter;
import com.project.emoney.service.TransactionService;
import com.project.emoney.service.UserService;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TransactionWorkerTest {

  @Mock
  TransactionService transactionService;

  @Mock
  AsyncServiceAdapter asyncServiceAdapter;

  @Mock
  UserService userService;

  @InjectMocks
  TransactionWorker transactionWorker;

  ObjectMapper objectMapper = new ObjectMapper();
  public static String email = "dwight@dundermifflin.co";
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  public User getUser(){
    User user = new  User();
    user.setId(1);
    user.setName("Dwight Schrute");
    user.setEmail(email);
    user.setPhone("628595839538");
    user.setPassword("password");
    user.setBalance(100000);
    user.setActive(true);
    return user;
  }

  public TransactionRequest getTransactionRequestWallet() {
    return new TransactionRequest("8768947295812759", 1, TransactionMethod.WALLET, email);
  }

  public TopUpOption getTopUpOption() {
    return new TopUpOption(1L, 10000L, 1000L);
  }

  private Transaction getTransaction(){
    return new Transaction(1,1,"8799274627849284",10000,1000,
        Status.IN_PROGRESS, TransactionMethod.MERCHANT, LocalDateTime.now(),
        LocalDateTime.now().plusDays(1),"");
  }

  private List<Transaction> getListTransactionInProgress(){
    List<Transaction> transactions = new ArrayList<>();
    Transaction transaction = getTransaction();
    transactions.add(transaction);
    Transaction transaction1 = getTransaction();
    transaction.setId(2);
    transaction.setMethod(TransactionMethod.BANK);
    transaction.setStatus(Status.VERIFYING);
    transactions.add(transaction1);
    Transaction transaction2 = getTransaction();
    transaction2.setId(3);
    transaction2.setExpiry(LocalDateTime.now().minusMinutes(1));
    return transactions;
  }

  @Test
  public void createTransactionTopUpOptionNotFoundTest() throws Exception {
    final TransactionRequest transactionRequest = getTransactionRequestWallet();
    transactionRequest.setIdTopUpOption(0L);
    final User user = getUser();
    when(asyncServiceAdapter.getUserByEmail(email)).thenReturn(CompletableFuture.completedFuture(user));
    when(asyncServiceAdapter.getTopUpOptionById(0L)).thenReturn(CompletableFuture.completedFuture(null));
    assert transactionWorker.createTransaction(objectMapper.writeValueAsString(transactionRequest)).equals("top up option not found");
  }

  @Test
  public void createTransactionWalletNotEnoughBalanceTest() throws Exception {
    final TransactionRequest transactionRequest = getTransactionRequestWallet();
    final User user = getUser();
    user.setBalance(10000);
    final TopUpOption topUpOption = getTopUpOption();
    when(asyncServiceAdapter.getUserByEmail(email)).thenReturn(CompletableFuture.completedFuture(user));
    when(asyncServiceAdapter.getTopUpOptionById(1L)).thenReturn(CompletableFuture.completedFuture(topUpOption));
    assert transactionWorker.createTransaction(objectMapper.writeValueAsString(transactionRequest)).equals("not enough balance");
  }

  @Test
  public void createTransactionNonWalletTest() throws Exception {
    final TransactionRequest transactionRequest = getTransactionRequestWallet();
    transactionRequest.setMethod(TransactionMethod.BANK);
    transactionRequest.setIdTopUpOption(1L);
    final User user = getUser();
    when(asyncServiceAdapter.getUserByEmail(email)).thenReturn(CompletableFuture.completedFuture(user));
    when(asyncServiceAdapter.getTopUpOptionById(1L)).thenReturn(CompletableFuture.completedFuture(getTopUpOption()));
    assert transactionWorker.createTransaction(objectMapper.writeValueAsString(transactionRequest)).equals("success");
  }

  @Test
  public void transactionNotFoundCancelTest() throws JsonProcessingException {
    CancelRequest cancelRequest = new CancelRequest(1, email);
    User user = getUser();
    when(transactionService.getById(1)).thenReturn(null);
    when(userService.getByEmail(email)).thenReturn(user);
    assert transactionWorker.cancel(objectMapper.writeValueAsString(cancelRequest)).equals("transaction not found");
  }

  @Test
  public void notYourTransactionCancelTest() throws JsonProcessingException {
    CancelRequest cancelRequest = new CancelRequest(1, email);
    User user = getUser();
    Transaction transaction = getTransaction();
    transaction.setUserId(2);
    when(transactionService.getById(1)).thenReturn(transaction);
    when(userService.getByEmail(email)).thenReturn(user);
    assert transactionWorker.cancel(objectMapper.writeValueAsString(cancelRequest)).equals("transaction belong to another user");
  }

  @Test
  public void successCancelTest() throws JsonProcessingException {
    CancelRequest cancelRequest = new CancelRequest(1, email);
    User user = getUser();
    Transaction transaction = getTransaction();
    when(transactionService.getById(1)).thenReturn(transaction);
    when(userService.getByEmail(email)).thenReturn(user);
    assert transactionWorker.cancel(objectMapper.writeValueAsString(cancelRequest)).equals("success");
  }

  @Test
  public void completedTransactionCancelTest() throws JsonProcessingException {
    CancelRequest cancelRequest = new CancelRequest(1, email);
    User user = getUser();
    Transaction transaction = getTransaction();
    transaction.setStatus(Status.FAILED);
    when(transactionService.getById(1)).thenReturn(transaction);
    when(userService.getByEmail(email)).thenReturn(user);
    assert transactionWorker.cancel(objectMapper.writeValueAsString(cancelRequest)).equals("can't cancel completed transaction");
  }

  @Test
  public void transactionInProgressTest() throws JsonProcessingException {
    HistoryRequest historyRequest = new HistoryRequest(email,1);
    HistoryRequest historyRequest2 = new HistoryRequest(email,2);
    User user = getUser();
    when(userService.getByEmail(email)).thenReturn(user);
    List<Transaction> transactions = getListTransactionInProgress();
    when(transactionService.getInProgressByUserId(1)).thenReturn(transactions);
    List<TransactionDTO> transactionDTOS = new ArrayList<>();
    transactionDTOS.add(new TransactionDTO(transactions.get(0)));
    transactionDTOS.add(new TransactionDTO(transactions.get(1)));
    assert transactionWorker.transactionInProgress(objectMapper.writeValueAsString(historyRequest))
        .equals(objectMapper.writeValueAsString(transactionDTOS));
    assert transactionWorker.transactionInProgress(objectMapper.writeValueAsString(historyRequest2))
        .equals(objectMapper.writeValueAsString(new ArrayList<TransactionDTO>()));
  }
}
