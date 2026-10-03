package org.example.springboot.service;

import org.example.springboot.dao.FromUsers;
import org.example.springboot.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final FromUsers fromUsers;
    private final BCryptPasswordEncoder encoder;

    public UserService(FromUsers fromUsers, BCryptPasswordEncoder encoder) {
        this.fromUsers = fromUsers;
        this.encoder = encoder;
    }
    public User findByUsername(String username) {
        User user = fromUsers.selectByUsername(username);
        return user;
    }
    public Boolean checkPassword(User user,String password) {
        String p = user.getPassword();
        Boolean flag =encoder.matches(password,p);
        return flag;
    }
    public User register(String username,String password) {
        User user = findByUsername(username);
        if (user==null) {
            String encode = encoder.encode(password);
            int i = fromUsers.addUser(username, encode);
            if (i>0){
                return fromUsers.selectByUsername(username);
            }
        }
        return null;
    }
}
