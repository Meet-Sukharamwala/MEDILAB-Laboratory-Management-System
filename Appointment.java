package com.example.medilab.models;

import java.io.Serializable;

public class Appointment implements Serializable {
    private int appointmentId;
    private int userId;
    private int patientId;
    private int testId;
    private String appointmentDate;
    private String appointmentTime;
    private String collectionType;
    private String address;
    private String status; // Pending, Confirmed, Completed, Cancelled
    private String createdAt;

    // Joined fields for display
    private String patientName;
    private String patientPhone;
    private String testName;
    private String testCategory;
    private double testPrice;

    public Appointment() {
    }

    public Appointment(int appointmentId, int userId, int patientId, int testId,
                       String appointmentDate, String appointmentTime, String collectionType,
                       String address, String status, String createdAt) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.patientId = patientId;
        this.testId = testId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.collectionType = collectionType;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public double getTestPrice() {
        return testPrice;
    }

    public void setTestPrice(double testPrice) {
        this.testPrice = testPrice;
    }
}
