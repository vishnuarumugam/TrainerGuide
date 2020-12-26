package com.example.trainerguide.models;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Trainee extends User {
    private String TrainerId;

    public Trainee(String userId, String name, Double bmr, Double bmi, Date dateOfBirth, String gender, Double weight, Double height, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, List<BmrProgress> bmrReport, HashMap<String, String> healthIssues, HashMap<String, String> foodAllergy, String trainerId) {
        super(userId, name, bmr, bmi, dateOfBirth, gender, weight, height, accCreateDttm, isTrainer, lastModDttm, image, email, bmrReport, healthIssues, foodAllergy);
        TrainerId = trainerId;
    }

    public Trainee(String userId, String name, String gender, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, String trainerId) {
        super(userId, name, gender, accCreateDttm, isTrainer, lastModDttm, image, email);
        TrainerId = trainerId;
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
}
