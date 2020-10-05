package com.example.trainerguide.models;

import java.util.Date;

public class BmrProgress {
    private Date addedDate;
    private Double bmrValue;



    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    public Double getBmrValue() {
        return bmrValue;
    }

    public void setBmrValue(Double bmrValue) {
        this.bmrValue = bmrValue;
    }

    public BmrProgress(Date addedDate, Double bmrValue) {
        this.addedDate = addedDate;
        this.bmrValue = bmrValue;
    }

    public BmrProgress() {
    }
}
