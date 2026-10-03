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
public class RegisterController {
    private final UserService userService;
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body,
                                        HttpServletRequest request) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> map = new HashMap<>();
        User register = userService.register(username, password);
        if (register == null) {
            map.put("status", "fail");
            map.put("cause","用户已存在");
        }
        else {
            HttpSession session = request.getSession();
            map.put("status", "success");
            map.put(SessionKeys.USER_ID, register.getId());
            map.put(SessionKeys.USERNAME, register.getUsername());
            session.setAttribute(SessionKeys.USER_ID, register.getId());
            session.setAttribute(SessionKeys.USERNAME, register.getUsername());
        }
        return map;   // 占位
    }
}
