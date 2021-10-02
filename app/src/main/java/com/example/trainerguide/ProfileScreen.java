package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.BmrProgress;
import com.example.trainerguide.models.Notification;
import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.validation.UserInputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.round;

public class ProfileScreen extends AppCompatActivity implements View.OnClickListener {

    // Reqd variables for Image File choosing and Storing
    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    //Firestore
    private StorageReference storageReference;

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button toolBarNotification;
    public String notificationFlag="";
    private TextView toolBarBadge;
    private ProgressDialog progressDialog;


    //Trainer Data
    private static Trainer trainer = new Trainer();


    //ProfileScreen Variables
    private ImageButton profileImage;
    private TextView profileUsername, traineesNumber, trainerRating, profileType, profilePhoneNumber, profileEmailId, profileGender, profileDob, profileExperience, profileSubscriptionFees, profileSubscriptionDescription, profileSubscriptionTrainer;
    private RelativeLayout  dobRelativeLay, experienceRelativeLay, subscriptionTrainerRelativeLay, subscriptionFeesRelativeLay, subscriptionDescriptionRelativeLay, subscriptionExtendRelativeLay, profilePhoneRelativeLay, profileResetPassRelativeLay, profileAdRelativeLay;
    private TabLayout profileTabLayout;
    private RelativeLayout accountTabView, profileTabView, profileLogoutLayout, profileTraineeDetailsLayout, profileTrainerDetailsLayout;
    private LinearLayout ratingTraineesLayout;
    private Button extend, subscriptionRemoveBtn;

    //Common variables
    private Intent intent;
    private Date currentDate = new Date();
    private String userId;
    private String path;
    private String userProfileUpdateValue;
    private User user;
    private String navigationScreen ="";
    private String userType, isAdmin;
    private Boolean IsTrainerProfile;
    Animation buttonBounce;
    private BottomNavigationView homeScreenTabLayout;
    private SharedPreferences sp;



    //PopUp Dialog
    Dialog profileDialog;
    ImageView profileDialogClose;
    TextView profileDobDialogTitle, profileExperienceDialogTitle,  profileSubscriptionFeesDialogTitle, profileSubscriptionDescDialogTitle, txtSubscriptionDate;
    LinearLayout profileDobDialogTitleLin,  profileExperienceDialogTitleLin, profileSubscriptionFeesDialogTitleLin, profileSubscriptionDescDialogTitleLin, profileMobileDialogTitleLin;
    DatePicker profileDobDialogDatePicker;
    Button profileDobDialogUpdate,  profileExperienceDialogUpdate,  profileSubscriptionFeesDialogUpdate, profileSubscriptionDescDialogUpdate, profileMobileDialogUpdate;
    EditText  profileExperienceDialogInput, profileSubscriptionFeesDialogInput, profileSubscriptionDescDialogInput, profileMobileDialogInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        //File Storage variables
        storageReference = FirebaseStorage.getInstance().getReference();


        //Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);

