package org.example.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.springboot.entity.Contact;

import java.util.List;

@Mapper
public interface ContactMapper {
    List<Contact> selectByUserId(@Param("userId") int userId);
    Contact selectByIdAndUser(@Param("id") int id, @Param("userId") int userId);
    int insert(Contact contact);
    int update(Contact contact);
    int deleteByIdAndUser(@Param("id") int id, @Param("userId") int userId);
}
