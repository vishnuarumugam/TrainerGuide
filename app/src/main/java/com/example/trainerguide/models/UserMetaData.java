package com.example.trainerguide.models;

import java.util.Date;

public class UserMetaData {
    private String userId;
    private String name;
    private Double bmi;
    private String image;
    private Date subscriptionEndDate;



    public UserMetaData(String userId, String name, Double bmi, String image, Date subscriptionEndDate) {
        this.userId = userId;
        this.name = name;
        this.bmi = bmi;
        this.image = image;
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public UserMetaData() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }
}
