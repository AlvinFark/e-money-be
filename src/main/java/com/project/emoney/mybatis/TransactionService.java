package com.project.emoney.mybatis;

import com.project.emoney.entity.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getInprogress(long id);
    void insert(Transaction transaction);
}
