package org.example.springboot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.springboot.entity.SessionKeys;
import org.example.springboot.entity.User;
import org.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {
    private final UserService userService;
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body,
                                     HttpServletRequest request) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> map = new HashMap<>();
        // TODO 1：查用户（userService 里有现成的 findByUsername）
        User byUsername = userService.findByUsername(username);
        if (byUsername == null) {
            map.put("status","fail");
            map.put("cause","用户不存在");
            return map;
        }
        Boolean b = userService.checkPassword(byUsername,password);
        if(b){
            HttpSession session = request.getSession();
            session.setAttribute(SessionKeys.USER_ID,byUsername.getId());
            session.setAttribute(SessionKeys.USERNAME,byUsername.getUsername());
            map.put("status","success");
            map.put(SessionKeys.USERNAME,byUsername.getUsername());
        }else {
            map.put("status","fail");
            map.put("cause","密码错误");
        }
        return map;
    }

}
