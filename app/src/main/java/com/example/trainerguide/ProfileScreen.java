package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.biubiubiu.justifytext.library.JustifyTextView;

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
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    private ProgressDialog progressDialog;

    //Recycler view variables
    private RecyclerView profileRecyclerHealth, profileRecyclerFood;
    private List<String> healthItems = new ArrayList<>();
    private List<String> foodAllergy = new ArrayList<>();
    private ProfileAdapter profileAdapter, profileAdapterFood;
    private RelativeLayout profileOtherRelativeLayFood, profileOtherRelativeLayHealth;


    //Trainer Data
    private static Trainer trainer = new Trainer();


    //ProfileScreen Variables
    private ImageButton profileImage;
    MaterialCardView accCardView, personalInfoCardView, foodInfoCardView, healthInfoCardView, subscriptionInfoCardView;
    TextView profileAccDrop, profilePersonalInfoDrop, profileFoodInfoDrop, profileWeight, profileEmailId, profileDob, profileHeight, profileFoodType, profileHealthInfoDrop, foodAllergyOther, healthIssuesOther, profileExperience, profileSubscriptionInfoDrop, profileSubscriptionType, profileSubscriptionFees, profileSubscriptionDescription, profileSubscriptionTrainer;
    RelativeLayout accRelativeCollapse, personalRelativeCollapse, foodInfoRelativeCollapse, dobRelativeLay, healthInfoRelativeCollapse, weightRelativeLay, heightRelativeLay, foodTypeRelativeLay, foodAllergyRelativeLay, healthIssuesRelativeLay, experienceRelativeLay, subscriptionInfoRelativeCollapse, subscriptionTypeRelativeLay, subscriptionTrainerRelativeLay, subscriptionFeesRelativeLay, subscriptionDescriptionRelativeLay;

    private String userId;
    private String path;
    private String userProfileUpdateValue;
    private User user;
    private String userType;


    //Common variables
    private Intent intent;
    private Date currentDate = new Date();

    //PopUp Dialog
    Dialog profileDialog;
    ImageView profileDialogClose;
    TextView profileDobDialogTitle, profileWeightDialogTitle, profileHeightDialogTitle, profileExperienceDialogTitle, profileFoodTypeDialogTitle, profileFoodAllergyDialogTitle, profileHealthInfoDialogTitle, profileSubscriptionTypeDialogTitle, profileSubscriptionFeesDialogTitle, profileSubscriptionDescDialogTitle;
    LinearLayout profileDobDialogTitleLin, profileWeightDialogTitleLin, profileHeightDialogTitleLin, profileExperienceDialogTitleLin, profileFoodTypeDialogTitleLin, profileFoodAllergyDialogTitleLin, profileHealthInfoDialogTitleLin, profileSubscriptionTypeDialogTitleLin, profileSubscriptionFeesDialogTitleLin, profileSubscriptionDescDialogTitleLin;
    DatePicker profileDobDialogDatePicker;
    Button profileDobDialogUpdate, profileWeightDialogUpdate, profileHeightDialogUpdate, profileExperienceDialogUpdate, profileHealthInfoDialogUpdate, profileFoodAllergyDialogUpdate, profileFoodTypeDialogUpdate, profileSubscriptionTypeDialogUpdate, profileSubscriptionFeesDialogUpdate, profileSubscriptionDescDialogUpdate;
    EditText profileWeightDialogInput, profileHeightDialogInput, profileExperienceDialogInput, profileSubscriptionFeesDialogInput, profileSubscriptionDescDialogInput;
    MaterialCheckBox diabetesHealthIssue, cholesterolHealthIssue, thyroidHealthIssue, bpHealthIssue, heartHealthIssue, physicalInjuriesHealthIssue;
    MaterialCheckBox diaryFoodAllergy, wheatFoodAllergy, nutsFoodAllergy, seaFoodAllergy, muttonFoodAllergy, chickenFoodAllergy;
    RadioButton vegFoodType, vegEggFoodType, nonVegFoodType, weightLossSubscription, weightGainSubscription, weightMaintainSubscription;
    EditText otherHealthIssue, otherFoodAllergy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        //File Storage variables
        storageReference = FirebaseStorage.getInstance().getReference();


        //Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);

        //Navigation view variables
        drawerLayout = findViewById(R.id.profile_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.appleGreen));

        //ProfileScreen variables
        profileImage = findViewById(R.id.profileImage);

        //Account Info variables
        accCardView = findViewById(R.id.accCardView);
        profileAccDrop = findViewById(R.id.profileAccDrop);
        accRelativeCollapse = findViewById(R.id.accRelativeCollapse);
        //Personal Info variables
        personalInfoCardView = findViewById(R.id.personalInfoCardView);
        profilePersonalInfoDrop = findViewById(R.id.profilePersonalInfoDrop);
        personalRelativeCollapse = findViewById(R.id.personalRelativeCollapse);
        profileWeight = findViewById(R.id.profileWeight);
        profileHeight = findViewById(R.id.profileHeight);
        profileExperience = findViewById(R.id.profileExperience);
        profileEmailId = findViewById(R.id.profileEmailId);
        profileDob = findViewById(R.id.profileDob);
        profileSubscriptionType = findViewById(R.id.profileSubscriptionType);
        profileSubscriptionTrainer = findViewById(R.id.profileSubscriptionTrainer);
        profileSubscriptionFees = findViewById(R.id.profileSubscriptionFees);
        profileSubscriptionDescription = findViewById(R.id.profileSubscriptionDescription);
        dobRelativeLay = findViewById(R.id.dobRelativeLay);
        weightRelativeLay = findViewById(R.id.weightRelativeLay);
        heightRelativeLay = findViewById(R.id.heightRelativeLay);
        experienceRelativeLay = findViewById(R.id.experienceRelativeLay);

        //Food Info variables
        profileFoodType = findViewById(R.id.profileFoodType);
        foodInfoCardView = findViewById(R.id.foodInfoCardView);
        profileFoodInfoDrop = findViewById(R.id.profileFoodInfoDrop);
        foodInfoRelativeCollapse = findViewById(R.id.foodInfoRelativeCollapse);
        profileOtherRelativeLayFood = findViewById(R.id.profileOtherRelativeLayFood);
        foodAllergyOther = findViewById(R.id.foodAllergyOther);
        foodTypeRelativeLay = findViewById(R.id.foodTypeRelativeLay);
        foodAllergyRelativeLay = findViewById(R.id.foodAllergyRelativeLay);

        //Health Info variables
        healthInfoCardView = findViewById(R.id.healthInfoCardView);
        healthInfoRelativeCollapse = findViewById(R.id.healthInfoRelativeCollapse);
        profileHealthInfoDrop = findViewById(R.id.profileHealthInfoDrop);
        healthIssuesRelativeLay = findViewById(R.id.healthIssuesRelativeLay);
        profileOtherRelativeLayHealth = findViewById(R.id.profileOtherRelativeLayHealth);
        healthIssuesOther =  findViewById(R.id.healthIssuesOther);

        //Subscription Info variables
        subscriptionInfoCardView = findViewById(R.id.subscriptionInfoCardView);
        subscriptionInfoRelativeCollapse = findViewById(R.id.subscriptionInfoRelativeCollapse);
        profileSubscriptionInfoDrop = findViewById(R.id.profileSubscriptionInfoDrop);
        subscriptionTypeRelativeLay = findViewById(R.id.subscriptionTypeRelativeLay);
        subscriptionTrainerRelativeLay = findViewById(R.id.subscriptionTrainerRelativeLay);
        subscriptionFeesRelativeLay = findViewById(R.id.subscriptionFeesRelativeLay);
        subscriptionDescriptionRelativeLay = findViewById(R.id.subscriptionDescriptionRelativeLay);

        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
        traineeMenu = navigationView.findViewById(R.id.nav_trainees);
        //User Info variables
        //userId = getIntent().getStringExtra("UserId");
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userType = sp.getString("ProfileType",null);
        userId = sp.getString("userId",null);

        path = userType+ "/" + userId;

        if (userType.equals("Trainer")){
            experienceRelativeLay.setVisibility(View.VISIBLE);
            subscriptionDescriptionRelativeLay.setVisibility(View.VISIBLE);
            subscriptionFeesRelativeLay.setVisibility(View.VISIBLE);
            //navigationView.findViewById(R.id.nav_trainees).setVisibility(View.GONE);
        }
        else {
            subscriptionTrainerRelativeLay.setVisibility(View.VISIBLE);
            subscriptionTypeRelativeLay.setVisibility(View.VISIBLE);

        }
        //Get User Details
        PopulateUserDetails();

        //Health Recycler view variables
        profileRecyclerHealth = findViewById(R.id.profileRecyclerHealth);
        profileRecyclerHealth.setLayoutManager(new GridLayoutManager(this,2));
        profileAdapter = new ProfileAdapter(healthItems, ProfileScreen.this);
        profileRecyclerHealth.setAdapter(profileAdapter);

        //Food Recycler view variables
        profileRecyclerFood = findViewById(R.id.profileRecyclerFoodIssues);
        profileRecyclerFood.setLayoutManager(new GridLayoutManager(this,2));
        profileAdapterFood = new ProfileAdapter(foodAllergy, ProfileScreen.this);
        profileRecyclerFood.setAdapter(profileAdapterFood);

        //PopUp Dialog
        profileDialog = new Dialog(this);
        profileDialog.setContentView(R.layout.profile_screen_dialog);
        profileDialogClose = profileDialog.findViewById(R.id.profileDialogClose);

        profileDobDialogTitleLin = profileDialog.findViewById(R.id.profileDobDialogTitleLin);
        profileWeightDialogTitleLin = profileDialog.findViewById(R.id.profileWeightDialogTitleLin);
        profileHeightDialogTitleLin = profileDialog.findViewById(R.id.profileHeightDialogTitleLin);
        profileExperienceDialogTitleLin = profileDialog.findViewById(R.id.profileExperienceDialogTitleLin);
        profileFoodTypeDialogTitleLin = profileDialog.findViewById(R.id.profileFoodTypeDialogTitleLin);
        profileFoodAllergyDialogTitleLin = profileDialog.findViewById(R.id.profileFoodAllergyDialogTitleLin);
        profileHealthInfoDialogTitleLin = profileDialog.findViewById(R.id.profileHealthInfoDialogTitleLin);
        profileSubscriptionTypeDialogTitleLin = profileDialog.findViewById(R.id.profileSubscriptionTypeDialogTitleLin);
        profileSubscriptionFeesDialogTitleLin = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogTitleLin);
        profileSubscriptionDescDialogTitleLin = profileDialog.findViewById(R.id.profileSubscriptionDescDialogTitleLin);

        profileDobDialogTitle = profileDialog.findViewById(R.id.profileDobDialogTitle);
        profileDobDialogUpdate = profileDialog.findViewById(R.id.profileDobDialogUpdate);
        profileDobDialogDatePicker = profileDialog.findViewById(R.id.profileDobDialogDatePicker);

        profileWeightDialogTitle = profileDialog.findViewById(R.id.profileWeightDialogTitle);
        profileWeightDialogUpdate = profileDialog.findViewById(R.id.profileWeightDialogUpdate);
        profileWeightDialogInput = profileDialog.findViewById(R.id.profileWeightDialogInput);

        profileHeightDialogTitle = profileDialog.findViewById(R.id.profileHeightDialogTitle);
        profileHeightDialogUpdate = profileDialog.findViewById(R.id.profileHeightDialogUpdate);
        profileHeightDialogInput = profileDialog.findViewById(R.id.profileHeightDialogInput);

        profileExperienceDialogTitle = profileDialog.findViewById(R.id.profileExperienceDialogTitle);
        profileExperienceDialogUpdate = profileDialog.findViewById(R.id.profileExperienceDialogUpdate);
        profileExperienceDialogInput = profileDialog.findViewById(R.id.profileExperienceDialogInput);

        profileFoodTypeDialogTitle = profileDialog.findViewById(R.id.profileFoodTypeDialogTitle);
        vegFoodType = profileDialog.findViewById(R.id.vegFoodType);
        vegEggFoodType = profileDialog.findViewById(R.id.vegEggFoodType);
        nonVegFoodType = profileDialog.findViewById(R.id.nonVegFoodType);
        profileFoodTypeDialogUpdate = profileDialog.findViewById(R.id.profileFoodTypeDialogUpdate);

        profileFoodAllergyDialogTitle = profileDialog.findViewById(R.id.profileFoodAllergyDialogTitle);
        profileFoodAllergyDialogUpdate = profileDialog.findViewById(R.id.profileFoodAllergyDialogUpdate);
        diaryFoodAllergy = profileDialog.findViewById(R.id.diaryFoodAllergy);
        wheatFoodAllergy = profileDialog.findViewById(R.id.wheatFoodAllergy);
        nutsFoodAllergy = profileDialog.findViewById(R.id.nutsFoodAllergy);
        seaFoodAllergy = profileDialog.findViewById(R.id.seaFoodAllergy);
        muttonFoodAllergy = profileDialog.findViewById(R.id.muttonFoodAllergy);
        chickenFoodAllergy = profileDialog.findViewById(R.id.chickenFoodAllergy);
        otherFoodAllergy = profileDialog.findViewById(R.id.otherFoodAllergy);

        profileHealthInfoDialogTitle = profileDialog.findViewById(R.id.profileHealthInfoDialogTitle);
        profileHealthInfoDialogUpdate = profileDialog.findViewById(R.id.profileHealthInfoDialogUpdate);
        diabetesHealthIssue = profileDialog.findViewById(R.id.diabetesHealthIssue);
        cholesterolHealthIssue = profileDialog.findViewById(R.id.cholesterolHealthIssue);
        thyroidHealthIssue = profileDialog.findViewById(R.id.thyroidHealthIssue);
        bpHealthIssue = profileDialog.findViewById(R.id.bpHealthIssue);
        heartHealthIssue = profileDialog.findViewById(R.id.heartHealthIssue);
        physicalInjuriesHealthIssue = profileDialog.findViewById(R.id.physicalInjuriesHealthIssue);
        otherHealthIssue = profileDialog.findViewById(R.id.otherHealthIssue);

        profileSubscriptionTypeDialogTitle= profileDialog.findViewById(R.id.profileSubscriptionTypeDialogTitle);
        profileSubscriptionTypeDialogUpdate = profileDialog.findViewById(R.id.profileSubscriptionTypeDialogUpdate);
        weightLossSubscription = profileDialog.findViewById(R.id.weightLossSubscription);
        weightGainSubscription = profileDialog.findViewById(R.id.weightGainSubscription);
        weightMaintainSubscription = profileDialog.findViewById(R.id.weightMaintainSubscription);
        profileSubscriptionFeesDialogTitle = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogTitle);
        profileSubscriptionFeesDialogInput = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogInput);
        profileSubscriptionFeesDialogUpdate = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogUpdate);
        profileSubscriptionDescDialogTitle = profileDialog.findViewById(R.id.profileSubscriptionDescDialogTitle);
        profileSubscriptionDescDialogInput = profileDialog.findViewById(R.id.profileSubscriptionDescDialogInput);
        profileSubscriptionDescDialogUpdate = profileDialog.findViewById(R.id.profileSubscriptionDescDialogUpdate);

        //Dialog Pop - up Listener
        dobRelativeLay.setOnClickListener(this);
        weightRelativeLay.setOnClickListener(this);
        heightRelativeLay.setOnClickListener(this);
        experienceRelativeLay.setOnClickListener(this);
        foodTypeRelativeLay.setOnClickListener(this);
        foodAllergyRelativeLay.setOnClickListener(this);
        healthIssuesRelativeLay.setOnClickListener(this);
        subscriptionTypeRelativeLay.setOnClickListener(this);
        subscriptionTrainerRelativeLay.setOnClickListener(this);
        subscriptionFeesRelativeLay.setOnClickListener(this);
        subscriptionDescriptionRelativeLay.setOnClickListener(this);

        //Dialog update
        profileDobDialogUpdate.setOnClickListener(this);
        profileWeightDialogUpdate.setOnClickListener(this);
        profileHeightDialogUpdate.setOnClickListener(this);
        profileExperienceDialogUpdate.setOnClickListener(this);
        profileFoodTypeDialogUpdate.setOnClickListener(this);
        profileFoodAllergyDialogUpdate.setOnClickListener(this);
        profileHealthInfoDialogUpdate.setOnClickListener(this);
        profileSubscriptionTypeDialogUpdate.setOnClickListener(this);
        profileSubscriptionFeesDialogUpdate.setOnClickListener(this);
        profileSubscriptionDescDialogUpdate.setOnClickListener(this);

        //Update profile picture
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });


        accCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (accRelativeCollapse.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(accCardView,new AutoTransition());
                    accRelativeCollapse.setVisibility(View.VISIBLE);
                    profileAccDrop.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up);

                }else {
                    TransitionManager.beginDelayedTransition(accCardView,new AutoTransition());
                    accRelativeCollapse.setVisibility(View.GONE);
                    profileAccDrop.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
                }
            }
        });

        personalInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (personalRelativeCollapse.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(personalInfoCardView,new AutoTransition());
                    personalRelativeCollapse.setVisibility(View.VISIBLE);
                    profilePersonalInfoDrop.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up);

                }else {
                    TransitionManager.beginDelayedTransition(personalInfoCardView,new AutoTransition());
                    personalRelativeCollapse.setVisibility(View.GONE);
                    profilePersonalInfoDrop.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
                }
            }
        });

        foodInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (foodInfoRelativeCollapse.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(foodInfoCardView,new AutoTransition());
                    foodInfoRelativeCollapse.setVisibility(View.VISIBLE);
                    profileFoodInfoDrop.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up);

                }else {
                    TransitionManager.beginDelayedTransition(foodInfoCardView,new AutoTransition());
                    foodInfoRelativeCollapse.setVisibility(View.GONE);
                    profileFoodInfoDrop.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
                }
            }
        });

        healthInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (healthInfoRelativeCollapse.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(healthInfoCardView,new AutoTransition());
                    healthInfoRelativeCollapse.setVisibility(View.VISIBLE);
                    profileHealthInfoDrop.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up);

                }else {
                    TransitionManager.beginDelayedTransition(healthInfoCardView,new AutoTransition());
                    healthInfoRelativeCollapse.setVisibility(View.GONE);
                    profileHealthInfoDrop.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
                }
            }
        });

        subscriptionInfoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscriptionInfoRelativeCollapse.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(subscriptionInfoCardView,new AutoTransition());
                    subscriptionInfoRelativeCollapse.setVisibility(View.VISIBLE);
                    profileSubscriptionInfoDrop.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up);

                }else {
                    TransitionManager.beginDelayedTransition(subscriptionInfoCardView,new AutoTransition());
                    subscriptionInfoRelativeCollapse.setVisibility(View.GONE);
                    profileSubscriptionInfoDrop.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
                }
            }
        });


        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_profile:
                        break;

                    case R.id.nav_trainees:
                        if (userType.equals("Trainer")){
                            intent=new Intent(ProfileScreen.this,TraineesScreen.class);
                            System.out.println("***Pro***H**"+userId+"**S******");
                            intent.putExtra("UserId",userId);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case R.id.nav_notification:
                        startActivity(new Intent(ProfileScreen.this,NotificationScreen.class));
                        finish();
                        break;
                    case R.id.nav_logout:
                        intent=new Intent(ProfileScreen.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


    }


    private void ShowDialog(String profileType) {
        profileDobDialogTitleLin.setVisibility(View.GONE);
        profileWeightDialogTitleLin.setVisibility(View.GONE);
        profileHeightDialogTitleLin.setVisibility(View.GONE);
        profileExperienceDialogTitleLin.setVisibility(View.GONE);
        profileFoodTypeDialogTitleLin.setVisibility(View.GONE);
        profileFoodAllergyDialogTitleLin.setVisibility(View.GONE);
        profileHealthInfoDialogTitleLin.setVisibility(View.GONE);
        profileSubscriptionTypeDialogTitleLin.setVisibility(View.GONE);
        profileSubscriptionFeesDialogTitleLin.setVisibility(View.GONE);
        profileSubscriptionDescDialogTitleLin.setVisibility(View.GONE);

        if (profileType.equals("DateOfBirth")){
            profileDobDialogTitleLin.setVisibility(View.VISIBLE);
            profileDobDialogUpdate.setClickable(false);
            profileDobDialogTitle.setText("Date of birth");
            profileDobDialogDatePicker.setMaxDate(currentDate.getTime());
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                profileDobDialogDatePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                       int  month = monthOfYear + 1;
                       userProfileUpdateValue = dayOfMonth +"-" + month +"-"+ year;
                       System.out.println(dayOfMonth +"-" + month +"-"+ year);
                       profileDobDialogUpdate.setClickable(true);

                    }
                });
            }
            profileDialog.show();
        }

        else if (profileType.equals("Weight")){
            profileWeightDialogTitleLin.setVisibility(View.VISIBLE);
            profileWeightDialogTitle.setText("Weight");
            profileWeightDialogInput.setText(profileWeight.getText().toString());
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();
        }

        else if (profileType.equals("Height")){
            profileHeightDialogTitleLin.setVisibility(View.VISIBLE);
            profileHeightDialogTitle.setText("Height");
            profileHeightDialogInput.setText(profileHeight.getText().toString());
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        else if (profileType.equals("FoodType")){
            profileFoodTypeDialogTitleLin.setVisibility(View.VISIBLE);
            profileFoodTypeDialogTitle.setText("Food Type");
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();
        }

        else if(profileType.equals("FoodAllergy")){
            profileFoodAllergyDialogTitleLin.setVisibility(View.VISIBLE);
            profileFoodAllergyDialogTitle.setText("Food Allergy");
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();
        }

        else if(profileType.equals("HealthIssues")){
            profileHealthInfoDialogTitleLin.setVisibility(View.VISIBLE);
            profileHealthInfoDialogTitle.setText("Health Issues");
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();

        }
        else if(profileType.equals("SubscriptionType")){
            profileSubscriptionTypeDialogTitleLin.setVisibility(View.VISIBLE);
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
                profileWeightDialogTitleLin.setVisibility(View.GONE);
                profileHeightDialogTitleLin.setVisibility(View.GONE);
                profileFoodTypeDialogTitleLin.setVisibility(View.GONE);
                profileFoodAllergyDialogTitleLin.setVisibility(View.GONE);
                profileHealthInfoDialogTitleLin.setVisibility(View.GONE);
                profileSubscriptionTypeDialogTitleLin.setVisibility(View.GONE);
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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********OnDataChange*******");
                user = snapshot.getValue(User.class);

                String pattern = "dd-MM-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                profileWeight.setText(user.getWeight().toString());
                profileHeight.setText(user.getHeight().toString());
                profileEmailId.setText(user.getEmail());
                profileDob.setText(simpleDateFormat.format(user.getDateOfBirth()));
                Picasso.get().load(user.getImage())
                        .placeholder(R.drawable.ic_share)
                        .fit()
                        .centerCrop()
                        .into(profileImage);

                //Health Issue Recycler View Data
                if(user.getHealthIssues()!=null) {
                    healthItems.clear();

                    for (Map.Entry healthIssue : user.getHealthIssues().entrySet()) {

                        if (!"Others".equals(healthIssue.getKey())){
                            healthItems.add(healthIssue.getValue().toString());

                            if (healthIssue.getValue().toString().equals("Diabetes")){
                                diabetesHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Cholesterol")){
                                cholesterolHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Thyroid")){
                                thyroidHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Blood Pressure")){
                                bpHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Heart Problems")){
                                heartHealthIssue.setChecked(true);
                            }
                            if(healthIssue.getValue().toString().equals("Physical Injuries")){
                                physicalInjuriesHealthIssue.setChecked(true);
                            }

                        }
                        else{
                            profileOtherRelativeLayHealth.setVisibility(View.VISIBLE);
                            healthIssuesOther.setText(healthIssue.getValue().toString());
                            otherHealthIssue.setText(healthIssue.getValue().toString());
                        }


                    }
                    profileAdapter.notifyDataSetChanged();
                }

                //Food Allergy Recycler View Data
                if(user.getFoodAllergy()!=null) {
                    foodAllergy.clear();
                    for (Map.Entry  foodAllergyItem : user.getFoodAllergy().entrySet()) {

                        if (!"Others".equals(foodAllergyItem.getKey())){
                            foodAllergy.add(foodAllergyItem.getValue().toString());

                            if (foodAllergyItem.getValue().toString().equals("Diary")){
                                diaryFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Wheat")){
                                wheatFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Nuts")){
                                nutsFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Sea Food")){
                                seaFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Mutton")){
                                muttonFoodAllergy.setChecked(true);
                            }
                            if (foodAllergyItem.getValue().toString().equals("Chicken")){
                                chickenFoodAllergy.setChecked(true);
                            }

                        }
                        else{
                            profileOtherRelativeLayFood.setVisibility(View.VISIBLE);
                            foodAllergyOther.setText(foodAllergyItem.getValue().toString());
                            otherFoodAllergy.setText(foodAllergyItem.getValue().toString());

                        }
                    }
                }

             if (userType.equals("Trainer")){
                    Trainer trainer = snapshot.getValue(Trainer.class);
                    profileExperience.setText(trainer.getExperience().toString());
                    profileSubscriptionFees.setText(trainer.getSubscriptionFees());
                    profileSubscriptionDescription.setText(trainer.getSubscriptionDescription().toString());

                }
                else{
                    Trainee trainee = snapshot.getValue(Trainee.class);
                    profileFoodType.setText(trainee.getFoodType());
                    //String trainerName = GetTrainerName(trainee.getTrainerId());
                    GetTrainerName(trainee.getTrainerId());
                    profileSubscriptionType.setText(trainee.getSubscriptionType());

                    if (trainee.getFoodType()!=null){
                        switch (trainee.getFoodType()){
                            case "Vegetarian":
                                vegFoodType.setChecked(true);
                                break;
                            case "Eggetarian":
                                vegEggFoodType.setChecked(true);
                                break;
                            case "Non-Vegetarian":
                                nonVegFoodType.setChecked(true);
                                break;
                            default:
                                break;
                        }
                    }
                    if (trainee.getSubscriptionType()!=null){
                        switch (trainee.getSubscriptionType()){
                            case "Weight Loss":
                                weightLossSubscription.setChecked(true);
                                break;
                            case "Weight Gain":
                                weightGainSubscription.setChecked(true);
                                break;
                            case "Weight Maintain":
                                weightMaintainSubscription.setChecked(true);
                                break;
                            default:
                                break;
                        }
                    }
                }

                //Dismiss Progress Dialog
                progressDialog.dismiss();

                profileAdapterFood.notifyDataSetChanged();
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
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View option) {

        switch (option.getId()){

            case R.id.dobRelativeLay:
                ShowDialog("DateOfBirth");
                break;
            case R.id.weightRelativeLay:
                ShowDialog("Weight");
                break;
            case R.id.heightRelativeLay:
                ShowDialog("Height");
                break;
            case R.id.foodTypeRelativeLay:
                ShowDialog("FoodType");
                break;
            case R.id.foodAllergyRelativeLay:
                ShowDialog("FoodAllergy");
                break;
            case R.id.healthIssuesRelativeLay:
                ShowDialog("HealthIssues");
                break;
            case R.id.experienceRelativeLay:
                ShowDialog("Experience");
                break;
            case R.id.subscriptionTypeRelativeLay:
                ShowDialog("SubscriptionType");
                break;
            case R.id.subscriptionFeesRelativeLay:
                ShowDialog("SubscriptionFees");
                break;
            case R.id.subscriptionDescriptionRelativeLay:
                ShowDialog("SubscriptionDescription");
                break;
            case R.id.profileDobDialogUpdate:
                updateProfile("dateOfBirth", userProfileUpdateValue);
                break;
            case R.id.profileWeightDialogUpdate:
                updateProfile("weight", profileWeightDialogInput.getText().toString());
                break;
            case R.id.profileHeightDialogUpdate:
                updateProfile("height",profileHeightDialogInput.getText().toString());
                break;
            case R.id.profileExperienceDialogUpdate:
                updateProfile("experience",profileExperienceDialogInput.getText().toString());
                break;
            case R.id.profileFoodTypeDialogUpdate:
                String foodTypeValue = "Not mentioned";
                if (vegFoodType.isChecked()){
                    foodTypeValue="Vegetarian";
                }
                else if (vegEggFoodType.isChecked()){
                    foodTypeValue="Eggetarian";
                }
                else if (nonVegFoodType.isChecked()){
                    foodTypeValue="Non-Vegetarian";
                }
                updateProfile("foodType",foodTypeValue);
                break;
            case R.id.profileFoodAllergyDialogUpdate:
                updateProfile("foodAllergy",null);
                break;
            case R.id.profileHealthInfoDialogUpdate:
                updateProfile("healthIssues",null);
                break;
            case R.id.profileSubscriptionTypeDialogUpdate:
                String subscriptionValue = "Not mentioned";
                if (weightLossSubscription.isChecked()){
                    subscriptionValue="Weight Loss";
                }
                else if (weightGainSubscription.isChecked()){
                    subscriptionValue="Weight Gain";
                }
                else if (weightMaintainSubscription.isChecked()){
                    subscriptionValue="Weight Maintain";
                }
                updateProfile("subscriptionType",subscriptionValue);
                break;

            case R.id.profileSubscriptionFeesDialogUpdate:
                updateProfile("subscriptionFees",profileSubscriptionFeesDialogInput.getText().toString());

            case  R.id.profileSubscriptionDescDialogUpdate:
                updateProfile("subscriptionDescription", profileSubscriptionDescDialogInput.getText().toString());
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
                                    imageMetaData.put("image",image);
                                    databaseReference.updateChildren(imageMetaData);
                                    Toast.makeText(ProfileScreen.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        User userBmValue = new User();
        HashMap<String,String> healthIssues = new HashMap<>();


        if (userField.equals("dateOfBirth")|| userField.equals("lastModDttm")){
            Date dateUpdate = null;

            try {
                dateUpdate = simpleDateFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            hash.put(userField,dateUpdate);
        }

        else if (userField.equals("weight") || userField.equals("height")){
            Double userProfileValue = new Double(value);
            hash.put(userField,userProfileValue);
            Double userBmi = null;
            Double userBmr = null;

            switch (userField){

                case "weight":
                    userBmi = userBmValue.bmiCalculation(userProfileValue, user.getHeight());
                    userBmr = userBmValue.bmrCalculation(userProfileValue, user.getHeight(),user.getGender(),25);
                    break;
                case "height":
                    userBmi = userBmValue.bmiCalculation(user.getWeight(), userProfileValue);
                    userBmr = userBmValue.bmrCalculation(user.getWeight(), userProfileValue,user.getGender(),25);
                    break;
                default:
                    break;
            }

            hash.put("bmi",round(userBmi));
            hash.put("bmr",round(userBmr));

        }

        else if (userField.equals("foodType") || userField.equals("subscriptionType")  || userField.equals("experience") || userField.equals("subscriptionFees") || userField.equals("subscriptionDescription")){

            if (userField.equals("experience") || userField.equals("subscriptionFees")){
                hash.put(userField, new Double(value));
            }
            hash.put(userField, value);
        }

        else if (userField.equals("foodAllergy")){

            HashMap<String,String> foodAllergy = new HashMap<>();

            if (diaryFoodAllergy.isChecked()){
                foodAllergy.put("Diary","Diary");
            }
            if(wheatFoodAllergy.isChecked()){
                foodAllergy.put("Wheat","Wheat");
            }
            if(nutsFoodAllergy.isChecked()){
                foodAllergy.put("Nuts","Nuts");
            }
            if(seaFoodAllergy.isChecked()){
                foodAllergy.put("Sea Food","Sea Food");
            }
            if(muttonFoodAllergy.isChecked()){
                foodAllergy.put("Mutton","Mutton");
            }
            if (chickenFoodAllergy.isChecked()){
                foodAllergy.put("Chicken","Chicken");
            }
            if (otherFoodAllergy.getText().toString().length()>0){
                foodAllergy.put("Others",otherFoodAllergy.getText().toString());
            }
            hash.put(userField, foodAllergy);
        }

        else if (userField.equals("healthIssues")){

            HashMap<String,String> healthIssue = new HashMap<>();

            if (diabetesHealthIssue.isChecked()){
                healthIssue.put("Diabetes","Diabetes");
            }
            if(cholesterolHealthIssue.isChecked()){
                healthIssue.put("Cholesterol","Cholesterol");
            }
            if (thyroidHealthIssue.isChecked()){
                healthIssue.put("Thyroid","Thyroid");
            }
            if(bpHealthIssue.isChecked()){
                healthIssue.put("Blood Pressure","Blood Pressure");
            }
            if(heartHealthIssue.isChecked()){
                healthIssue.put("Heart Problems","Heart Problems");
            }
            if(physicalInjuriesHealthIssue.isChecked()){
                healthIssue.put("Physical Injuries","Physical Injuries");
            }
            if (otherHealthIssue.getText().toString().length()>0){
                healthIssue.put("Others",otherHealthIssue.getText().toString());
            }

            hash.put(userField, healthIssue);
        }

        databaseReference.updateChildren(hash);
        profileDialog.dismiss();
    }



}