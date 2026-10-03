package org.example.springboot.dao;

import org.example.springboot.entity.User;
import org.example.springboot.mapper.UserMapper;
import org.springframework.stereotype.Repository;
import java.util.Date;

@Repository
public class FromUsers {
    private UserMapper userMapper;
    public FromUsers(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User selectByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    public int addUser(String username, String password) {
        return userMapper.addUser(username, password,new Date());
    }
}
