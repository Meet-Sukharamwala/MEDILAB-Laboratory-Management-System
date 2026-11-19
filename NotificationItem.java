package com.example.medilab.models;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    private int notificationId;
    private int userId;
    private String title;
    private String message;
    private String createdAt;
    private boolean isRead;

    public NotificationItem() {
    }

    public NotificationItem(int notificationId, int userId, String title, String message, String createdAt, boolean isRead) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public NotificationItem(int userId, String title, String message, String createdAt) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = false;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public int getIsRead() {
        return isRead ? 1 : 0;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
