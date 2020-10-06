package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trainerguide.models.BmrProgress;
import com.example.trainerguide.models.User;
import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistrationForm extends AppCompatActivity{

    private EditText name,  email, mobileNumber, password, confirmPassword;
    private TextInputLayout txtLayPassword, txtLayConPassword;
    private Button registerButton;
    private static String userId;
    UserInputValidation userInputValidation;
    private boolean IsTrainerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);
        boolean value = getIntent().getExtras().getBoolean("IsTrainer");
        name = findViewById(R.id.userRgrName_Input);
        email = findViewById(R.id.userRgrEmail_Input);
        mobileNumber = findViewById(R.id.userRgrMobile_Input);
        password = findViewById(R.id.userRgrPassword_Input);
        confirmPassword = findViewById(R.id.userRgrConPassword_Input);

        registerButton = findViewById(R.id.rgrButton);
        txtLayPassword = findViewById(R.id.txtLayRgrPassword_Input);
        txtLayConPassword = findViewById(R.id.txtLayRgrConPassword_Input);

        userInputValidation = new UserInputValidation();

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                txtLayPassword.setPasswordVisibilityToggleEnabled(true);
                password.setError(null);
                return false;
            }
        });

        confirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                txtLayConPassword.setPasswordVisibilityToggleEnabled(true);
                confirmPassword.setError(null);
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( RegistrationValidation() ){
                    Toast.makeText(RegistrationForm.this, "In", Toast.LENGTH_SHORT).show();

                    //Upload Details in Firebase FirstTime
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                    userId=name.getText().toString()+System.currentTimeMillis();
                    BmrProgress bmrProgress = new BmrProgress(Calendar.getInstance().getTime(),1.00);
                    List<BmrProgress> lstBmrProg = new ArrayList<>();
                    lstBmrProg.add(bmrProgress);
                    User user = new User(userId,name.getText().toString(),0.00,Calendar.getInstance().getTime(),"Male",0.00,0.00, Calendar.getInstance().getTime(),false,Calendar.getInstance().getTime(),"image",email.getText().toString(),lstBmrProg);
                    databaseReference.child(userId).setValue(user);
                    startActivity(new Intent(RegistrationForm.this,HomeScreen.class));
                }
                else {
                    Toast.makeText(RegistrationForm.this, "Out", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private Boolean RegistrationValidation() {

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
                return false;
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
            return false;

        }
    }


}