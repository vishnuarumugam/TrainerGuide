package com.example.trainerguide.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodList {
    private List<Food> foodItemsVeg = new ArrayList<>();
    private List<Food> foodItemsNonVeg = new ArrayList<>();

    public List<Food> getFoodItemsVeg() {
        return foodItemsVeg;
    }

    public List<Food> getFoodItemsNonVeg() {
        return foodItemsNonVeg;
    }

    public void setFoodItemsVeg(Food foodItemsVeg) {
        this.foodItemsVeg.add(foodItemsVeg);
    }

    public void setFoodItemsNonVeg(Food foodItemsNonVeg) {
        this.foodItemsNonVeg.add(foodItemsNonVeg);
    }
}
