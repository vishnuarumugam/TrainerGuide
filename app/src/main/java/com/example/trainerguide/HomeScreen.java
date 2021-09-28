package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Ad;
import com.example.trainerguide.models.BmrProgress;
import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.IndicatorView.animation.type.WormAnimation;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.round;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener, AdSliderAdapter.OnClickAdListener {

    //Homescreen variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button toolBarNotification;
    private TextView sideUserName;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    Intent intent;
    Animation buttonBounce;
    private User user;
    private Trainee trainee;

    //PopUp Dialog
    private Dialog profileDialog;
    private ImageView profileDialogClose;
    private LinearLayout profileDobDialogTitleLin, profileWeightDialogTitleLin, profileHeightDialogTitleLin, profileExperienceDialogTitleLin, profileFoodTypeDialogTitleLin, profileFoodAllergyDialogTitleLin, profileHealthInfoDialogTitleLin, profileSubscriptionFeesDialogTitleLin, profileSubscriptionDescDialogTitleLin;
    private TextView profileWeightDialogTitle, profileHeightDialogTitle, profileFoodTypeDialogTitle, profileFoodAllergyDialogTitle, profileHealthInfoDialogTitle;
    private EditText profileWeightDialogInput, profileHeightDialogInput;
    private Button profileWeightDialogUpdate, profileHeightDialogUpdate, profileHealthInfoDialogUpdate, profileFoodAllergyDialogUpdate, profileFoodTypeDialogUpdate;
    private MaterialCheckBox diabetesHealthIssue, cholesterolHealthIssue, thyroidHealthIssue, bpHealthIssue, heartHealthIssue, physicalInjuriesHealthIssue;
    private MaterialCheckBox diaryFoodAllergy, wheatFoodAllergy, nutsFoodAllergy, seaFoodAllergy, muttonFoodAllergy, chickenFoodAllergy;
    private RadioButton vegFoodType, vegEggFoodType, nonVegFoodType;
    private EditText otherHealthIssue, otherFoodAllergy;

    //Dashboard variables
    private RelativeLayout progressLayout, weightLayout, heightLayout, dietTypeLayout, allergicLayout, healthIssueLayout, postAdLayout;
    private TextView dashboard_user_name;
    private BottomNavigationView homeScreenTabLayout;
    private CardView homeScreenGoalLayout, findTrainerLayout, home_screen_ad_layout;
    private RadioButton weightLossSubscription, weightGainSubscription, weightMaintainSubscription;;

    //Ad Slider
    List<Ad> adList = new ArrayList<>();
    SliderView topAdSliderView;
    //int[] adImages = {R.mipmap.ad_image,R.mipmap.create_account_image, R.mipmap.create_account_image};
    int[] adImages;
    private AdSliderAdapter topAdSliderAdapter;

    //User Detail variables
    private String userId, path, userType, isAdmin;
    private SharedPreferences sp;

    //Progress Bar
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        //Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolbar = findViewById(R.id.tool_bar);
        //sideUserName = navigationView.findViewById(R.id.sideUserName);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        toolbar.setTitleTextColor(getResources().getColor(R.color.themeColourThree));
        /*toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourTwo));
        toolbar.setTitleTextColor(getResources().getColor(R.color.themeColourOne));*/

        //toolBarNotification.setBackgroundColor(getResources().getColor(R.color.white));

        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);

        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));
        //toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourThree));


        //Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);

        //Menu Items
        profileMenu = findViewById(R.id.nav_profile);
        //traineeMenu = findViewById(R.id.nav_trainees);

        //Dashboard variables
        homeScreenTabLayout = findViewById(R.id.homeScreenTabLayout);
        dashboard_user_name = findViewById(R.id.dashboard_user_name);
        homeScreenGoalLayout = findViewById(R.id.home_screen_goal_layout);
        findTrainerLayout = findViewById(R.id.findTrainerLayout);
        weightLossSubscription = findViewById(R.id.weightLossSubscription);
        weightGainSubscription = findViewById(R.id.weightGainSubscription);
        weightMaintainSubscription = findViewById(R.id.weightMaintainSubscription);

        weightLossSubscription.setOnClickListener(this);
        weightGainSubscription.setOnClickListener(this);
        weightMaintainSubscription.setOnClickListener(this);

        progressLayout = findViewById(R.id.progressLayout);
        weightLayout = findViewById(R.id.weightLayout);
        heightLayout = findViewById(R.id.heightLayout);
        dietTypeLayout = findViewById(R.id.dietTypeLayout);
        allergicLayout = findViewById(R.id.allergicLayout);
        healthIssueLayout = findViewById(R.id.healthIssueLayout);
        postAdLayout = findViewById(R.id.postAdLayout);


        weightLayout.setOnClickListener(this);
        heightLayout.setOnClickListener(this);
        progressLayout.setOnClickListener(this);
        dietTypeLayout.setOnClickListener(this);
        allergicLayout.setOnClickListener(this);
        healthIssueLayout.setOnClickListener(this);
        findTrainerLayout.setOnClickListener(this);
        postAdLayout.setOnClickListener(this);

        home_screen_ad_layout = findViewById(R.id.home_screen_ad_layout);
        home_screen_ad_layout.setVisibility(View.GONE);



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
        profileSubscriptionFeesDialogTitleLin = profileDialog.findViewById(R.id.profileSubscriptionFeesDialogTitleLin);
        profileSubscriptionDescDialogTitleLin = profileDialog.findViewById(R.id.profileSubscriptionDescDialogTitleLin);

        //Pop-up dialog
        profileWeightDialogTitle = profileDialog.findViewById(R.id.profileWeightDialogTitle);
        profileWeightDialogUpdate = profileDialog.findViewById(R.id.profileWeightDialogUpdate);
        profileWeightDialogInput = profileDialog.findViewById(R.id.profileWeightDialogInput);

        profileHeightDialogTitle = profileDialog.findViewById(R.id.profileHeightDialogTitle);
        profileHeightDialogUpdate = profileDialog.findViewById(R.id.profileHeightDialogUpdate);
        profileHeightDialogInput = profileDialog.findViewById(R.id.profileHeightDialogInput);

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

        profileWeightDialogUpdate.setOnClickListener(this);
        profileHeightDialogUpdate.setOnClickListener(this);
        profileFoodTypeDialogUpdate.setOnClickListener(this);
        profileFoodAllergyDialogUpdate.setOnClickListener(this);
        profileHealthInfoDialogUpdate.setOnClickListener(this);

        //Ad Slider Variables
        topAdSliderView = findViewById(R.id.homeScreenTopAdSlider);

        toolBarNotification.setOnClickListener(this);



        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userType = sp.getString("ProfileType",null);

        dashboard_user_name.setText(sp.getString("UserName",null) + "!");

        isAdmin = sp.getString("isAdmin",null);
        if (userType.equals("Trainer")){
            homeScreenGoalLayout.setVisibility(View.GONE);
            findTrainerLayout.setVisibility(View.GONE);
           /* traineeDashboard.setVisibility(View.VISIBLE);
            foodDashboard.setVisibility(View.VISIBLE);
            //pdf_dashboard.setVisibility(View.GONE);*/
        }
        else{

            switch (sp.getString("UserGoal",null)){
                case "Weight Loss":
                    weightLossSubscription.setChecked(true);
                    break;
                case "Weight Gain":
                    weightGainSubscription.setChecked(true);
                    break;
                case "Stay Fit":
                    weightMaintainSubscription.setChecked(true);
                    break;
                default:
                    break;
            }

            if (isAdmin.equals("0") || isAdmin==null){
                homeScreenTabLayout.getMenu().removeItem(R.id.foodListTab);
            }
            else if(isAdmin.equals("1")){
                home_screen_ad_layout.setVisibility(View.VISIBLE);
            }
            homeScreenTabLayout.getMenu().removeItem(R.id.traineesTab);

            /*traineeDashboard.setVisibility(View.GONE);
            foodDashboard.setVisibility(View.GONE);
            //pdf_dashboard.setVisibility(View.GONE);*/
        }

        userId = sp.getString("userId",null);
        path = userType+ "/" + userId;



        homeScreenTabLayout.setSelectedItemId(R.id.homeTab);
        homeScreenTabLayout.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.homeTab:
                        break;
                    case R.id.trainersTab:
                        startActivity(new Intent(HomeScreen.this,TrainerScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.traineesTab:
                        startActivity(new Intent(HomeScreen.this,TraineesScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.foodListTab:startActivity(new Intent(HomeScreen.this,FoodSourceListScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    case R.id.profileTab:
                        startActivity(new Intent(HomeScreen.this,ProfileScreen.class));
                        overridePendingTransition(0,0);
                        finish();
                        break;
                    default:
                        break;

                }
                return false;
            }
        });

        populateAds();
        topAdSliderAdapter = new AdSliderAdapter(adList);
        topAdSliderAdapter.setOnClickAdListener(HomeScreen.this);

        PopulateUserDetails();


        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        intent=new Intent(HomeScreen.this,ProfileScreen.class);
                        intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;
                    /*case R.id.nav_trainees:
                        intent=new Intent(HomeScreen.this,TraineesScreen.class);
                        intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;*/
                    case R.id.nav_notification:
                        startActivity(new Intent(HomeScreen.this,NotificationScreen.class));
                        finish();
                        break;

                    case R.id.nav_logout:
                        startActivity(new Intent(HomeScreen.this,MainActivity.class));
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
                        Toast.makeText(HomeScreen.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });


    }


    public void onClick(View option) {

        SharedPreferences.Editor editor = sp.edit();
        switch (option.getId()) {

            case R.id.weightLossSubscription:
                editor.putString("UserGoal", "Weight Loss");
                trainee.setSubscriptionType("Weight Loss");
                updateProfile("subscriptionType", "Weight Loss");
                break;
            case R.id.weightGainSubscription:
                editor.putString("UserGoal", "Weight Gain");
                trainee.setSubscriptionType("Weight Gain");
                updateProfile("subscriptionType", "Weight Gain");
                break;
            case R.id.weightMaintainSubscription:
                editor.putString("UserGoal", "Stay Fit");
                trainee.setSubscriptionType("Stay Fit");
                updateProfile("subscriptionType", "Stay Fit");
                break;
            case R.id.weightLayout:
                ShowDialog("Weight");
                break;
            case R.id.heightLayout:
                ShowDialog("Height");
                break;
            case R.id.progressLayout:
                startActivity(new Intent(HomeScreen.this,UserReport.class));
                finish();
                break;
            case R.id.dietTypeLayout:
                ShowDialog("FoodType");
                break;
            case R.id.allergicLayout:
                ShowDialog("FoodAllergy");
                break;
            case R.id.healthIssueLayout:
                ShowDialog("HealthIssues");
                break;
            case R.id.profileWeightDialogUpdate:
                option.startAnimation(buttonBounce);
                updateProfile("weight", profileWeightDialogInput.getText().toString());
                break;
            case R.id.profileHeightDialogUpdate:
                option.startAnimation(buttonBounce);
                updateProfile("height", profileHeightDialogInput.getText().toString());
                break;
            case R.id.profileFoodTypeDialogUpdate:
                option.startAnimation(buttonBounce);
                String foodTypeValue = "Not mentioned";
                if (vegFoodType.isChecked()) {
                    foodTypeValue = "Vegetarian";
                } else if (vegEggFoodType.isChecked()) {
                    foodTypeValue = "Eggetarian";
                } else if (nonVegFoodType.isChecked()) {
                    foodTypeValue = "Non-Vegetarian";
                }
                updateProfile("foodType", foodTypeValue);
                break;
            case R.id.profileFoodAllergyDialogUpdate:
                option.startAnimation(buttonBounce);
                updateProfile("foodAllergy", null);
                break;
            case R.id.profileHealthInfoDialogUpdate:
                option.startAnimation(buttonBounce);
                updateProfile("healthIssues", null);
                break;

            case R.id.findTrainerLayout:
                startActivity(new Intent(HomeScreen.this, FindTrainer.class));
                finish();
                break;
            case R.id.toolBarNotification:
                option.startAnimation(buttonBounce);
                startActivity(new Intent(HomeScreen.this,NotificationScreen.class));
                finish();
                break;
            case R.id.postAdLayout:
                startActivity(new Intent(HomeScreen.this,AdPostingScreen.class));
                finish();
                break;
            default:
                break;
        }
    }

    public void PopulateUserDetails(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        //Show Progress Dialog
        //progressDialog.show();
        //Set Content
        progressDialog.setContentView(R.layout.progressdialog);
        //Set Transparent Background
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********OnDataChange*******");
                user = snapshot.getValue(User.class);

                //dashboard_user_name.setText(user.getName().toString() + " !");

                if (user.getFoodType()!=null){
                    switch (user.getFoodType()){
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

                String pattern = "dd-MM-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                //Health Issue Recycler View Data
                if(user.getHealthIssues()!=null) {

                    for (Map.Entry healthIssue : user.getHealthIssues().entrySet()) {

                        if (!"Others".equals(healthIssue.getKey())){

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
                            otherHealthIssue.setText(healthIssue.getValue().toString());
                        }


                    }
                }

                //Food Allergy Recycler View Data
                if(user.getFoodAllergy()!=null) {
                    for (Map.Entry  foodAllergyItem : user.getFoodAllergy().entrySet()) {

                        if (!"Others".equals(foodAllergyItem.getKey())){

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

                            otherFoodAllergy.setText(foodAllergyItem.getValue().toString());

                        }
                    }
                }


                if (userType.equals("Trainer")){
                    Trainer trainer = snapshot.getValue(Trainer.class);

                }
                else{
                    trainee = snapshot.getValue(Trainee.class);
                    System.out.println("Trainee");

                    if (trainee.getSubscriptionType()!=null){
                        System.out.println("Trainee1");

                        switch (trainee.getSubscriptionType()){
                            case "Weight Loss":
                                weightLossSubscription.setChecked(true);
                                break;
                            case "Weight Gain":
                                weightGainSubscription.setChecked(true);
                                break;
                            case "Stay Fit":
                                weightMaintainSubscription.setChecked(true);
                                break;
                            default:
                                break;
                        }
                    }
                }


                //Dismiss Progress Dialog
                progressDialog.dismiss();

                //profileAdapterFood.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
            /*Date dateUpdate = null;
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
                hash.put("lastModDttm", Calendar.getInstance().getTime());


            }

*/
        }

        else if (userField.equals("weight") || userField.equals("height")){
            if( !(value.isEmpty()) && (new Double(value)>0)) {
                Double userProfileValue = new Double(new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue());
                Double userBmi = null;
                Double userBmr = null;


                if (userField.equals("weight")){
                    if (userProfileValue.intValue()>=30 && userProfileValue.intValue()<=300){
                        user.setWeight(userProfileValue);
                        bmrProgressList.add(new BmrProgress(Calendar.getInstance().getTime(),userProfileValue));
                        hash.put("bmrReport",bmrProgressList);
                        hash.put(userField, userProfileValue);

                    }
                    else {
                        save=false;
                        profileWeightDialogInput.setError("Please enter a valid Weight");
                    }

                }
                else{

                    if (userProfileValue.intValue()>=90 && userProfileValue.intValue()<=230){
                        user.setHeight(userProfileValue);
                        hash.put(userField, userProfileValue);
                    }
                    else {
                        save=false;
                        profileHeightDialogInput.setError("Please enter a valid Height");
                    }
                }

                if( (new Double(user.getWeight())>0) && (new Double(user.getHeight())>0) ){
                    switch (userField) {

                        case "weight":

                            userBmi = userBmValue.bmiCalculation(userProfileValue, user.getHeight());
                            userBmr = userBmValue.bmrCalculation(userProfileValue, user.getHeight());
                            break;
                        case "height":
                            userBmi = userBmValue.bmiCalculation(user.getWeight(), userProfileValue);
                            userBmr = userBmValue.bmrCalculation(user.getWeight(), userProfileValue);

                            break;
                        default:
                            break;
                    }

                    hash.put("bmi", new Double(round(userBmi)));
                    hash.put("bmr", new Double(round(userBmr)));
                    hash.put("lastModDttm",Calendar.getInstance().getTime());
                    if (userType.equals("User")){
                        System.out.println("traineeMetaDataUpdatewief");
                        traineeMetaDataUpdate(new Double(round(userBmi)));
                    }
                }

            }
            else{
                switch (userField) {

                    case "weight":
                        profileWeightDialogInput.setError("Please enter a valid Weight");
                        break;
                    case "height":
                        profileHeightDialogInput.setError("Please enter a valid Height");
                        break;
                    default:
                        break;
                }
                save=false;
            }

        }

        else if (userField.equals("foodType")){
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
            //PopulateUserDetails();

        }
    }

    private void ShowDialog(String profileType) {
        profileDobDialogTitleLin.setVisibility(View.GONE);
        profileWeightDialogTitleLin.setVisibility(View.GONE);
        profileHeightDialogTitleLin.setVisibility(View.GONE);
        profileExperienceDialogTitleLin.setVisibility(View.GONE);
        profileFoodTypeDialogTitleLin.setVisibility(View.GONE);
        profileFoodAllergyDialogTitleLin.setVisibility(View.GONE);
        profileHealthInfoDialogTitleLin.setVisibility(View.GONE);
        profileSubscriptionFeesDialogTitleLin.setVisibility(View.GONE);
        profileSubscriptionDescDialogTitleLin.setVisibility(View.GONE);

        if (profileType.equals("Weight")){
            profileWeightDialogTitleLin.setVisibility(View.VISIBLE);
            profileWeightDialogTitle.setText("Weight");
            profileWeightDialogInput.setText(user.getWeight().toString());
            profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            profileDialog.show();
        }

        else if (profileType.equals("Height")){
            profileHeightDialogTitleLin.setVisibility(View.VISIBLE);
            profileHeightDialogTitle.setText("Height");
            profileHeightDialogInput.setText(user.getHeight().toString());
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

    private void populateAds() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ad");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Ad ad = dataSnapshot.getValue(Ad.class);
                    adList.add(ad);
                }
                topAdSliderView.setSliderAdapter(topAdSliderAdapter);
                topAdSliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
                topAdSliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                topAdSliderView.startAutoCycle();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }


    @Override
    public void onClickAd(int position) {
        Ad ad = adList.get(position);

        if (ad.getRedirectTo().equals("Web Page")){
            String urlLink = ad.getUrl();
            Uri uri = Uri.parse(urlLink);
            startActivity(new Intent(Intent.ACTION_VIEW,uri));
        }
        else if (ad.getRedirectTo().equals("Profile")){
            Intent intent = new Intent(HomeScreen.this, AdViewScreen.class);
            intent.putExtra("ad",ad);
            intent.putExtra("userEmail",user.getEmail());
            startActivity(intent);
        }
        else {

        }

    }
}