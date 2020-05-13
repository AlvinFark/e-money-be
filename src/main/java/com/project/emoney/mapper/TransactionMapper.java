package com.project.emoney.mapper;

import com.project.emoney.entity.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TransactionMapper {

    @Select("SELECT * FROM Transaction WHERE userId = #{userId} AND status = 'IN_PROGRESS'")
    List<Transaction> getInProgress(long userId);

    @Select("SELECT * FROM Transaction WHERE userId = #{userId} AND status = 'COMPLETED'")
    List<Transaction> getCompleted(long userId);

    @Insert("INSERT INTO Transaction (userId, cardNumber, value, fee, status, time, method, expiry) VALUES" +
        "(#{userId}, #{cardNumber}, #{value}, #{fee}, #{status}, #{time}, #{method}, #{expiry})")
    void insert(Transaction transaction);
}
