package com.project.emoney.mapper;

import com.project.emoney.entity.TopUpOption;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TopUpOptionMapper {

    @Select("SELECT * FROM TopUpOption")
    List<TopUpOption> getAllTopUpOption();

    @Insert("INSERT INTO TopUpOption (value, fee) VALUES (#{value}, #{fee})")
    void createTopUpOption(TopUpOption topUpOption);
}
