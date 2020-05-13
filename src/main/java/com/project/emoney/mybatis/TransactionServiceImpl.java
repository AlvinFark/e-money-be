package com.project.emoney.mybatis;

import com.project.emoney.entity.Transaction;
import com.project.emoney.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public List<Transaction> getInProgress(long id) {
        return transactionMapper.getInProgress(id);
    }

    @Override
    public List<Transaction> getCompleted(long id) {
        return transactionMapper.getCompleted(id);
    }

    @Override
    public void insert(Transaction transaction) {
        transactionMapper.insert(transaction);
    }

    @Override
    public void updateTransaction(long id) {
        transactionMapper.updateTransaction(id);
    }
}
