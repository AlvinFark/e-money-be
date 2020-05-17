package com.project.emoney.mapper;

import com.project.emoney.entity.Status;
import com.project.emoney.entity.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TransactionMapper {

    @Select("SELECT * FROM transaction WHERE userId = #{userId} AND (status = 'IN_PROGRESS' OR status = 'VERIFYING') ORDER BY time DESC")
    List<Transaction> getInProgressByUserId(long userId);

    @Select("SELECT * FROM transaction WHERE userId = #{userId} ORDER BY time DESC")
    List<Transaction> getAllByUserId(long userId);

    @Select("SELECT * FROM transaction WHERE id = #{id}")
    Transaction getById(long id);

    @Insert("INSERT INTO transaction (userId, cardNumber, value, fee, status, time, method, expiry) VALUES" +
        "(#{userId}, #{cardNumber}, #{value}, #{fee}, #{status}, #{time}, #{method}, #{expiry})")
    void insert(Transaction transaction);

    @Update("UPDATE transaction SET status = #{status} WHERE id = #{id}")
    void setStatusById(long id, Status status);

    @Update("UPDATE transaction SET imagePath = #{extension}, status = #{status} WHERE id = #{id}")
    void setExtensionById(long id, String extension, Status status);
}
