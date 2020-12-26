package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.logging.Logger;

public class ForgotPasswordForm extends AppCompatActivity {

    private EditText txtEmail;
    private UserInputValidation userInputValidation;
    private FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_form);

        txtEmail = findViewById(R.id.userFgpEmail_Input);
        final Button btnFgp = findViewById(R.id.btnFgp);

        // FireBase Initialization
        fAuth = FirebaseAuth.getInstance();

        userInputValidation = new UserInputValidation();

        btnFgp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Forgot Password Link sending to Email
                fAuth.sendPasswordResetEmail(txtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordForm.this, "Password Link sent to your Email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            Toast.makeText(ForgotPasswordForm.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        });
    }


}