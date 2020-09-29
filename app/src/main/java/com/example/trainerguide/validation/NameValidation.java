package com.example.trainerguide.validation;

public class NameValidation {

    public String nameValidation(String name){

        if (name.isEmpty()){
            return "User Email Id cannot be empty";
        }

        return "Valid";
    }
}
