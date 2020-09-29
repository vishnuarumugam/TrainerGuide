package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationForm extends AppCompatActivity {

    private EditText name,  email, mobileNumber, password, confirmPassword;
    private TextInputLayout txtLayPassword, txtLayConPassword;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);

        name = findViewById(R.id.userRgrName_Input);
        email = findViewById(R.id.userRgrEmail_Input);
        mobileNumber = findViewById(R.id.userRgrMobile_Input);
        password = findViewById(R.id.userRgrPassword_Input);
        confirmPassword = findViewById(R.id.userRgrConPassword_Input);

        registerButton = findViewById(R.id.rgrButton);
        txtLayPassword = findViewById(R.id.txtLayRgrPassword_Input);
        txtLayConPassword = findViewById(R.id.txtLayRgrConPassword_Input);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( RegistrationValidation() ){
                    Toast.makeText(RegistrationForm.this, "In", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrationForm.this,HomeScreen.class));
                }
                else {
                    Toast.makeText(RegistrationForm.this, "Out", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private Boolean RegistrationValidation() {

        UserInputValidation userInputValidation =  new UserInputValidation();

        String nameValid = userInputValidation.userNameValidation(name.getText().toString());
        String emailValid = userInputValidation.emailValidation(email.getText().toString());
        String mobileNumberValid = userInputValidation.mobileNumberValidation(mobileNumber.getText().toString());
        String passwordValid = userInputValidation.passwordValidation(password.getText().toString(),"RegistrationPassword");

        System.out.println("nameValid"+nameValid+" emailValid"+emailValid+" mobileNumberValid"+mobileNumberValid+"passwordValid"+passwordValid);

        if ( nameValid.equals("Valid") && passwordValid.equals("Valid") && emailValid.equals("Valid") && mobileNumberValid.equals("Valid")){
            Toast.makeText(RegistrationForm.this, "RegistrationValidation", Toast.LENGTH_SHORT).show();
            if (password.getText().toString().equals(confirmPassword.getText().toString())){
                Toast.makeText(RegistrationForm.this, "InIn", Toast.LENGTH_SHORT).show();
                return true;
            }
            else {
                txtLayConPassword.setPasswordVisibilityToggleEnabled(false);
                confirmPassword.setError("Confirm Password doesn't match with Password");
            }
        }
        else{

            if (!nameValid.equals("Valid")){
                name.setError(nameValid);
            }

            if (!passwordValid.equals("Valid")){
                txtLayPassword.setPasswordVisibilityToggleEnabled(false);
                password.setError(passwordValid);
            }
            if (!mobileNumberValid.equals("Valid")){
                mobileNumber.setError(mobileNumberValid);
            }
            if (!emailValid.equals("Valid")){
                email.setError(emailValid);
            }
            if (confirmPassword.getText().toString().isEmpty()){
                txtLayConPassword.setPasswordVisibilityToggleEnabled(false);
                confirmPassword.setError("Password cannot be empty");
            }
            return true;

        }

        return false;
    }


}