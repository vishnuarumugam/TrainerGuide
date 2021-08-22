package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.User;
import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

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
    private CheckBox checkbox;
    private Context context;
    private RelativeLayout registerLayout;
    private LinearLayout loginLayout, noInternetLayout;
    private Button noInternetButton;

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
        checkbox = findViewById(R.id.checkbox);

        registerLayout = findViewById(R.id.registerLayout);
        loginLayout = findViewById(R.id.loginLayout);
        noInternetLayout = findViewById(R.id.noInternetLayout);
        noInternetButton = findViewById(R.id.noInternetButton);

        Button loginButton = findViewById(R.id.btnLogin);

        final SharedPreferences sp;
        sp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean status = sp.getBoolean("IsLoggedIn",false);




        if (networkConnection()){
            if(status)
            {
                loginLayout.setVisibility(View.VISIBLE);
                registerLayout.setVisibility(View.VISIBLE);
                noInternetLayout.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(),HomeScreen.class));
                finish();
            }
        }
        else{
            loginLayout.setVisibility(View.INVISIBLE);
            registerLayout.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
        }


        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                if (value)
                {
                    // Show Password
                    userPasswordIn.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    // Hide Password
                    userPasswordIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        noInternetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(0,0);
                finish();

            }
        });

       /* userPasswordIn.setOnTouchListener(new View.OnTouchListener() {
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
        });*/

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.startAnimation(buttonBounce);
                loginButton.setEnabled(false);
                loginButton.setBackgroundColor(getResources().getColor(R.color.themeColourFour));

                if (loginValidation()) {
                    fAuth.signInWithEmailAndPassword(userEmailIn.getText().toString().trim(), userPasswordIn.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // Check if Email is verified
                                    if (fAuth.getCurrentUser().isEmailVerified()) {

                                        //String path = "Trainer/";
                                        databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference("Trainer");

                                        databaseReferenceTrainer.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                System.out.println("********OnDataChange*******");
                                                SharedPreferences.Editor editor = sp.edit();


                                                if (snapshot.hasChild(fAuth.getCurrentUser().getUid())) {
                                                    profileType = "Trainer";
                                                } else {
                                                    profileType = "User";
                                                }
                                                databaseReference = FirebaseDatabase.getInstance().getReference(profileType + "/" + fAuth.getCurrentUser().getUid());

                                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        User user = snapshot.getValue(User.class);

                                                        editor.putString("UserName", user.getName());

                                                        if (profileType.equals("User")){
                                                            Trainee trainee = snapshot.getValue(Trainee.class);
                                                            editor.putString("UserGoal", trainee.getSubscriptionType());
                                                        }

                                                        if( user.getIsAdmin() == null ){
                                                            HashMap hash= new HashMap();
                                                            hash.put("isAdmin", "0");
                                                            editor.putString("isAdmin","0");
                                                            databaseReference.updateChildren(hash);
                                                        }
                                                        else if( user.getIsAdmin().equals("1") ){
                                                            editor.putString("isAdmin","1");
                                                        }
                                                        else {
                                                            editor.putString("isAdmin","0");
                                                        }

                                                        editor.putString("userId", fAuth.getCurrentUser().getUid());
                                                        editor.putBoolean("IsLoggedIn", true);
                                                        editor.putString("ProfileType", profileType);
                                                        editor.commit();


                                                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                                        intent.putExtra("UserId", fAuth.getCurrentUser().getUid());

                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                System.out.println("DatabaseError");

                                            }
                                        });
                                    }
                                    else // Send Email for verification for not verified Emails
                                    {
                                        fAuth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            loginButton.setEnabled(true);
                                                            loginButton.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                                                            userPasswordIn.setText("");
                                                            Toast.makeText(MainActivity.this, "We have sent an email to your email address. Please verify to login", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loginButton.setEnabled(true);
                            loginButton.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                            Toast.makeText(MainActivity.this, "Please provide valid User name and Password", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    loginButton.setEnabled(true);
                    loginButton.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                }
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,OnBoardingScreen.class));
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

    private boolean networkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
                return connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
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