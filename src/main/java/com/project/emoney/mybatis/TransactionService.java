package com.project.emoney.mybatis;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getInProgressByUserId(long id);
    List<Transaction> getAllByUserId(long id);
    void insert(Transaction transaction);
    void updateStatusById(long id, Status status);
}
