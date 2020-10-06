package com.example.trainerguide.validation;

public class UserInputValidation {


    private String whiteSpace = ".*\\\\S+.*";


    public String emailValidation(String email){
        String emailRegex= "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (email.isEmpty()){
            return "Email address cannot be empty";
        }

        else if(!email.matches(emailRegex)){
            return "Invalid email address";
        }
        /*else if(email.trim().isEmpty()){
            return "White space is not allowed";
        }*/
        return "Valid";
    }

    public String passwordValidation(String password, String type){

        String upperCaseChars = "(.*[A-Z].*)";
        String lowerCaseChars = "(.*[a-z].*)";
        String specialChars = "(.*[@,#,$,%].*$)";
        String numbers = "(.*[0-9].*)";

        if (password.isEmpty()){
            return "Password cannot be empty";
        }
        else if ((password.length()<8) || ((password.length()>15))){
            return "Password must be more than 8 and less than 20 characters in length";
        }

        else if (password.matches(whiteSpace)){
            return "White space is not allowed";
        }
        else if(password.trim().isEmpty()){
            return "White space is not allowed";
        }

        if (type.equals("RegistrationPassword")){
            if (!password.matches(upperCaseChars)){
                return "Password must have atleast one uppercase character";
            }
            else if (!password.matches(lowerCaseChars)){
                return "Password must have atleast one lowercase character";
            }
            else if (!password.matches(numbers)){
                return "Password must have atleast one number";
            }
            else if (!password.matches(specialChars)){
                return "Password must have atleast one special character among @#$%";
            }
        }

        return "Valid";
    }

    public String userNameValidation(String userName){

        String userNameRegex = "[A-Za-z]+";

        if (userName.isEmpty()){
            return "Username cannot be empty";
        }
        else if ((userName.length()<=2) || (userName.length()>15)){
            return "Username must be more than 2 and less than 15 characters in length";
        }
        else if (userName.matches(whiteSpace)){
            return "White space is not allowed";
        }
        else if (!userName.matches(userNameRegex)){
            return "Invalid username";
        }

        return "Valid";
    }

    public String mobileNumberValidation(String mobileNumber){

        String characters = "[A-Za-z]";

        if (mobileNumber.toString().isEmpty()){
            return "Mobile number cannot be empty";
        }

        else if ((mobileNumber.toString().matches(characters)) && (mobileNumber.length()<10)){
            return "Invalid Mobile number";
        }

        return "Valid";
    }

    public  String otpNumberValidation(String otpNumber){
        String characters = "[A-Za-z]";

        if (otpNumber.toString().isEmpty()){
            return "OTP cannot be empty";
        }

        else if ((otpNumber.toString().matches(characters)) || (otpNumber.length()<6)){
            return "Invalid OTP number";
        }
        return "Valid";
    }


}
