package com.example.medilab.models;

import java.io.Serializable;

/**
 * Model class representing a Medical Test catalog entity in MEDILAB.
 * Used for database mapping, test selection, and CRUD operations.
 */
public class MedicalTest implements Serializable {
    private int id;
    private String testName;
    private String category;
    private double price;
    private String normalRange;
    private String description;

    // Default constructor
    public MedicalTest() {
    }

    // Constructor without ID (for new insertions)
    public MedicalTest(String testName, String category, double price, String normalRange, String description) {
        this.testName = testName;
        this.category = category;
        this.price = price;
        this.normalRange = normalRange;
        this.description = description;
    }

    // Full constructor (for database retrieval)
    public MedicalTest(int id, String testName, String category, double price, String normalRange, String description) {
        this.id = id;
        this.testName = testName;
        this.category = category;
        this.price = price;
        this.normalRange = normalRange;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTestId() {
        return id;
    }

    public void setTestId(int id) {
        this.id = id;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNormalRange() {
        return normalRange;
    }

    public void setNormalRange(String normalRange) {
        this.normalRange = normalRange;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ID #" + id + " - " + testName + " [" + category + "]";
    }
}
