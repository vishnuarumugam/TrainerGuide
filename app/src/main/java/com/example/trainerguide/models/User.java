package com.example.trainerguide.models;

import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private String image;
    private String email;
    private List<BmrProgress> bmrReport;

    public User(String userId, String name, Double bmr, Date dateOfBirth, String gender, Double weight, Double height, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email, List<BmrProgress> bmrReport) {
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
        this.bmrReport = bmrReport;
    }
    public User(String userId, String name, String gender, Date accCreateDttm, boolean isTrainer, Date lastModDttm, String image, String email) {
        this.userId = userId;
        this.name = name;
        this.bmr = 0.0;
        this.dateOfBirth = Calendar.getInstance().getTime();
        this.gender = gender;
        this.weight = 0.00;
        this.height = 0.00;
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

    public Double getBmr() {
        return bmr;
    }

    public void setBmr(Double bmr) {
        this.bmr = bmr;
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
}
