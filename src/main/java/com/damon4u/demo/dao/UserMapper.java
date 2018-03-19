package com.damon4u.demo.dao;

import com.damon4u.demo.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Description:
 *
 * @author damon4u
 * @version 2017-08-22 12:19
 */
@Mapper
@Repository
public interface UserMapper {

    @Insert("insert into user(user_name) values (#{userName})")
    @Options(useGeneratedKeys=true)
    int insert(User user);

    @Select("select id,user_name from user where id=#{id}")
    User findById(@Param("id") Long id);

    List<User> findByUserName(@Param("userName") String userName);
}
