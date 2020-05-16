package com.project.emoney.mapper;

import com.project.emoney.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getById(long id);

    @Select("SELECT * FROM user WHERE email = #{email} ORDER BY id DESC LIMIT 1")
    User getByEmail(String email);

    @Select("SELECT * FROM user WHERE email = #{emailOrPhone} OR phone = #{emailOrPhone} ORDER BY id DESC LIMIT 1")
    User getByEmailOrPhone(String emailOrPhone);

    @Insert("INSERT INTO user (name, email, phone, password, balance, active) VALUES (#{name}, #{email}, #{phone}, #{password}, 0, false)")
    void insert(User user);

    @Update("UPDATE user SET active = TRUE WHERE email = #{email} ORDER BY email DESC LIMIT 1")
    void setActiveByEmail(String email);

    @Update("UPDATE user SET balance = #{balance} WHERE id = #{id}")
    void updateBalance(User user);

    @Select("UPDATE user SET active = true WHERE id = #{id}")
    void activate(User user);

    @Update("UPDATE user SET password = #{password} WHERE email = #{email}")
    void updatePassword(User user);
}
