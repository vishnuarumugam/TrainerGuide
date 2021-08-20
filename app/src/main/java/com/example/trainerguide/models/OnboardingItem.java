package com.example.trainerguide.models;

import android.widget.ImageView;

public class OnboardingItem {

    private int image;
    private String title;
    private String subTitle;
    private String description;

    public OnboardingItem() {
    }

    public OnboardingItem(int image, String title, String subTitle, String description) {
        this.image = image;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
