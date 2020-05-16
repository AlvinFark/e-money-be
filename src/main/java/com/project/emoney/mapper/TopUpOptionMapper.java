package com.project.emoney.mapper;

import com.project.emoney.entity.TopUpOption;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TopUpOptionMapper {

    @Select("SELECT * FROM topupoption")
    List<TopUpOption> getAll();

    @Insert("INSERT INTO topupoption (value, fee) VALUES (#{value}, #{fee})")
    void insert(TopUpOption topUpOption);

    @Select("SELECT * FROM topupoption WHERE id = #{id}")
    TopUpOption getById(long id);
}
