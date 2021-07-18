package com.example.trainerguide.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Trainer extends User {
    private HashMap<String,UserMetaData> usersList;
    private Double subscriptionFees;
    private String subscriptionDescription;
    private Double experience;
    private double rating;
    private double ratedTraineescount;


    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRatedTraineescount() {
        return ratedTraineescount;
    }

    public void setRatedTraineescount(double ratedTraineescount) {
        this.ratedTraineescount = ratedTraineescount;
    }

    public void setUser(UserMetaData user) {
        if (this.usersList == null) {
            this.usersList = new HashMap<>();
            this.usersList.put(user.getUserId(),user);
        } else {
            this.usersList.put(user.getUserId(),user);
        }
    }

    public Trainer(String userId, String name,Long phoneNumber, Double bmr, Double bmi, Date dateOfBirth, String gender, Double weight, Double height, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, List<BmrProgress> bmrReport, HashMap<String,UserMetaData> usersList,HashMap<String,String> healthIssues, HashMap<String,String> foodAllergy, String foodType, String subscriptionType, String description) {
        //super(userId, name, bmr,bmi, dateOfBirth, gender, weight, height, accCreateDttm, isTrainer, lastModDttm, image, email, bmrReport,new HashMap<String, String>(),new HashMap<String, String>(),new HashMap<String, Notification>(), foodType, subscriptionType, description);
        super(userId, name, bmr,bmi, dateOfBirth, gender, weight, height, accCreateDttm, isTrainer, lastModDttm, image, email,phoneNumber, bmrReport,healthIssues,foodAllergy,new HashMap<String, Notification>(), foodType);
        this.usersList = usersList;
    }

    public Trainer(String userId, String name, String gender, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, Long phoneNumber) {
        super(userId, name, gender, accCreateDttm, isTrainer, lastModDttm, image, email, phoneNumber);
        this.usersList = new HashMap<>();
    }

    public Trainer() {
    }
    
    public Double getSubscriptionFees() {
        return subscriptionFees;
    }

    public void setSubscriptionFees(Double subscriptionFees) {
        this.subscriptionFees = subscriptionFees;
    }


    public String getSubscriptionDescription() {
        return subscriptionDescription;
    }

    public void setSubscriptionDescription(String subscriptionDescription) {
        this.subscriptionDescription = subscriptionDescription;
    }

    public Double getExperience() {
        return experience;
    }

    public void setExperience(Double experience) {
        this.experience = experience;
    }

    public void setUsersList(HashMap<String,UserMetaData> usersList) {
        this.usersList = usersList;
    }

    public HashMap<String,UserMetaData> getUsersList() {
        return usersList;
    }



}
