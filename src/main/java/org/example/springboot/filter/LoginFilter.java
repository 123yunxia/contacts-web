package org.example.springboot.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.springboot.entity.SessionKeys;

import java.io.IOException;
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        // 1. 把 ServletRequest 转成 HttpServletRequest（为什么必须转：session 是 HTTP 概念）
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        // 2. getSession(false) → 取 SessionKeys.USER_ID
        HttpSession session = req.getSession(false);
        Object attribute = session==null ? null:session.getAttribute(SessionKeys.USER_ID);
        // 3. 没有 → 401 + 写回 [] + return（★ 千万别往下调 chain）
        if (attribute == null) {
            resp.setStatus(401);
            System.out.println("拦截成功，原因：attribute == null");
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().write("[]");
            return;
        }
        // 4. 有 → chain.doFilter(request, response)
        chain.doFilter(request, response);

    }
}
