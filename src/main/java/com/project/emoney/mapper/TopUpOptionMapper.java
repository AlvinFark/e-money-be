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

    @Select("SELECT * FROM TopUpOption")
    List<TopUpOption> getAllTopUpOption();

    @Insert("INSERT INTO TopUpOption (value, fee) VALUES (#{value}, #{fee})")
    void createTopUpOption(TopUpOption topUpOption);

    @Select("SELECT * FROM TopUpOption WHERE id = #{id}")
    TopUpOption getById(long id);
}
