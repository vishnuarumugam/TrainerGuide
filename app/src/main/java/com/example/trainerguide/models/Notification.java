package com.example.trainerguide.models;

import java.util.Date;

public class Notification {
    private Date addedDate;
    private String notification;
    private String notificationType;
    private String notificationHeader;
    private boolean trainer;
    private String notificationId;

    public Notification(Date addedDate, String notification, String notificationType, String userId, boolean trainer, String notificationId) {
        this.addedDate = addedDate;
        this.notification = notification;
        this.notificationType = notificationType;
        this.userId = userId;
        this.trainer = trainer;
        this.notificationId = notificationId;
    }
    public Notification() {
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

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public boolean isTrainer() {
        return trainer;
    }

    public void setTrainer(boolean trainer) {
        this.trainer = trainer;
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

    public String getNotificationHeader() {
        return notificationHeader;
    }

    public void setNotificationHeader(String notificationHeader) {
        this.notificationHeader = notificationHeader;
    }
}
