package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
    private RelativeLayout profileOtherRelativeLayFood;


    //Trainer Data
    private static Trainer trainer = new Trainer();


    //ProfileScreen Variables
    private ImageButton profileImage;
    MaterialCardView accCardView, personalInfoCardView, foodInfoCardView, healthInfoCardView;
    TextView profileAccDrop, profilePersonalInfoDrop, profileFoodInfoDrop, profileWeight, profileEmailId, profileDob, profileHeight, profileHealthInfoDrop, foodAllergyOther;
    RelativeLayout accRelativeCollapse, personalRelativeCollapse, foodInfoRelativeCollapse, dobRelativeLay, healthInfoRelativeCollapse, weightRelativeLay, heightRelativeLay, foodTypeRelativeLay, foodAllergyRelativeLay, healthIssuesRelativeLay;

    private String userId;
    private String path;
    private String userProfileUpdateValue;
    private User user;


    //Common variables
    private Intent intent;
    private Date currentDate = new Date();

    //PopUp Dialog
    Dialog profileDialog;
    ImageView profileDialogClose;
    TextView profileDobDialogTitle, profileWeightDialogTitle, profileHeightDialogTitle, profileFoodTypeDialogTitle, profileFoodAllergyDialogTitle, profileHealthInfoDialogTitle;
    LinearLayout profileDobDialogTitleLin, profileWeightDialogTitleLin, profileHeightDialogTitleLin, profileFoodTypeDialogTitleLin, profileFoodAllergyDialogTitleLin, profileHealthInfoDialogTitleLin;
    DatePicker profileDobDialogDatePicker;
    Button profileDobDialogUpdate, profileWeightDialogUpdate, profileHeightDialogUpdate;
    EditText profileWeightDialogInput, profileHeightDialogInput;


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
        profileEmailId = findViewById(R.id.profileEmailId);
        profileDob = findViewById(R.id.profileDob);
        dobRelativeLay = findViewById(R.id.dobRelativeLay);
        weightRelativeLay = findViewById(R.id.weightRelativeLay);
        heightRelativeLay = findViewById(R.id.heightRelativeLay);


        //Food Info variables
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

        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
        traineeMenu = findViewById(R.id.nav_trainees);
        //User Info variables
        userId = getIntent().getStringExtra("UserId");
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        final String userType = sp.getString("ProfileType",null);
        path = userType+ "/" + userId;


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
        profileFoodTypeDialogTitleLin = profileDialog.findViewById(R.id.profileFoodTypeDialogTitleLin);
        profileFoodAllergyDialogTitleLin = profileDialog.findViewById(R.id.profileFoodAllergyDialogTitleLin);
        profileHealthInfoDialogTitleLin = profileDialog.findViewById(R.id.profileHealthInfoDialogTitleLin);

        profileDobDialogTitle = profileDialog.findViewById(R.id.profileDobDialogTitle);
        profileDobDialogUpdate = profileDialog.findViewById(R.id.profileDobDialogUpdate);
        profileDobDialogDatePicker = profileDialog.findViewById(R.id.profileDobDialogDatePicker);

        profileWeightDialogTitle = profileDialog.findViewById(R.id.profileWeightDialogTitle);
        profileWeightDialogUpdate = profileDialog.findViewById(R.id.profileWeightDialogUpdate);
        profileWeightDialogInput = profileDialog.findViewById(R.id.profileWeightDialogInput);

        profileHeightDialogTitle = profileDialog.findViewById(R.id.profileHeightDialogTitle);
        profileHeightDialogUpdate = profileDialog.findViewById(R.id.profileHeightDialogUpdate);
        profileHeightDialogInput = profileDialog.findViewById(R.id.profileHeightDialogInput);

        profileFoodTypeDialogTitle = profileDialog.findViewById(R.id.profileFoodTypeDialogTitle);
        profileFoodAllergyDialogTitle = profileDialog.findViewById(R.id.profileFoodAllergyDialogTitle);
        profileHealthInfoDialogTitle = profileDialog.findViewById(R.id.profileHealthInfoDialogTitle);



        dobRelativeLay.setOnClickListener(this);
        weightRelativeLay.setOnClickListener(this);
        heightRelativeLay.setOnClickListener(this);
        foodTypeRelativeLay.setOnClickListener(this);
        foodAllergyRelativeLay.setOnClickListener(this);
        healthIssuesRelativeLay.setOnClickListener(this);
        profileDobDialogUpdate.setOnClickListener(this);
        profileWeightDialogUpdate.setOnClickListener(this);
        profileHeightDialogUpdate.setOnClickListener(this);

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
        profileFoodTypeDialogTitleLin.setVisibility(View.GONE);
        profileFoodAllergyDialogTitleLin.setVisibility(View.GONE);
        profileHealthInfoDialogTitleLin.setVisibility(View.GONE);

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
                //User user = snapshot.getValue(User.class);
                user = snapshot.getValue(User.class);
                //trainer = user;
                System.out.println("********"+user+"*******");
                //profileName.setText(user.getName());

                String pattern = "dd-MM-yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

                /*profileWeight.setText(user.getWeight().toString() + " in kgs");
                profileHeight.setText(user.getHeight().toString() + " in cms");*/
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
                        }
                        else{

                        }


                    }
                    profileAdapter.notifyDataSetChanged();
                }

                //Food Allergy Recycler View Data
                if(user.getfoodAllergy()!=null) {
                    foodAllergy.clear();
                    for (Map.Entry  foodAllergyItem : user.getfoodAllergy().entrySet()) {

                        if (!"Others".equals(foodAllergyItem.getKey())){
                            foodAllergy.add(foodAllergyItem.getValue().toString());
                        }
                        else{
                            profileOtherRelativeLayFood.setVisibility(View.VISIBLE);
                            foodAllergyOther.setText(foodAllergyItem.getValue().toString());

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
            case R.id.profileDobDialogUpdate:

                updateProfile("dateOfBirth", userProfileUpdateValue);
                break;
            case R.id.profileWeightDialogUpdate:
                updateProfile("weight", profileWeightDialogInput.getText().toString());
                break;
            case R.id.profileHeightDialogUpdate:
                userProfileUpdateValue = profileWeightDialogInput.getText().toString();
                updateProfile("height",profileHeightDialogInput.getText().toString());
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

        healthIssues.put("Heart Problem", "Heart Problem");

        System.out.println("inUpdate"+ value);
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
            //hash.put("healthIssues",healthIssues);

        }
        databaseReference.updateChildren(hash);



    }



}