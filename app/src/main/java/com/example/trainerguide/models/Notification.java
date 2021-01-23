package com.example.trainerguide.models;

import java.util.Date;

public class Notification {
    private Date addedDate;
    private String notification;
    private String notificationType;

    public Notification(Date addedDate, String notification, String notificationType, String userId) {
        this.addedDate = addedDate;
        this.notification = notification;
        this.notificationType = notificationType;
        this.userId = userId;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
}
