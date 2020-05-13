package com.project.emoney.mybatis;

import com.project.emoney.entity.Status;
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
}
