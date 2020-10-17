package com.example.trainerguide.models;

public class UserMetaData {
    private String userId;
    private String name;
    private Double bmi;
    private String image;

    public UserMetaData(String userId, String name, Double bmi, String image) {
        this.userId = userId;
        this.name = name;
        this.bmi = bmi;
        this.image = image;
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
}
