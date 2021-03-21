package com.example.trainerguide.validation;

public class FoodValidation {

    private String whiteSpace = ".*\\\\S+.*";

    public String foodNameValidation(String foodName){

        String foodNameRegex = "[A-Za-z0-9]+";

        if (foodName.isEmpty()){
            return "Name cannot be empty";
        }
        else if (!foodName.matches(foodNameRegex) || !(foodName.length() >2)){
            return "Invalid name";
        }

        return "Valid";
    }

    public String foodNutritionValidation(String foodNutrition){

        String foodNameRegex = "[A-Za-z]+";

        if (foodNutrition.isEmpty()){
            return "Nutrition cannot be empty";
        }
        else if (!foodNutrition.matches(foodNameRegex) || !(foodNutrition.length() >2)){
            return "Invalid nutrition";
        }

        return "Valid";
    }

    public String foodCalorieValidation(Double foodCalorie) {

        if (foodCalorie==0.0){
            return "Calorie cannot be empty or zero";
        }
        return "Valid";
    }

    public String foodQuantityValidation(Double foodQuantity) {

        if (foodQuantity==0.0){
            return "Quantity cannot be empty or zero";
        }

        return "Valid";
    }
}
