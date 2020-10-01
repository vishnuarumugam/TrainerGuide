package com.example.trainerguide;

public class Trainee {

    private String name;
    private Double bmr;
    private String image;

    public Trainee(String name, Double bmr, String image) {
        this.name = name;
        this.bmr = bmr;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Trainee{" +
                "name='" + name + '\'' +
                ", bmr=" + bmr +
                ", image='" + image + '\'' +
                '}';
    }
}
