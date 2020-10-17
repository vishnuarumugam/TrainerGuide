package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trainerguide.models.BmrProgress;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistrationForm extends AppCompatActivity{

    private EditText name, email, mobileNumber, password, confirmPassword;
    private TextInputLayout txtLayPassword, txtLayConPassword;
    private Button registerButton;
    private static String userId;
    UserInputValidation userInputValidation;
    private boolean IsTrainerProfile;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private String imageDownloadUrl;
    private Uri fileUri;
    DatabaseReference databaseReference;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);
        // Get the passed Intent value from Profile Select Activity
        IsTrainerProfile = getIntent().getExtras().getBoolean("IsTrainer");

        // Initializing the Components
        name = findViewById(R.id.userRgrName_Input);
        email = findViewById(R.id.userRgrEmail_Input);
        mobileNumber = findViewById(R.id.userRgrMobile_Input);
        password = findViewById(R.id.userRgrPassword_Input);
        confirmPassword = findViewById(R.id.userRgrConPassword_Input);
        registerButton = findViewById(R.id.rgrButton);
        txtLayPassword = findViewById(R.id.txtLayRgrPassword_Input);
        txtLayConPassword = findViewById(R.id.txtLayRgrConPassword_Input);
        storageReference = FirebaseStorage.getInstance().getReference("FitnessGuide");

        fAuth = FirebaseAuth.getInstance();

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

                if (RegistrationValidation()) {
                    Toast.makeText(RegistrationForm.this, "In", Toast.LENGTH_SHORT).show();

                    // Firebase Authentication User Creation
                    fAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    //Upload Details in Firebase FirstTime
                                    databaseReference = FirebaseDatabase.getInstance().getReference();
                                    userId = fAuth.getCurrentUser().getUid();

                                    // Upload Profile Picture into FireBase Database with Profile Picture metadata
                                    if (IsTrainerProfile) {
                                        Trainer trainer = new Trainer(userId, name.getText().toString(), "Male", Calendar.getInstance().getTime(), IsTrainerProfile, Calendar.getInstance().getTime(), "image", email.getText().toString());
                                        /*List<String> healthIssues = new ArrayList<>();
                                        healthIssues.add("BloodPressure");
                                        healthIssues.add("Cholestrol");
                                        trainer.setHealthIssues(healthIssues);*/
                                        databaseReference.child("Trainer").child(userId).setValue(trainer);

                                        // Upload Profile Picture into FireBase Storage
                                        uploadFile(userId,trainer);

                                    } else {
                                        User user = new User(userId, name.getText().toString(), "Male", Calendar.getInstance().getTime(), IsTrainerProfile, Calendar.getInstance().getTime(), "image", email.getText().toString());
                                        databaseReference.child("User").child(userId).setValue(user);

                                        // Upload Profile Picture into FireBase Storage
                                        uploadFile(userId,user);
                                    }
                                    Intent intent = new Intent(RegistrationForm.this, HomeScreen.class);
                                    intent.putExtra("UserId",fAuth.getCurrentUser().getUid());
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistrationForm.this, "Profile Creation Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(RegistrationForm.this, "Out", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Resolve the extension of the Image File selected
    private String getExtension(Uri uri) {
        ContentResolver CR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(CR.getType(fileUri));
    }

    private void uploadFile(final String userId, final Trainer trainer) {
        // Pick the default Image from the MipMap Folder
        // Path under FireBase Storage --> FitnessGuide/(Trainer|User)/UserId.(jpg|png)

        Uri file = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.mipmap.profile);
        final StorageReference fileReference;
        fileUri = Uri.fromFile(new File(file.getPath()));
        if (fileUri != null) {
            fileReference = storageReference.child("Trainer").child(userId + "." + getExtension(file));
            uploadTask = fileReference.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    trainer.setImage(task.getResult().toString());
                                    databaseReference.child("Trainer").child(userId).setValue(trainer);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistrationForm.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "image File not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile(final String userId, final User user) {
        // Pick the default Image from the MipMap Folder
        // Path under FireBase Storage --> FitnessGuide/(Trainer|User)/UserId.(jpg|png)

        Uri file = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.mipmap.profile);
        final StorageReference fileReference;
        fileUri = Uri.fromFile(new File(file.getPath()));
        if (fileUri != null) {
            fileReference = storageReference.child("User").child(userId + "." + getExtension(file));
            uploadTask = fileReference.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    user.setImage(task.getResult().toString());
                                    databaseReference.child("User").child(userId).setValue(user);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistrationForm.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "image File not available", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean RegistrationValidation() {

        String nameValid = userInputValidation.userNameValidation(name.getText().toString());
        String emailValid = userInputValidation.emailValidation(email.getText().toString());
        String mobileNumberValid = userInputValidation.mobileNumberValidation(mobileNumber.getText().toString());
        String passwordValid = userInputValidation.passwordValidation(password.getText().toString(), "RegistrationPassword");

        System.out.println("nameValid" + nameValid + " emailValid" + emailValid + " mobileNumberValid" + mobileNumberValid + "passwordValid" + passwordValid);

        if (nameValid.equals("Valid") && passwordValid.equals("Valid") && emailValid.equals("Valid") && mobileNumberValid.equals("Valid")) {
            Toast.makeText(RegistrationForm.this, "RegistrationValidation", Toast.LENGTH_SHORT).show();
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(RegistrationForm.this, "InIn", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                txtLayConPassword.setPasswordVisibilityToggleEnabled(false);
                confirmPassword.setError("Confirm Password doesn't match with Password");
                return false;
            }
        } else {

            if (!nameValid.equals("Valid")) {
                name.setError(nameValid);
            }

            if (!passwordValid.equals("Valid")) {
                txtLayPassword.setPasswordVisibilityToggleEnabled(false);
                password.setError(passwordValid);
            }
            if (!mobileNumberValid.equals("Valid")) {
                mobileNumber.setError(mobileNumberValid);
            }
            if (!emailValid.equals("Valid")) {
                email.setError(emailValid);
            }
            if (confirmPassword.getText().toString().isEmpty()) {
                txtLayConPassword.setPasswordVisibilityToggleEnabled(false);
                confirmPassword.setError("Password cannot be empty");
            }
            return false;

        }
    }
}