package com.project.emoney.service.impl;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.TopUpOption;
import com.project.emoney.entity.Transaction;
import com.project.emoney.entity.User;
import com.project.emoney.mapper.TransactionMapper;
import com.project.emoney.payload.request.TransactionRequest;
import com.project.emoney.service.TransactionService;
import com.project.emoney.utils.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public Transaction getById(long id) {
        return transactionMapper.getById(id);
    }

    @Override
    public List<Transaction> getInProgressByUserId(long id) {
        return transactionMapper.getInProgressByUserId(id);
    }

    @Override
    public List<Transaction> getAllByUserId(long id) {
        return transactionMapper.getAllByUserId(id);
    }

    @Override
    public void insert(Transaction transaction) {
        transactionMapper.insert(transaction);
    }

    @Override
    public void updateStatusById(long id, Status status) {
        transactionMapper.setStatusById(id, status);
    }

    @Override
    public void setExtensionById(long id, String extension) {
        transactionMapper.setExtensionById(id, extension);
    }

    @Override
    public void insertByTransactionRequestAndUserAndTopUpOptionAndStatus(TransactionRequest transactionRequest, User user, TopUpOption topUpOption, Status status) {
        Transaction transaction = new Transaction( user.getId(), transactionRequest.getCardNumber(), topUpOption.getValue(),
            topUpOption.getFee(), status, transactionRequest.getMethod(), LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS),
            LocalDateTime.now().plusHours(GlobalVariable.TIME_DIFF_DB_HOURS +GlobalVariable.TRANSACTION_LIFETIME_HOURS));
        insert(transaction);
    }
}
