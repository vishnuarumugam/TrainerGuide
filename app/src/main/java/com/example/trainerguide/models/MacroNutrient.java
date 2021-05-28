package com.example.trainerguide.models;

public class MacroNutrient {

    private String name;
    private Double calorie;
    private Double percentage;

    public MacroNutrient() {
    }

    public MacroNutrient(String name, Double calorie, Double percentage) {
        this.name = name;
        this.calorie = calorie;
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCalorie() {
        return calorie;
    }

    public void setCalorie(Double calorie) {
        this.calorie = calorie;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
