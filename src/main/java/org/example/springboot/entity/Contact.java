package org.example.springboot.entity;

import java.util.Date;

public class Contact {
    private int id;
    private int userId;
    private String name;
    private String phone;
    private String landline;
    private Date createdAt;

    public Contact(int id, int userId, String name, String phone, String landline, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.landline = landline;
        this.createdAt = createdAt;
    }

    public Contact() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", landline='" + landline + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
