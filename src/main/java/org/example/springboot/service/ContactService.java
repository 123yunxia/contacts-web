package org.example.springboot.service;

import org.example.springboot.entity.Contact;
import org.example.springboot.mapper.ContactMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {
    private final ContactMapper contactMapper;

    public ContactService(ContactMapper contactMapper) {
        this.contactMapper = contactMapper;
    }

    public List<Contact> listContacts(int currentUserId) {   // ← 只走 Dao/Mapper，不碰 session
        // 你写：就一行
        return contactMapper.selectByUserId(currentUserId);
    }
    public Contact addContact(Contact contact,int currentUserId) {
        contact.setUserId(currentUserId);
        try {
            int row = contactMapper.insert(contact);
            return  row>0?contact:null;
        }catch (DuplicateKeyException e) {
            return null;
        }
    }
    public int updateContact(Contact contact,int currentUserId,int id) {
        contact.setUserId(currentUserId);
        contact.setId(id);
        return contactMapper.update(contact);
    }
    public int deleteContact(int currentUserId,int id) {
        return contactMapper.deleteByIdAndUser(id,currentUserId);
    }
    public Contact getContact(int id,int currentUserId) {
        return contactMapper.selectByIdAndUser(id,currentUserId);
    }
}
