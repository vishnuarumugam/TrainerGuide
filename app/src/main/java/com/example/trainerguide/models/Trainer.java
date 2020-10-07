package com.example.trainerguide.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trainer extends User {
    private List<UserMetaData> usersList;

    public List<UserMetaData> getUsersList() {
        return usersList;
    }

    public void setUser(UserMetaData user) {
        this.usersList.add(user);
    }

    public void setUsersList(List<UserMetaData> usersList) {
        this.usersList = usersList;
    }

    public Trainer(String userId, String name, Double bmr, Double bmi, Date dateOfBirth, String gender, Double weight, Double height, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, List<BmrProgress> bmrReport, List<UserMetaData> usersList) {
        super(userId, name, bmr,bmi, dateOfBirth, gender, weight, height, accCreateDttm, isTrainer, lastModDttm, image, email, bmrReport);
        this.usersList = usersList;
    }

    public Trainer(String userId, String name, String gender, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email) {
        super(userId, name, gender, accCreateDttm, isTrainer, lastModDttm, image, email);
        this.usersList = new ArrayList<>();
    }

    public Trainer() {
    }
}
