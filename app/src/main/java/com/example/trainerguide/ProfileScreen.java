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
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileScreen extends AppCompatActivity {

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
    TextView profileAccDrop, profilePersonalInfoDrop, profileFoodInfoDrop, profileWeight, profileHeight, profileHealthInfoDrop, foodAllergyOther;
    RelativeLayout accRelativeCollapse, personalRelativeCollapse, foodInfoRelativeCollapse, dobRelativeLay;
    LinearLayout healthInfoLinearCollapse;

    private String userId;
    private String path;

    //Common variables
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

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
            dobRelativeLay = findViewById(R.id.dobRelativeLay);

            //Food Info variables
            foodInfoCardView = findViewById(R.id.foodInfoCardView);
            profileFoodInfoDrop = findViewById(R.id.profileFoodInfoDrop);
            foodInfoRelativeCollapse = findViewById(R.id.foodInfoRelativeCollapse);
            profileOtherRelativeLayFood = findViewById(R.id.profileOtherRelativeLayFood);
            foodAllergyOther = findViewById(R.id.foodAllergyOther);

        //Health Info variables
        healthInfoCardView = findViewById(R.id.healthInfoCardView);
        healthInfoLinearCollapse = findViewById(R.id.healthInfoLinearCollapse);
        profileHealthInfoDrop = findViewById(R.id.profileHealthInfoDrop);

        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
        traineeMenu = findViewById(R.id.nav_trainees);
        //User Info variables
        userId = getIntent().getStringExtra("UserId");
        path = "Trainer/" + userId;

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

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();
            }
        });


        dobRelativeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileScreen.this, "DOb Clicked", Toast.LENGTH_SHORT).show();
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

                if (healthInfoLinearCollapse.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(healthInfoCardView,new AutoTransition());
                    healthInfoLinearCollapse.setVisibility(View.VISIBLE);
                    profileHealthInfoDrop.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up);

                }else {
                    TransitionManager.beginDelayedTransition(healthInfoCardView,new AutoTransition());
                    healthInfoLinearCollapse.setVisibility(View.GONE);
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
                        intent=new Intent(ProfileScreen.this,TraineesScreen.class);
                        intent.putExtra("UserId",userId);
                        startActivity(intent);
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

    private void Filechooser()
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

    //Method to populate Trainee data
    public void populateRecyclerData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path+"/healthIssues");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                healthItems = new ArrayList<>();

                for(DataSnapshot healthIssue : snapshot.getChildren()){

                    healthItems.add(healthIssue.getValue().toString());
                }

                profileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        System.out.println(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("********OnDataChange*******");
                Trainer user = snapshot.getValue(Trainer.class);
                trainer = user;
                System.out.println("********"+user+"*******");
                //profileName.setText(user.getName());
                profileWeight.setText(user.getWeight().toString() + " in kgs");
                profileHeight.setText(user.getHeight().toString() + " in cms");
                Picasso.get().load(user.getImage())
                        .placeholder(R.drawable.ic_share)
                        .fit()
                        .centerCrop()
                        .into(profileImage);

                //Dismiss Progress Dialog
                progressDialog.dismiss();


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
}