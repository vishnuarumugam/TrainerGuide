package com.example.trainerguide.models;

public class UserMetaData {
    private String userId;
    private String name;
    private Double bmr;
    private String image;

    public UserMetaData(String userId, String name, Double bmr, String image) {
        this.userId = userId;
        this.name = name;
        this.bmr = bmr;
        this.image = image;
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

    public Double getBmr() {
        return bmr;
    }

    public void setBmr(Double bmr) {
        this.bmr = bmr;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
