package com.example.trainerguide.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Trainee extends User {
    private String TrainerId;
    //private String foodType;
    private String subscriptionType;
    private Date subscriptionEndDate;

    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public Trainee(String userId, String name, Long phoneNumber, Double bmr, Double bmi, Date dateOfBirth, String gender, Double weight, Double height, Date accCreateDttm, boolean isTrainer, Date lastModDttm,
                   String image, String email, List<BmrProgress> bmrReport, HashMap<String, String> healthIssues, HashMap<String, String> foodAllergy, String trainerId, HashMap<String, Notification> notifications, String subscriptionType, String foodType) {
        super(userId, name, bmr, bmi, dateOfBirth, gender, weight, height, accCreateDttm, isTrainer, lastModDttm, image, email,phoneNumber, bmrReport, healthIssues, foodAllergy, notifications, foodType);
        TrainerId = trainerId;
        subscriptionType = subscriptionType;
    }

    public Trainee(String userId, String name, String gender, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, String trainerId,Long phoneNumber) {
        super(userId, name, gender, accCreateDttm, isTrainer, lastModDttm, image, email,phoneNumber);
        TrainerId = trainerId;
        subscriptionType = "Not mentioned";
    }

    public Trainee(){

    }

    public Trainee(String trainerId) {
        TrainerId = trainerId;
    }

    public String getTrainerId() {
        return TrainerId;
    }

    public void setTrainerId(String trainerId) {
        TrainerId = trainerId;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}
