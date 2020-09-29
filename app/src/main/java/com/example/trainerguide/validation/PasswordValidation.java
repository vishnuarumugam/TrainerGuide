package com.example.trainerguide.validation;

public class PasswordValidation {


    public String passwordValidation(String password){

        if (password.isEmpty()){
            return "User Password cannot be empty";
        }

        return "Valid";
    }
}
