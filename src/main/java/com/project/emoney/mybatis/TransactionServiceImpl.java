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
    public List<Transaction> getInprogress(long id) {
        return transactionMapper.getInprogress(id);
    }
}
