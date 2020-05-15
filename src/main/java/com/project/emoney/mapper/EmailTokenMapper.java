package com.project.emoney.mapper;

import com.project.emoney.entity.EmailToken;
import com.project.emoney.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EmailTokenMapper {

    @Insert("INSERT INTO emailtoken (userId, token, time) VALUES (#{userId}, #{token}, #{time})")
    void createToken(EmailToken emailToken);

    @Select("SELECT * FROM emailtoken WHERE token = #{token}")
    EmailToken findTokenByToken(String token);

    @Select("SELECT * FROM emailtoken WHERE userId = #{id}")
    EmailToken findTokenByUser(User user);
}
