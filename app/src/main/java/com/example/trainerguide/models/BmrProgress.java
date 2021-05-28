package com.example.trainerguide.models;

import java.util.Date;

public class BmrProgress {
    private Date addedDate;
    private Double bmiValue;



    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Double getBmrValue() {
        return bmiValue;
    }

    public void setBmrValue(Double bmrValue) {
        this.bmiValue = bmrValue;
    }

    public BmrProgress(Date addedDate, Double bmrValue) {
        this.addedDate = addedDate;
        this.bmiValue = bmrValue;
    }

    public BmrProgress() {
    }
}
