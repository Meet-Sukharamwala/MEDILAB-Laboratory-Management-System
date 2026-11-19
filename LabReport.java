package com.example.medilab.models;

import java.io.Serializable;

/**
 * Model class representing a Laboratory Report in MEDILAB.
 * Connects Patient + Medical Test + Test Date + Result + Remarks.
 * Contains both foreign keys and denormalized joined fields for display/PDF export.
 */
public class LabReport implements Serializable {
    private int reportId;
    private int patientId;
    private int testId;
    private String testDate;
    private String result;
    private String remarks;

    // Joined presentation fields
    private String patientName;
    private String patientPhone;
    private String patientBloodGroup;
    private int patientAge;
    private String patientGender;
    private String patientEmail;
    private String patientAddress;

    private String testName;
    private String testCategory;
    private String testNormalRange;
    private double testPrice;

    // Default constructor
    public LabReport() {
    }

    // Constructor for new insertion
    public LabReport(int patientId, int testId, String testDate, String result, String remarks) {
        this.patientId = patientId;
        this.testId = testId;
        this.testDate = testDate;
        this.result = result;
        this.remarks = remarks;
    }

    // Constructor with report ID
    public LabReport(int reportId, int patientId, int testId, String testDate, String result, String remarks) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.testId = testId;
        this.testDate = testDate;
        this.result = result;
        this.remarks = remarks;
    }

    // Getters and Setters
    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
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

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPatientName() {
        return patientName != null ? patientName : "Patient #" + patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone != null ? patientPhone : "";
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getPatientBloodGroup() {
        return patientBloodGroup != null ? patientBloodGroup : "";
    }

    public void setPatientBloodGroup(String patientBloodGroup) {
        this.patientBloodGroup = patientBloodGroup;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender != null ? patientGender : "";
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientEmail() {
        return patientEmail != null ? patientEmail : "";
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientAddress() {
        return patientAddress != null ? patientAddress : "";
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getTestName() {
        return testName != null ? testName : "Test #" + testId;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestCategory() {
        return testCategory != null ? testCategory : "";
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public String getTestNormalRange() {
        return testNormalRange != null ? testNormalRange : "";
    }

    public void setTestNormalRange(String testNormalRange) {
        this.testNormalRange = testNormalRange;
    }

    public String getNormalRange() {
        return getTestNormalRange();
    }

    public void setNormalRange(String normalRange) {
        setTestNormalRange(normalRange);
    }

    public double getTestPrice() {
        return testPrice;
    }

    public void setTestPrice(double testPrice) {
        this.testPrice = testPrice;
    }
}
