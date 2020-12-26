package com.example.trainerguide.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trainer extends User {
    private HashMap<String,UserMetaData> usersList;
    private String fees;
    private String description;
    private String experience;

    public HashMap<String,UserMetaData> getUsersList() {
        return usersList;
    }

    public void setUser(UserMetaData user) {
        if (this.usersList == null) {
            this.usersList = new HashMap<>();
            this.usersList.put(user.getUserId(),user);
        } else {
            this.usersList.put(user.getUserId(),user);
        }
    }

    public void setUsersList(HashMap<String,UserMetaData> usersList) {
        this.usersList = usersList;
    }

    public Trainer(String userId, String name, Double bmr, Double bmi, Date dateOfBirth, String gender, Double weight, Double height, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, List<BmrProgress> bmrReport, HashMap<String,UserMetaData> usersList,HashMap<String,String> healthIssues, HashMap<String,String> foodAllergy) {
        super(userId, name, bmr,bmi, dateOfBirth, gender, weight, height, accCreateDttm, isTrainer, lastModDttm, image, email, bmrReport,new HashMap<String, String>(),new HashMap<String, String>());
        this.usersList = usersList;
    }

    public Trainer(String userId, String name, String gender, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email) {
        super(userId, name, gender, accCreateDttm, isTrainer, lastModDttm, image, email);
        this.usersList = new HashMap<>();
    }

    public Trainer() {
    }
    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

}
