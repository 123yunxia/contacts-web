package org.example.springboot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LogoutController {
    @PostMapping("/logout")
    public Map<String,Object> logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Map<String,Object> map = new HashMap<>();
        if (session != null) {
            map.put("status","success");
            session.invalidate();
            return map;
        }
        map.put("status","fail");
        map.put("cause","用户未登录");
        return map;
    }
}
