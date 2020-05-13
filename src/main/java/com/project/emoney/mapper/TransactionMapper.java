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

    @Select("SELECT * FROM Transaction WHERE userId = #{userId} AND status = 'IN_PROGRESS'")
    List<Transaction> getInProgressByUserId(long userId);

    @Select("SELECT * FROM Transaction WHERE userId = #{userId}")
    List<Transaction> getAllByUserId(long userId);

    @Select("SELECT * FROM Transaction WHERE id = #{id}")
    Transaction getById(long id);

    @Insert("INSERT INTO Transaction (userId, cardNumber, value, fee, status, time, method, expiry) VALUES" +
        "(#{userId}, #{cardNumber}, #{value}, #{fee}, #{status}, #{time}, #{method}, #{expiry})")
    void insert(Transaction transaction);

    @Update("UPDATE Transaction SET status = #{status} WHERE id = #{id}")
    void setStatusById(long id, Status status);

    @Update("UPDATE Transaction SET imagePath = #{extension} WHERE id = #{id}")
    void setExtensionById(long id, String extension);
}
