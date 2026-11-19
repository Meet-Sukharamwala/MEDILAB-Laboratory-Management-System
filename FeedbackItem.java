package com.example.medilab.models;

import java.io.Serializable;

public class FeedbackItem implements Serializable {
    private int feedbackId;
    private int userId;
    private int appointmentId;
    private int rating;
    private String comment;
    private String createdAt;

    public FeedbackItem() {
    }

    public FeedbackItem(int feedbackId, int userId, int rating, String comment, String createdAt) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public FeedbackItem(int feedbackId, int userId, int appointmentId, int rating, String comment, String createdAt) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
