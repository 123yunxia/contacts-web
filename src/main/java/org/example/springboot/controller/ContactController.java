package org.example.springboot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.springboot.entity.Contact;
import org.example.springboot.entity.SessionKeys;
import org.example.springboot.service.ContactService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ContactController {
    private ContactService contactService;
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
    @GetMapping("/contacts")
    public List<Contact> selectByUserId(HttpServletRequest request, HttpServletResponse response ) {
        HttpSession session = request.getSession(false);
        Object uid = (session==null) ? null : session.getAttribute(SessionKeys.USER_ID);
        if (uid == null) {
            System.out.println("[CONTACT] 未登录：session=" + (session == null ? "无" : "有但无 userId"));
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            return new ArrayList<>();
        }
        int userId = (int) uid;
        return contactService.listContacts(userId);
    }
    @PostMapping("/contacts")
    public Map<String,Object> add(@RequestBody Contact contact, HttpServletResponse response , HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession(false);
        Object uid = (session==null) ? null : session.getAttribute(SessionKeys.USER_ID);
        if (uid == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            return map;
        }
        Contact contact1 = contactService.addContact(contact, (int) uid);
        if (contact1 == null) {
            map.put("status", "fail");
            map.put("cause","该号码已存在");
        }
        else {
            map.put("status", "success");
            map.put("id",contact1.getId());
        }
        return map;
    }
    @GetMapping("/contacts/{id}")
    public Contact getone(@PathVariable("id") int id, HttpServletResponse resp , HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object uid = (session==null) ? null : session.getAttribute(SessionKeys.USER_ID);
        if (uid == null) {
            resp.setStatus(401);
            resp.setContentType("application/json;charset=utf-8");
            return null;
        }
        Contact contact = contactService.getContact(id, (int) uid);
        if(contact == null) {
            resp.setStatus(404);
        }
        return contact;
    }
    @PutMapping("/contacts/{id}")
    public Map<String,Object> update(@RequestBody Contact contact, @PathVariable("id") int id, HttpServletResponse resp , HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Map<String,Object> map = new HashMap<>();
        Object uid = (session==null) ? null : session.getAttribute(SessionKeys.USER_ID);
        if (uid == null) {
            resp.setStatus(401);
            resp.setContentType("application/json;charset=utf-8");
            return map;
        }
        int i = contactService.updateContact(contact, (int) uid, id);
        if (i > 0) {
            map.put("status", "success");
        }else  {
            map.put("status", "fail");
        }
        return map;
    }
    @DeleteMapping("/contacts/{id}")
    public Map<String, Object> remove(@PathVariable("id") int id,
                                      HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession(false);
        Object uid = (session == null) ? null : session.getAttribute(SessionKeys.USER_ID);
        if (uid == null) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            return map;
        }
        int rows = contactService.deleteContact((int) uid, id);
        if (rows > 0) {
            map.put("status", "success");
        } else {
            map.put("status", "fail");
        }
        return map;
    }
}
