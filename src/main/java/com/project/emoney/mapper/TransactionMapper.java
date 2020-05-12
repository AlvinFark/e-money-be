package com.project.emoney.mapper;

import com.project.emoney.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TransactionMapper {

    @Select("SELECT * FROM Transaction WHERE userId = #{userId} GROUP BY userId HAVING status = 'inprogress'")
    List<Transaction> getInprogress(long userId);

}
