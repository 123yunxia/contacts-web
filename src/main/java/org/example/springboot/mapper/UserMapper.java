package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.User;

import java.util.Date;

@Mapper
public interface UserMapper {
    User selectByUsername(@Param("username")  String username);
    int addUser(@Param("username") String username, @Param("password") String password,@Param("createdAt") Date date);
    int updateUser(@Param("username") String username,@Param("password") String password);
    int deleteUser(@Param("username") String username);
}
