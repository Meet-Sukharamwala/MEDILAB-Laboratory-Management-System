package com.example.medilab.models;

import java.io.Serializable;

public class User implements Serializable {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String dob;
    private String gender;
    private String bloodGroup;
    private String address;
    private String createdAt;

    public User() {
    }

    public User(int userId, String name, String email, String phone, String password,
                String dob, String gender, String bloodGroup, String address, String createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.createdAt = createdAt;
    }

    public User(String name, String email, String phone, String password,
                String dob, String gender, String bloodGroup, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