        //Navigation view variables
        drawerLayout = findViewById(R.id.profile_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolBarBadge = findViewById(R.id.toolBarBadge);

        if (getIntent().hasExtra("notificationFlag")){
            notificationFlag = getIntent().getExtras().getString("notificationFlag", "");
        }

        if (notificationFlag.equals("present"))
            toolBarBadge.setVisibility(View.VISIBLE);
        else
            toolBarBadge.setVisibility(View.INVISIBLE);


        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        toolbar.setTitleTextColor(getResources().getColor(R.color.themeColourThree));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));

        //ProfileScreen variables
        profileImage = findViewById(R.id.profileImage);
        profileTabLayout = findViewById(R.id.profileTabLayout);
        ratingTraineesLayout = findViewById(R.id.ratingTraineesLayout);
        profileTraineeDetailsLayout = findViewById(R.id.profileTraineeDetailsLayout);
        profileTrainerDetailsLayout = findViewById(R.id.profileTrainerDetailsLayout);

        ratingTraineesLayout.setVisibility(View.GONE);
        profileTrainerDetailsLayout.setVisibility(View.GONE);
        profileTraineeDetailsLayout.setVisibility(View.GONE);


        extend = findViewById(R.id.extendbtn);
        subscriptionRemoveBtn = findViewById(R.id.subscriptionRemoveBtn);
        txtSubscriptionDate = findViewById(R.id.txtSubscriptionDate);

        profileType = findViewById(R.id.profileType);
        profileUsername = findViewById(R.id.profileUsername);
        traineesNumber = findViewById(R.id.traineesNumber);
        trainerRating = findViewById(R.id.trainerRating);
        profileExperience = findViewById(R.id.profileExperience);
        profileEmailId = findViewById(R.id.profileEmailId);
        profileGender = findViewById(R.id.profileGender);
        profilePhoneNumber= findViewById(R.id.profilePhoneNumber);
        profileDob = findViewById(R.id.profileDob);
        profileSubscriptionTrainer = findViewById(R.id.profileSubscriptionTrainer);
        profileSubscriptionFees = findViewById(R.id.profileSubscriptionFees);
        profileSubscriptionDescription = findViewById(R.id.profileSubscriptionDescription);
        dobRelativeLay = findViewById(R.id.dobRelativeLay);
        experienceRelativeLay = findViewById(R.id.experienceRelativeLay);

        //Subscription Info variables
        subscriptionTrainerRelativeLay = findViewById(R.id.subscriptionTrainerRelativeLay);
        subscriptionFeesRelativeLay = findViewById(R.id.subscriptionFeesRelativeLay);
        subscriptionDescriptionRelativeLay = findViewById(R.id.subscriptionDescriptionRelativeLay);
        subscriptionExtendRelativeLay = findViewById(R.id.subscriptionExtendRelativeLay);
        profilePhoneRelativeLay = findViewById(R.id.profilePhoneRelativeLay);
        profileResetPassRelativeLay = findViewById(R.id.profileResetPassRelativeLay);
        subscriptionExtendRelativeLay.setVisibility(View.GONE);
        profileAdRelativeLay = findViewById(R.id.profileAdRelativeLay);
        profileAdRelativeLay.setOnClickListener(this);





        //User Info variables
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        IsTrainerProfile = false;
        String passedUserId = "";
        userType = sp.getString("ProfileType", null);
        userId = sp.getString("userId", null);


        if (getIntent().hasExtra("Screen")){
            navigationScreen = getIntent().getExtras().getString("Screen", "");
        }

        path = userType+ "/" + userId;

        checkNotification(path);

        if (userType.equals("Trainer")){
            ratingTraineesLayout.setVisibility(View.VISIBLE);
            profileTrainerDetailsLayout.setVisibility(View.VISIBLE);
            profileTraineeDetailsLayout.setVisibility(View.GONE);
        }
        else {
            ratingTraineesLayout.setVisibility(View.GONE);
            profileTrainerDetailsLayout.setVisibility(View.GONE);
            profileTraineeDetailsLayout.setVisibility(View.VISIBLE);

        }

        extend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extend.setEnabled(false);
                extend.setBackgroundColor(getResources().getColor(R.color.themeColourFour));
                extend.startAnimation(buttonBounce);
                AlertDialog dialog = new AlertDialog.Builder(ProfileScreen.this).create();
                dialog.setMessage(getResources().getString(R.string.extendSubscription));
                dialog.setCancelable(true);

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Trainee trainee = snapshot.getValue(Trainee.class);
                                Notification notify = new Notification();
                                notify.setNotificationId(UUID.randomUUID().toString());
                                notify.setNotification(trainee.getName() + " requested for extending subscription for 30 Days");
                                notify.setNotificationHeader("Extend subscription request notification");
                                notify.setAddedDate(Calendar.getInstance().getTime());
                                notify.setNotificationType("Extend");
                                notify.setTrainer(false);
                                notify.setUserId(trainee.getUserId());
                                DatabaseReference trainerDatabaseReference = FirebaseDatabase.getInstance().getReference("Trainer/"+trainee.getTrainerId()+"/Notification/"+notify.getNotificationId());

                                trainerDatabaseReference.setValue(notify);

                                Toast.makeText(ProfileScreen.this, "Extend Request sent to Trainer", Toast.LENGTH_SHORT).show();

                                //startActivity(new Intent(ProfileScreen.this,ProfileScreen.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        extend.setEnabled(true);
                        extend.setBackgroundColor(getResources().getColor(R.color.themeColourTwo));
                    }
                });
                dialog.show();





            }
        });

        subscriptionRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscriptionRemoveBtn.setEnabled(false);
                subscriptionRemoveBtn.setBackgroundColor(getResources().getColor(R.color.themeColourFour));
                subscriptionRemoveBtn.startAnimation(buttonBounce);


                AlertDialog dialog = new AlertDialog.Builder(ProfileScreen.this).create();
                dialog.setMessage(getResources().getString(R.string.removeSubscription));
                dialog.setCancelable(true);

                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Trainee trainee = snapshot.getValue(Trainee.class);
                                Notification notify = new Notification();
                                notify.setNotificationId(UUID.randomUUID().toString());
                                notify.setNotification(trainee.getName() + " requested for ending the subscription");
                                notify.setNotificationHeader("Remove subscription request notification");
                                notify.setAddedDate(Calendar.getInstance().getTime());
                                notify.setNotificationType("Remove");
                                notify.setTrainer(false);
                                notify.setUserId(trainee.getUserId());
                                DatabaseReference trainerDatabaseReference = FirebaseDatabase.getInstance().getReference("Trainer/"+trainee.getTrainerId()+"/Notification/"+notify.getNotificationId());

                                trainerDatabaseReference.setValue(notify);

                                Toast.makeText(ProfileScreen.this, "Remove request sent to Trainer", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        subscriptionRemoveBtn.setEnabled(true);

                    }
                });
                dialog.show();



            }
        });


        //Get User Details
        PopulateUserDetails();

        //PopUp Dialog
        profileDialog = new Dialog(this);
        profileDialog.setContentView(R.layout.profile_screen_dialog);
        profileDialogClose = profileDialog.findViewById(R.id.profileDialogClose);

        profileDobDialogTitleLin = profileDialog.findViewById(R.id.profileDobDialogTitleLin);
        profileExperienceDialogTitleLin = profileDialog.findViewById(R.id.profileExperienceDialogTitleLin);
        profileSubscriptionFeesDialogTitleLin = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogTitleLin);
        profileSubscriptionDescDialogTitleLin = profileDialog.findViewById(R.id.profileSubscriptionDescDialogTitleLin);

        profileDobDialogTitle = profileDialog.findViewById(R.id.profileDobDialogTitle);
        profileDobDialogUpdate = profileDialog.findViewById(R.id.profileDobDialogUpdate);
        profileDobDialogDatePicker = profileDialog.findViewById(R.id.profileDobDialogDatePicker);

        profileExperienceDialogTitle = profileDialog.findViewById(R.id.profileExperienceDialogTitle);
        profileExperienceDialogUpdate = profileDialog.findViewById(R.id.profileExperienceDialogUpdate);
        profileExperienceDialogInput = profileDialog.findViewById(R.id.profileExperienceDialogInput);

        profileMobileDialogTitleLin = profileDialog.findViewById(R.id.profileMobileDialogTitleLin);
        profileMobileDialogUpdate = profileDialog.findViewById(R.id.profileMobileDialogUpdate);
        profileMobileDialogInput = profileDialog.findViewById(R.id.profileMobileDialogInput);

        profileSubscriptionFeesDialogTitle = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogTitle);
        profileSubscriptionFeesDialogInput = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogInput);
        profileSubscriptionFeesDialogUpdate = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogUpdate);
        profileSubscriptionDescDialogTitle = profileDialog.findViewById(R.id.profileSubscriptionDescDialogTitle);
        profileSubscriptionDescDialogInput = profileDialog.findViewById(R.id.profileSubscriptionDescDialogInput);
        profileSubscriptionDescDialogUpdate = profileDialog.findViewById(R.id.profileSubscriptionDescDialogUpdate);

        //Dialog Pop - up Listener
        dobRelativeLay.setOnClickListener(this);
        experienceRelativeLay.setOnClickListener(this);
        subscriptionTrainerRelativeLay.setOnClickListener(this);
        subscriptionFeesRelativeLay.setOnClickListener(this);
        subscriptionDescriptionRelativeLay.setOnClickListener(this);
        profilePhoneRelativeLay.setOnClickListener(this);
        profileResetPassRelativeLay.setOnClickListener(this);

        //ToolBar
        toolBarNotification.setOnClickListener(this);

        //Dialog update
        profileDobDialogUpdate.setOnClickListener(this);
        profileExperienceDialogUpdate.setOnClickListener(this);
        profileMobileDialogUpdate.setOnClickListener(this);
        profileSubscriptionFeesDialogUpdate.setOnClickListener(this);
        profileSubscriptionDescDialogUpdate.setOnClickListener(this);

        //Update profile picture
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImage.startAnimation(buttonBounce);

                    FileChooser();
            }
        });


        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        break;

                    case R.id.nav_notification:
                        startActivity(new Intent(ProfileScreen.this,NotificationScreen.class));
                        finish();
                        break;

                    case R.id.nav_logout:
                        startActivity(new Intent(ProfileScreen.this,MainActivity.class));
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("userId");
                        editor.remove("ProfileType");
                        editor.remove("IsLoggedIn");
                        //editor.putBoolean("IsLoggedIn",false);
                        editor.commit();
                        finish();
                        break;
                    default:
                        Toast.makeText(ProfileScreen.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        homeScreenTabLayout = findViewById(R.id.homeScreenTabLayout);
        isAdmin = sp.getString("isAdmin",null);

        if (userType.equals("Trainer")){

        }
        else{
            /*homeScreenTabLayout.getMenu().removeItem(R.id.foodListTab);
            homeScreenTabLayout.getMenu().removeItem(R.id.traineesTab);
*/
            if (isAdmin.equals("0") || isAdmin==null){
                homeScreenTabLayout.getMenu().removeItem(R.id.foodListTab);
            }
            homeScreenTabLayout.getMenu().removeItem(R.id.traineesTab);

        }
        homeScreenTabLayout.setSelectedItemId(R.id.profileTab);
        homeScreenTabLayout.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.homeTab:
                        //startActivity(new Intent(ProfileScreen.this,HomeScreen.class));
                        intent = new Intent(ProfileScreen.this,HomeScreen.class);
                        intent.putExtra("Screen", "ProfileScreen");
                        intent.putExtra("notificationFlag", notificationFlag);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.trainersTab:
                        startActivity(new Intent(ProfileScreen.this,TrainerScreen.class).putExtra("notificationFlag", notificationFlag));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.traineesTab:
                        startActivity(new Intent(ProfileScreen.this,TraineesScreen.class).putExtra("notificationFlag", notificationFlag));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.foodListTab:
                        startActivity(new Intent(ProfileScreen.this,FoodSourceListScreen.class).putExtra("notificationFlag", notificationFlag));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.profileTab:
                        break;
                    default:
                        break;

                }
                return false;
            }
        });


        accountTabView = findViewById(R.id.accountTabView);
        profileTabView = findViewById(R.id.profileTabView);
        profileLogoutLayout = findViewById(R.id.profileLogoutLayout);
        accountTabView.setVisibility(View.GONE);
        profileTabView.setVisibility(View.VISIBLE);
        profileLogoutLayout.setVisibility(View.GONE);
        profileLogoutLayout.setOnClickListener(this);

        profileTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Account")){
                    accountTabView.setVisibility(View.VISIBLE);
                    profileTabView.setVisibility(View.GONE);
                    profileLogoutLayout.setVisibility(View.VISIBLE);

                }
                else if(tab.getText().equals("Profile")){
                    accountTabView.setVisibility(View.GONE);
                    profileTabView.setVisibility(View.VISIBLE);
                    profileLogoutLayout.setVisibility(View.GONE);
                }
                else{
                    System.out.println("nothing");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    private void ShowDialog(String profileType) {
        profileDobDialogTitleLin.setVisibility(View.GONE);
        profileExperienceDialogTitleLin.setVisibility(View.GONE);
        profileSubscriptionFeesDialogTitleLin.setVisibility(View.GONE);
        profileSubscriptionDescDialogTitleLin.setVisibility(View.GONE);
        profileMobileDialogTitleLin.setVisibility(View.GONE);

        if (profileType.equals("DateOfBirth")){
            profileDobDialogTitleLin.setVisibility(View.VISIBLE);
            profileDobDialogUpdate.setClickable(false);
            profileDobDialogTitle.setText("Date of birth");
            profileDob.getText().toString();
            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            int day = Integer.parseInt(profileDob.getText().toString().substring(0,2));
            int month = Integer.parseInt(profileDob.getText().toString().substring(3,5)) - 1;
            int year = Integer.parseInt(profileDob.getText().toString().substring(6,10));

            profileDobDialogDatePicker.setMaxDate(currentDate.getTime());
            profileDobDialogDatePicker.updateDate(year,month,day);
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                profileDobDialogDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int  month = monthOfYear + 1;
                        userProfileUpdateValue = dayOfMonth +"-" + month +"-"+ year;
                        profileDobDialogUpdate.setClickable(true);

                    }
                });
            }
            profileDialog.show();
        }

        else if (profileType.equals("Experience")){
            profileExperienceDialogTitleLin.setVisibility(View.VISIBLE);
            profileExperienceDialogTitle.setText("Experience");
            if (!profileExperience.getText().toString().isEmpty()){
                profileExperienceDialogInput.setText(profileExperience.getText().toString());
            }
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();

        }

        else if (profileType.equals("PhoneNumber")){
            profileMobileDialogTitleLin.setVisibility(View.VISIBLE);
            profileMobileDialogInput.setText(profilePhoneNumber.getText().toString());
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();
        }

        else if(profileType.equals("SubscriptionFees")){
            profileSubscriptionFeesDialogTitleLin.setVisibility(View.VISIBLE);
            profileSubscriptionFeesDialogInput.setText(profileSubscriptionFees.getText().toString());
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();
        }

        else if(profileType.equals("SubscriptionDescription")){
            profileSubscriptionDescDialogTitleLin.setVisibility(View.VISIBLE);
            profileSubscriptionDescDialogInput.setText(profileSubscriptionDescription.getText().toString());
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();
        }

        profileDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileDialog.dismiss();
                profileDobDialogTitleLin.setVisibility(View.GONE);

            }
        });
    }


    public void PopulateUserDetails(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        //Show Progress Dialog
        progressDialog.show();
        //Set Content
        progressDialog.setContentView(R.layout.progressdialog);
        //Set Transparent Background
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********OnDataChange*******");
                user = snapshot.getValue(User.class);

                String pattern = "dd-MM-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);


                profileUsername.setText(user.getName());
                profileType.setText(user.isTrainer()==true ? "Trainer": "Trainee");
                profileGender.setText(user.getGender());
                profileEmailId.setText(user.getEmail());
                profilePhoneNumber.setText(user.getPhoneNumber().toString());
                profileDob.setText(simpleDateFormat.format(user.getDateOfBirth()));
                Picasso.get().load(user.getImage())
                        .placeholder(R.drawable.ic_share)
                        .fit()
                        .centerCrop()
                        .into(profileImage);

                if (userType.equals("Trainer")){
                    Trainer trainer = snapshot.getValue(Trainer.class);


                    traineesNumber.setText(trainer.getUsersList() == null ? "--" : String.valueOf(trainer.getUsersList().size()));

                    if (trainer.getExperience() != null){

                        profileExperience.setText(String.valueOf(trainer.getExperience().toString()));
                    }
                    else{
                        profileExperience.setText("0.0");
                    }
                    if (trainer.getSubscriptionFees() != null){
                        profileSubscriptionFees.setText(trainer.getSubscriptionFees().toString());
                    }
                    else{
                        profileSubscriptionFees.setText("0.0");
                    }
                    if (trainer.getSubscriptionDescription() != null){
                        profileSubscriptionDescription.setText(trainer.getSubscriptionDescription().toString());
                    }
                    else{
                        profileSubscriptionDescription.setText("Not mentioned");
                    }

                }
                else{
                    Trainee trainee = snapshot.getValue(Trainee.class);
                    if(trainee.getTrainerId()!= null && trainee.getTrainerId()!="") {

                        if (trainee.getSubscriptionEndDate()!=null){
                            System.out.println("subscriptionExtendRelativeLay");
                            subscriptionExtendRelativeLay.setVisibility(View.VISIBLE);
                            Date endDate = trainee.getSubscriptionEndDate();
                            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
                            txtSubscriptionDate.setText(formatDate.format(endDate));
                        }

                        else{
                            subscriptionExtendRelativeLay.setVisibility(View.GONE);
                            //txtSubscriptionDate.setText("--");
                        }

                    }
                    //String trainerName = GetTrainerName(trainee.getTrainerId());
                    if (trainee.getTrainerId()!=null && !(trainee.getTrainerId().equals("")) ){
                        GetTrainerName(trainee.getTrainerId());
                    }
                    else{
                        profileSubscriptionTrainer.setText("No Trainer assigned");
                        profileSubscriptionTrainer.setTextColor(getResources().getColor(R.color.orange));
                        subscriptionExtendRelativeLay.setVisibility(View.GONE);
                    }

                }

                //Dismiss Progress Dialog
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetTrainerName(String trainerId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trainer/" + trainerId);
        //final String trainerName = "No trainer";

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trainer trainer = snapshot.getValue(Trainer.class);
                profileSubscriptionTrainer.setText(trainer.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer((GravityCompat.START));
        }
        else
        {
            Intent intent;
            switch(navigationScreen){

                case "TraineesScreen":
                    intent = new Intent(ProfileScreen.this,TraineesScreen.class);
                    startActivity(intent);
                    finish();
                    break;
                case "TrainerScreen":
                    intent = new Intent(ProfileScreen.this,TrainerScreen.class);
                    startActivity(intent);
                    finish();
                    break;
                case  "NotificationScreen":
                    intent = new Intent(ProfileScreen.this,NotificationScreen.class);
                    startActivity(intent);
                    finish();
                    break;
                case "FoodSourceListScreen":
                    intent = new Intent(ProfileScreen.this,FoodSourceListScreen.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    intent = new Intent(ProfileScreen.this,HomeScreen.class);
                    startActivity(intent);
                    finish();
                    break;
            }

        }
    }

    @Override
    public void onClick(View option) {

        switch (option.getId()) {
                case R.id.toolBarNotification:
                    option.startAnimation(buttonBounce);
                    System.out.println("toolbar");
                    startActivity(new Intent(ProfileScreen.this,NotificationScreen.class).putExtra("Screen","ProfileScreen"));
                    finish();
                    break;
                case R.id.dobRelativeLay:
                    ShowDialog("DateOfBirth");
                    break;
                case R.id.experienceRelativeLay:
                    ShowDialog("Experience");
                    break;
                case R.id.subscriptionFeesRelativeLay:
                    ShowDialog("SubscriptionFees");
                    break;
                case R.id.subscriptionDescriptionRelativeLay:
                    ShowDialog("SubscriptionDescription");
                    break;
                case R.id.profilePhoneRelativeLay:
                    ShowDialog("PhoneNumber");
                    break;
                case R.id.profileResetPassRelativeLay:
                    FirebaseAuth fAuth;
                    fAuth = FirebaseAuth.getInstance();

                    fAuth.sendPasswordResetEmail(user.getEmail().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfileScreen.this, "Password Link sent to your Email. Please reset your password and login again", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = settings.edit();
                                editor.remove("userId");
                                editor.remove("ProfileType");
                                editor.remove("IsLoggedIn");
                                editor.commit();
                                finish();

                            } else {
                                //Toast.makeText(ForgotPasswordForm.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(ProfileScreen.this, "Some error occurred. Please try after sometime", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                    break;

                case R.id.profileAdRelativeLay:
                    Intent intent = new Intent(ProfileScreen.this, AdListScreen.class);
                    System.out.println("getEmail"+user.getEmail());
                    intent.putExtra("userEmail",user.getEmail());
                    startActivity(intent);
                    finish();
                    break;
                case R.id.profileDobDialogUpdate:
                    option.startAnimation(buttonBounce);
                    updateProfile("dateOfBirth", userProfileUpdateValue);
                    break;
                case R.id.profileExperienceDialogUpdate:
                    option.startAnimation(buttonBounce);
                    updateProfile("experience", profileExperienceDialogInput.getText().toString());
                    break;
                case R.id.profileMobileDialogUpdate:
                    option.startAnimation(buttonBounce);
                    updateProfile("phoneNumber", profileMobileDialogInput.getText().toString());
                    break;
                case R.id.profileSubscriptionFeesDialogUpdate:
                    option.startAnimation(buttonBounce);
                    updateProfile("subscriptionFees", profileSubscriptionFeesDialogInput.getText().toString());
                    break;
                case R.id.profileSubscriptionDescDialogUpdate:
                    option.startAnimation(buttonBounce);
                    updateProfile("subscriptionDescription", profileSubscriptionDescDialogInput.getText().toString());
                    break;
                case R.id.profileLogoutLayout:
                    startActivity(new Intent(ProfileScreen.this,MainActivity.class));
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.remove("userId");
                    editor.remove("ProfileType");
                    editor.remove("IsLoggedIn");
                    //editor.putBoolean("IsLoggedIn",false);
                    editor.commit();
                    finish();
                    break;
                default:
                    break;

            }

    }

    private void FileChooser()
    {
        CropImage.startPickImageActivity(ProfileScreen.this);
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);*/

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK)
        {
            Uri uri = CropImage.getPickImageResultUri(this,data);
            imageUri = uri;
            if(CropImage.isReadExternalStoragePermissionsRequired(this,uri))
            {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                StartCrop(imageUri);

            }
            else {
                StartCrop(imageUri);
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                uploadFile(result.getUri());
            }
        }
    }

    private void StartCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private String getExtension(Uri uri) {
        ContentResolver CR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(CR.getType(imageUri));
    }

    private void uploadFile(Uri imageUri) {

        if(imageUri!= null)
        {

            final StorageReference fileReference = storageReference.child("FitnessGuide/Trainer").child(FirebaseAuth.getInstance().getUid()+".null");
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    HashMap<String,Object> imageMetaData = new HashMap<>();
                                    String image = task.getResult().toString();
                                    imageMetaData.put("image", image);
                                    databaseReference.updateChildren(imageMetaData);

                                    if (userType.equals("User")){
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Trainee trainee = snapshot.getValue(Trainee.class);

                                                if (trainee.getTrainerId()!=null && trainee.getTrainerId().length()>0){
                                                    DatabaseReference databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference("Trainer/"+trainee.getTrainerId()+"/usersList/"+trainee.getUserId());
                                                    databaseReferenceTrainer.updateChildren(imageMetaData);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                    Toast.makeText(ProfileScreen.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileScreen.this, "Something gone wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else{
            Toast.makeText(this, "File not selected", Toast.LENGTH_SHORT).show();
        }


    }

    private void updateProfile(String userField, String value){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        String pattern = "dd-MM-yyyy";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        HashMap hash= new HashMap();
        User userBmValue = user;
        HashMap<String,String> healthIssues = new HashMap<>();
        Boolean save = true;
        List<BmrProgress> bmrProgressList;
        if (user.getBmrReport()!=null){
            bmrProgressList = user.getBmrReport();
        }
        else {

            bmrProgressList = new ArrayList<BmrProgress>();
        }



        if (userField.equals("dateOfBirth")|| userField.equals("lastModDttm")){
            Date dateUpdate = null;
            Double userBmi = null;
            Double userBmr = null;

            try {
                dateUpdate = simpleDateFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            hash.put(userField,dateUpdate);
            databaseReference.updateChildren(hash);

            if ((new Double(user.getWeight())>0) && (new Double(user.getHeight())>0)){
                userBmr = userBmValue.bmrCalculation(user.getWeight(), user.getHeight());

                hash.put("bmr", new Double(round(userBmr)));
                hash.put("lastModDttm",Calendar.getInstance().getTime());


            }


        }

        else if (userField.equals("phoneNumber")){
            UserInputValidation userInputValidation = new UserInputValidation();
            String mobileNumberValid = userInputValidation.mobileNumberValidation(value);

            if (!mobileNumberValid.equals("Valid")) {
                save=false;
                profileMobileDialogInput.setError(mobileNumberValid);
            }
            else {
                hash.put(userField, Long.parseLong(value));
            }

        }

        else if ( userField.equals("experience") || userField.equals("subscriptionFees") || userField.equals("subscriptionDescription")){

            if (userField.equals("experience") || userField.equals("subscriptionFees")){
                if(value.length()>0){
                    Double userProfileValue = new Double(new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue());

                    if (userField.equals("experience")){

                        if (new Double(value).intValue()<=60){
                            hash.put(userField, userProfileValue);
                        }
                        else{
                            save=false;
                            profileExperienceDialogInput.setError("Please enter a valid Experience");
                        }

                    }
                    else{

                        if (userField.equals("subscriptionFees")){
                            if (new Double(value).intValue()>=1){
                                hash.put(userField, userProfileValue);
                            }
                            else{
                                save=false;
                                profileSubscriptionFeesDialogInput.setError("Please enter a valid Fees");
                            }
                        }


                    }

                }else{
                    switch (userField) {

                        case "experience":
                            profileExperienceDialogInput.setError("Please enter a valid Experience");
                            break;
                        case "subscriptionFees":
                            profileSubscriptionFeesDialogInput.setError("Please enter a valid Fees");
                            break;
                        default:
                            break;
                    }
                    save=false;
                }
            }
            else if (userField.equals("subscriptionDescription")){

                if(value.length()>0){
                    hash.put(userField, value);
                }
                else {
                    profileSubscriptionDescDialogInput.setError("Please enter a valid Description");
                    save=false;
                }

            }
            else{
                hash.put(userField, value);
            }

        }

        if(save) {
            System.out.println("Profile data updated successfully");
            databaseReference.updateChildren(hash);

            try {
                Thread.sleep(1000);

            }catch (Exception e){
                e.printStackTrace();
            }
            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();

            profileDialog.dismiss();
            PopulateUserDetails();

        }
    }
    public void traineeMetaDataUpdate(Double bmi){
            System.out.println("traineeMetaDataUpdate");
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Trainee trainee = snapshot.getValue(Trainee.class);

                    if (trainee.getTrainerId()!=null && trainee.getTrainerId().length()>0){
                        HashMap hash= new HashMap();
                        hash.put("bmi", bmi);
                        DatabaseReference databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference("Trainer/"+trainee.getTrainerId()+"/usersList/"+trainee.getUserId());
                        databaseReferenceTrainer.updateChildren(hash);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    public void checkNotification(String notificationPath){

        DatabaseReference databaseReferenceNotify = FirebaseDatabase.getInstance().getReference(notificationPath + "/Notification");
        databaseReferenceNotify.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()!=null){
                    System.out.println("present");
                    notificationFlag="present";
                    toolBarBadge.setVisibility(View.VISIBLE);
                }
                else {
                    System.out.println("empty");
                    notificationFlag="empty";
                    toolBarBadge.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }


}