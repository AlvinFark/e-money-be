package com.project.emoney.service;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.User;
import com.project.emoney.payload.request.TransactionRequest;

import java.util.List;

public interface TransactionService {
    Transaction getById(long id);
    List<Transaction> getInProgressByUserId(long id);
    List<Transaction> getAllByUserId(long id);
    List<Transaction> getInProgressAndMerchantByUserId(long id);

    void insert(Transaction transaction);
    void updateStatusById(long id, Status status);
    void setExtensionAndStatusById(long id, String extension, Status status);
    void insertByTransactionRequestAndUserAndTopUpOptionAndStatus(TransactionRequest transactionRequest, User user, TopUpOption topUpOption, Status status);
}
