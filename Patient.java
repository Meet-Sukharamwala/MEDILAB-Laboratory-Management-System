package com.example.medilab.models;

import java.io.Serializable;

/**
 * Model class representing a Patient entity in MEDILAB.
 * Used for database mapping, list displays, and CRUD operations.
 */
public class Patient implements Serializable {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String bloodGroup;

    // Default constructor
    public Patient() {
    }

    // Constructor without ID (for new insertions)
    public Patient(String name, int age, String gender, String phone, String email, String address, String bloodGroup) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.bloodGroup = bloodGroup;
    }

    // Full constructor (for database retrieval)
    public Patient(int id, String name, int age, String gender, String phone, String email, String address, String bloodGroup) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.bloodGroup = bloodGroup;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return id;
    }

    public void setPatientId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    @Override
    public String toString() {
        return "ID #" + id + " - " + name + " (" + gender + ", " + age + "y)";
    }
}
