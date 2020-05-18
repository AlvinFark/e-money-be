package com.project.emoney.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.entity.TransactionMethod;
import com.project.emoney.entity.User;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.service.AsyncServiceAdapter;
import com.project.emoney.service.TransactionService;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TransactionWorkerTest {

  @Mock
  TransactionService transactionService;

  @Mock
  AsyncServiceAdapter asyncServiceAdapter;

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
  public void createTransactionNonWallet() throws Exception {
    final TransactionRequest transactionRequest = getTransactionRequestWallet();
    transactionRequest.setMethod(TransactionMethod.BANK);
    transactionRequest.setIdTopUpOption(1L);
    final User user = getUser();
    when(asyncServiceAdapter.getUserByEmail(email)).thenReturn(CompletableFuture.completedFuture(user));
    when(asyncServiceAdapter.getTopUpOptionById(1L)).thenReturn(CompletableFuture.completedFuture(getTopUpOption()));
    assert transactionWorker.createTransaction(objectMapper.writeValueAsString(transactionRequest)).equals("success");
  }
}
