package com.project.emoney.mapper;

import com.project.emoney.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(long id);

    @Select("SELECT * FROM user WHERE email = #{emailOrPhone} OR phone = #{emailOrPhone}")
    User getUserByEmailOrPhone(String emailOrPhone);

    @Insert("INSERT INTO user (name, email, phone, password, balance, active) VALUES (#{name}, #{email}, #{phone}, #{password}, 0, false)")
    User insert(User user);
}
