package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private ConstraintLayout loginView;
    private TextInputLayout txtLayPassword;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    DatabaseReference databaseReferenceTrainer, databaseReference;
    private String profileType;
    private boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // loading Animation from
        final Animation buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);
        userEmailIn = findViewById(R.id.userEmail_Input);
        userPasswordIn = findViewById(R.id.userPassword_Input);
        createAccount = findViewById(R.id.txtCreateAccount);
        forgotPassword = findViewById(R.id.txtForgotPassword);
        fAuth = FirebaseAuth.getInstance();
        //txtLayPassword = findViewById(R.id.lgn_txtLayPassword);

        Button loginButton = findViewById(R.id.btnLogin);

        /*loginView = findViewById(R.id.loginView);

        ScrollView scrollView = new ScrollView(getApplicationContext());
        //scrollView.addView(loginView);
        loginView.canScrollVertically(v);
        //setContentView(scrollView);*/


        final SharedPreferences sp;
        sp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //sp = getSharedPreferences("IsLoggedIn",MODE_PRIVATE);
        Boolean status = sp.getBoolean("IsLoggedIn",false);
        System.out.println("status"+status);

        if(status)
            {
                startActivity(new Intent(getApplicationContext(),HomeScreen.class));
            finish();
        }

        /*if(fAuth!=null)
        {
            startActivity(new Intent(getApplicationContext(),HomeScreen.class));
        }*/

        userPasswordIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (userPasswordIn.getRight() - userPasswordIn.getCompoundDrawables()[RIGHT].getBounds().width())) {
                        int selection = userPasswordIn.getSelectionEnd();
                        if (isPasswordVisible) {
                            // set drawable image
                            userPasswordIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                            // hide Password
                            userPasswordIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isPasswordVisible = false;
                        } else  {
                            // set drawable image
                            userPasswordIn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                            // show Password
                            userPasswordIn.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isPasswordVisible = true;
                        }
                        userPasswordIn.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });


   /*     userPasswordIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtLayPassword.setPasswordVisibilityToggleEnabled(true);
                userPasswordIn.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.startAnimation(buttonBounce);
                loginButton.setEnabled(false);
                loginButton.setBackgroundColor(getResources().getColor(R.color.themeColourFour));

                if ( loginValidation() ){
                    fAuth.signInWithEmailAndPassword(userEmailIn.getText().toString().trim(),userPasswordIn.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    //String path = "Trainer/";
                                    databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference("Trainer");

                                    databaseReferenceTrainer.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            System.out.println("********OnDataChange*******");

                                            if(snapshot.hasChild(fAuth.getCurrentUser().getUid())){
                                                profileType = "Trainer";
                                            }
                                            else{
                                                profileType = "User";
                                            }
                                            databaseReference = FirebaseDatabase.getInstance().getReference(profileType+"/"+fAuth.getCurrentUser().getUid());

                                            SharedPreferences.Editor editor = sp.edit();
                                            editor.putString("userId", fAuth.getCurrentUser().getUid());
                                            editor.putBoolean("IsLoggedIn",true);
                                            editor.putString("ProfileType", profileType);
                                            editor.commit();


                                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                                            intent.putExtra("UserId",fAuth.getCurrentUser().getUid());

                                            startActivity(intent);
                                            finish();


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            System.out.println("DatabaseError");

                                        }
                                    });

                                    /*try {

                                    }catch(Exception ex){
                                        System.out.println("catch");
                                    }

                                    try {
                                        databaseReferenceTrainee = FirebaseDatabase.getInstance().getReference().child("User");
                                        System.out.println("InUser");

                                    }
                                    catch (Exception ex){}*/
                                    //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    /*SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("userId", fAuth.getCurrentUser().getUid());
                                    editor.putBoolean("IsLoggedIn",true);

                                    finish();*/

//                                    databaseReferenceTrainer.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            if(snapshot.hasChild(fAuth.getCurrentUser().getUid())){
//                                                profileType = "Trainer";
//                                            }else{
//                                                databaseReferenceTrainee.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                        if(snapshot.hasChild(fAuth.getCurrentUser().getUid())){
//                                                            profileType = "Trainee";
//                                                        }else{
//                                                            Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//                                            System.out.println("cancel");
//
//                                        }
//                                    });

                                    /*SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("userId", fAuth.getCurrentUser().getUid());
                                    editor.putBoolean("IsLoggedIn",true);
                                    editor.putString("ProfileType", profileType);
                                    editor.commit();


                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                                    intent.putExtra("UserId",fAuth.getCurrentUser().getUid());

                                    startActivity(intent);
                                    finish();
*/
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loginButton.setEnabled(true);
                            loginButton.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                            Toast.makeText(MainActivity.this, "Please provide valid User Name and Password", Toast.LENGTH_SHORT).show();
                        }
                    });                }

                else{
                    loginButton.setEnabled(true);
                    loginButton.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                }

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
                finish();
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
                //txtLayPassword.setPasswordVisibilityToggleEnabled(false);
                userPasswordIn.setError(passwordValid);
            }
        }
        return false;
    }


}