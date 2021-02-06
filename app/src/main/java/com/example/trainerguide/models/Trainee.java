package com.example.trainerguide.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Trainee extends User {
    private String TrainerId;
    private String foodType;
    private String subscriptionType;

    public Trainee(String userId, String name, Double bmr, Double bmi, Date dateOfBirth, String gender, Double weight, Double height, Date accCreateDttm, boolean isTrainer, Date lastModDttm,
                   String image, String email, List<BmrProgress> bmrReport, HashMap<String, String> healthIssues, HashMap<String, String> foodAllergy, String trainerId, HashMap<String, Notification> notifications, String foodType, String subscriptionType) {
        super(userId, name, bmr, bmi, dateOfBirth, gender, weight, height, accCreateDttm, isTrainer, lastModDttm, image, email, bmrReport, healthIssues, foodAllergy, notifications);
        TrainerId = trainerId;
        foodType = foodType;
        subscriptionType = subscriptionType;
    }

    public Trainee(String userId, String name, String gender, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, String trainerId) {
        super(userId, name, gender, accCreateDttm, isTrainer, lastModDttm, image, email);
        TrainerId = trainerId;
        foodType = "Not mentioned";
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

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}
