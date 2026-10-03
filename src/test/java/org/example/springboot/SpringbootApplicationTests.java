package org.example.springboot;

import org.example.springboot.entity.Contact;
import org.example.springboot.mapper.ContactMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringbootApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired ContactMapper contactMapper;

    @Test
    void 测试查联系人() {
        List<Contact> list = contactMapper.selectByUserId(22);
        list.forEach(System.out::println);      // 打印出来看 landline 和 createdAt 有没有值
    }

}
