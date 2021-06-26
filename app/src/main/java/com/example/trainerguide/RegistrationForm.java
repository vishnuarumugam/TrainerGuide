package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.trainerguide.models.Trainee;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

public class RegistrationForm extends AppCompatActivity{

    private EditText name, email, mobileNumber, password, confirmPassword;
    private TextInputLayout txtLayPassword, txtLayConPassword;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button registerButton;
    private ImageButton userRgrPassHint;
    private static String userId;
    UserInputValidation userInputValidation;
    private boolean IsTrainerProfile;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private String imageDownloadUrl;
    private Uri fileUri;
    DatabaseReference databaseReference;
    FirebaseAuth fAuth;
    private boolean isPasswordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);
        // Get the passed Intent value from Profile Select Activity
        IsTrainerProfile = getIntent().getExtras().getBoolean("IsTrainer");

        // loading Animation from
        final Animation buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);


        // Initializing the Components
        name = findViewById(R.id.userRgrName_Input);
        email = findViewById(R.id.userRgrEmail_Input);
        mobileNumber = findViewById(R.id.userRgrMobile_Input);
        password = findViewById(R.id.userRgrPassword_Input);
        confirmPassword = findViewById(R.id.userRgrConPassword_Input);
        registerButton = findViewById(R.id.rgrButton);
        userRgrPassHint = findViewById(R.id.userRgrPassHint);
        /*txtLayPassword = findViewById(R.id.txtLayRgrPassword_Input);
        txtLayConPassword = findViewById(R.id.txtLayRgrConPassword_Input);*/
        storageReference = FirebaseStorage.getInstance().getReference("FitnessGuide");
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.check(R.id.radioBtnMale);

        fAuth = FirebaseAuth.getInstance();

        userInputValidation = new UserInputValidation();

        userRgrPassHint.setTooltipText("Password must contain one lower & upper case alphabet, one number and one among @#$");
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    password.setError(null);

                    if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[RIGHT].getBounds().width())) {
                        int selection = password.getSelectionEnd();
                        if (isPasswordVisible) {
                            // set drawable image
                            System.out.println("set drawable image visi of");

                            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                            // hide Password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isPasswordVisible = false;
                        } else  {
                            // set drawable image
                            System.out.println("set drawable image visi");
                            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                            // show Password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isPasswordVisible = true;
                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        /*password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //txtLayPassword.setPasswordVisibilityToggleEnabled(true);
                password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //txtLayConPassword.setPasswordVisibilityToggleEnabled(true);
                confirmPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButton.setEnabled(false);
                registerButton.startAnimation(buttonBounce);
                registerButton.setBackgroundColor(getResources().getColor(R.color.themeColourFour));
                if (RegistrationValidation()) {
                    Toast.makeText(RegistrationForm.this, "In", Toast.LENGTH_SHORT).show();
                    int selectedId=radioGroup.getCheckedRadioButtonId();
                    radioButton=(RadioButton)findViewById(selectedId);
                    // Firebase Authentication User Creation
                    fAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    //Upload Details in Firebase FirstTime
                                    databaseReference = FirebaseDatabase.getInstance().getReference();
                                    userId = fAuth.getCurrentUser().getUid();
                                    System.out.println(mobileNumber.getText().toString());
                                    // Upload Profile Picture into FireBase Database with Profile Picture metadata
                                    if (IsTrainerProfile) {
                                        Trainer trainer = new Trainer(userId, name.getText().toString(), radioButton.getText().toString(), Calendar.getInstance().getTime(), IsTrainerProfile, Calendar.getInstance().getTime(), "image", email.getText().toString(),Long.parseLong(mobileNumber.getText().toString()));
                                        /*List<String> healthIssues = new ArrayList<>();
                                        healthIssues.add("BloodPressure");
                                        healthIssues.add("Cholestrol");
                                        trainer.setHealthIssues(healthIssues);*/
                                        databaseReference.child("Trainer").child(userId).setValue(trainer);

                                        // Upload Profile Picture into FireBase Storage
                                        uploadFile(userId,trainer);

                                    } else {
                                        System.out.println(IsTrainerProfile);
                                        Trainee trainee = new Trainee(userId, name.getText().toString(), radioButton.getText().toString(), Calendar.getInstance().getTime(), IsTrainerProfile, Calendar.getInstance().getTime(), "image", email.getText().toString(),null,Long.parseLong(mobileNumber.getText().toString()));
                                        databaseReference.child("User").child(userId).setValue(trainee);

                                        // Upload Profile Picture into FireBase Storage
                                        uploadFile(userId,trainee);
                                    }

                                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("userId", fAuth.getCurrentUser().getUid());
                                    editor.putBoolean("IsLoggedIn",true);
                                    if(IsTrainerProfile) {
                                    editor.putString("ProfileType", "Trainer");}
                                    else {
                                        editor.putString("ProfileType", "User");}

                                    editor.commit();

                                    Intent intent = new Intent(RegistrationForm.this, HomeScreen.class);
                                    intent.putExtra("UserId",fAuth.getCurrentUser().getUid());
                                    startActivity(intent);

                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegistrationForm.this, "Profile Creation Failed", Toast.LENGTH_SHORT).show();
                            registerButton.setEnabled(true);
                            registerButton.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                        }
                    });
                } else {
                    registerButton.setEnabled(true);
                    registerButton.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
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

    private void uploadFile(final String userId, final Trainee user) {
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
                //txtLayConPassword.setPasswordVisibilityToggleEnabled(false);
                confirmPassword.setError("Confirm Password doesn't match with Password");
                return false;
            }
        } else {

            if (!nameValid.equals("Valid")) {
                name.setError(nameValid);
            }

            if (!passwordValid.equals("Valid")) {
                isPasswordVisible = false;
                //txtLayPassword.setPasswordVisibilityToggleEnabled(false);
                password.setError(passwordValid);
            }
            if (!mobileNumberValid.equals("Valid")) {
                mobileNumber.setError(mobileNumberValid);
            }
            if (!emailValid.equals("Valid")) {
                email.setError(emailValid);
            }
            if (confirmPassword.getText().toString().isEmpty()) {
                //txtLayConPassword.setPasswordVisibilityToggleEnabled(false);
                confirmPassword.setError("Password cannot be empty");
            }
            return false;

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrationForm.this, SelectProfileType.class);
        startActivity(intent);
        finish();
    }

}