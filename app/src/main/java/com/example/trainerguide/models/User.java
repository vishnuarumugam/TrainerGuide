package com.example.trainerguide.models;

import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class User {

    private String userId;
    private String name;
    private Double bmr;
    private Date dateOfBirth;
    private String gender;
    private Double weight;
    private Double height;
    private Date accCreateDttm;
    private boolean isTrainer;
    private Date lastModDttm;
    private Double bmi;
    private String image;
    private String email;
    private List<BmrProgress> bmrReport;
    private HashMap<String,String> healthIssues;
    private HashMap<String,String> foodAllergy;

    public User(String userId, String name, Double bmr, Double bmi, Date dateOfBirth, String gender,
                Double weight, Double height, Date accCreateDttm, boolean isTrainer, Date lastModDttm,
                String image, String email, List<BmrProgress> bmrReport, HashMap<String,String> healthIssues,
                HashMap<String,String> foodAllergy) {
        this.userId = userId;
        this.name = name;
        this.bmr = bmr;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.accCreateDttm = accCreateDttm;
        this.isTrainer = isTrainer;
        this.lastModDttm = lastModDttm;
        this.image = image;
        this.email = email;
        this.bmi = bmi;
        this.bmrReport = bmrReport;
        this.healthIssues = healthIssues;
        this.foodAllergy = foodAllergy;
    }

    public User(String userId, String name, String gender, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email) {
        this.userId = userId;
        this.name = name;
        this.bmr = 0.0;
        this.dateOfBirth = Calendar.getInstance().getTime();
        this.gender = gender;
        this.weight = 0.00;
        this.height = 0.00;
        this.bmi = 0.00;
        this.accCreateDttm = accCreateDttm;
        this.isTrainer = isTrainer;
        this.lastModDttm = lastModDttm;
        this.image = image;
        this.email = email;
        this.bmrReport = new ArrayList<>();
    }
    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", bmr=" + bmr +
                ", dateOfBirth=" + dateOfBirth +
                ", gender='" + gender + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                ", accCreateDttm=" + accCreateDttm +
                ", isTrainer=" + isTrainer +
                ", lastModDttm=" + lastModDttm +
                ", image='" + image + '\'' +
                ", email='" + email + '\'' +
                ", bmrReport=" + bmrReport +
                '}';
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

    public void setBmr(Double weight, Double height,String gender, Integer age) {
        Double bmi = bmrCalculation(weight,height,gender,age);
        this.bmi = bmi;
    }

    public Double getBmr() {
        return bmr;
    }

    public void setBmi(Double weight, Double height) {
        Double bmi = bmiCalculation(weight,height);
        this.bmi = bmi;
    }

    public Double getBmi() {
        return bmi;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Date getAccCreateDttm() {
        return accCreateDttm;
    }

    public void setAccCreateDttm(Date accCreateDttm) {
        this.accCreateDttm = accCreateDttm;
    }

    public boolean isTrainer() {
        return isTrainer;
    }

    public void setTrainer(boolean trainer) {
        isTrainer = trainer;
    }

    public Date getLastModDttm() {
        return lastModDttm;
    }

    public void setLastModDttm(Date lastModDttm) {
        this.lastModDttm = lastModDttm;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BmrProgress> getBmrReport() {
        return bmrReport;
    }

    public void setBmrReport(List<BmrProgress> bmrReport) {
        this.bmrReport = bmrReport;
    }

    public Double bmrCalculation(double weight, double height, String gender, Integer age){
        Double weightBmr = weight*10;
        Double heightBmr = height*6.25;
        Integer ageBmr = age*5;
        Double bmr = weightBmr + heightBmr - ageBmr;

        if (gender.equals("Male")){
            bmr = bmr + 5;
        }
        else {
            bmr = bmr - 161;
        }
        return bmr;
    }

    public Double bmiCalculation(Double weight, Double height) {
        Double weightBmi = weight;
        Double heightBmi = height/100;
        Double bmi = weightBmi/heightBmi;
        return bmi;
    }

    public HashMap<String,String> getHealthIssues() {
        return healthIssues;
    }

    public void setHealthIssues(HashMap<String,String> healthIssues) {
        this.healthIssues = healthIssues;
    }

    public HashMap<String,String> getfoodAllergy() {
        return foodAllergy;
    }

    public void setfoodAllergy(HashMap<String,String> foodAllergy) {
        this.foodAllergy = foodAllergy;
    }

}
