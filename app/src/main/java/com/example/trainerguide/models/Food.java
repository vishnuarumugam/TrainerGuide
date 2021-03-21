package com.example.trainerguide.models;

public class Food {

    private String name;
    private String nutritionType;
    private Double calorieValue;
    private String foodType;
    private String measurementUnit;
    private Double quantity;
    public int value;
    public Double totalCalorie;
    public String tab;

    public Food() {

    }

    public Food(String name, String nutritionType, Double calorieValue, String foodType, String measurementUnit, Double quantity) {
        this.name = name;
        this.nutritionType = nutritionType;
        this.calorieValue = calorieValue;
        this.foodType = foodType;
        this.measurementUnit = measurementUnit;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNutritionType() {
        return nutritionType;
    }

    public void setNutritionType(String nutritionType) {
        this.nutritionType = nutritionType;
    }

    public Double getCalorieValue() {
        return calorieValue;
    }

    public void setCalorieValue(Double calorieValue) {
        this.calorieValue = calorieValue;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
