package com.project.emoney.mapper;

import com.project.emoney.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(long id);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User getUserByEmail(String email);

    @Select("SELECT * FROM user WHERE email = #{emailOrPhone} OR phone = #{emailOrPhone}")
    User getUserByEmailOrPhone(String emailOrPhone);

    @Insert("INSERT INTO user (name, email, phone, password, balance, active) VALUES (#{name}, #{email}, #{phone}, #{password}, 0, false)")
    void insert(User user);

    @Update("UPDATE USER SET active = TRUE WHERE email = #{email}")
    void setActive(String email);

    @Select("UPDATE user SET active = true WHERE id = #{id}")
    void activateUser(User user);
}
