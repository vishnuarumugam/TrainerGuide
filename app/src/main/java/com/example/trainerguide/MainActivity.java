package com.example.trainerguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText userEmailIn, userPasswordIn;
    private TextView createAccount, forgotPassword;
    private TextInputLayout txtLayPassword;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("login",MODE_PRIVATE);


        userEmailIn = findViewById(R.id.userEmail_Input);
        userPasswordIn = findViewById(R.id.userPassword_Input);
        createAccount = findViewById(R.id.txtCreateAccount);
        forgotPassword = findViewById(R.id.txtForgotPassword);
        fAuth = FirebaseAuth.getInstance();
        txtLayPassword = findViewById(R.id.lgn_txtLayPassword);

        Button loginButton = findViewById(R.id.btnLogin);

        /*if(fAuth!=null)
        {
            startActivity(new Intent(getApplicationContext(),HomeScreen.class));
        }*/


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( loginValidation() ){
                    fAuth.signInWithEmailAndPassword(userEmailIn.getText().toString().trim(),userPasswordIn.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                                    intent.putExtra("UserId",fAuth.getCurrentUser().getUid());

                                    String path = "Trainer";
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.hasChild(fAuth.getCurrentUser().getUid()))
                                            {

                                            }
                                            else
                                            {
                                                String path = "User";
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    });                }

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SelectProfileType.class));
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPasswordForm.class));
            }
        });


    }

    private boolean loginValidation() {

        UserInputValidation userInputValidation =  new UserInputValidation();

        String emailValid = userInputValidation.emailValidation(userEmailIn.getText().toString());
        String passwordValid = userInputValidation.passwordValidation(userPasswordIn.getText().toString(),"LoginPassword");

        if ( emailValid.equals("Valid") && passwordValid.equals("Valid") ){
            return true;
        }
        else{

            if ( !emailValid.equals("Valid")){
                userEmailIn.setError(emailValid);
            }

            if ( !passwordValid.equals("Valid")){
                txtLayPassword.setPasswordVisibilityToggleEnabled(false);
                userPasswordIn.setError(passwordValid);
            }
        }
        return false;
    }


}