package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Food;
import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.example.trainerguide.validation.FoodValidation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class FoodSourceListScreen extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Food foodObject;
    private FoodValidation foodValidation = new FoodValidation();

    //Food- Recycler view variables
    private TextView foodName, foodNutritionType, foodCalorie, searchCalories;
    private EditText foodSourceAddName, foodSourceAddCalorie, foodSourceAddQuantity;
    private Button foodToggleVeg, foodToggleNonVeg;
    private RecyclerView foodListRecycler;
    private FoodListAdapter foodListAdapter;
    private List<Food> foodList = new ArrayList<>();

    private String userId;
    private String userPath;
    private String userType;

    private FloatingActionButton foodSourceAdd;
    private ProgressBar progress_bar;
    private String foodPath;

    //PopUp Dialog
    Dialog foodSourceDialog;
    ImageView foodSourceDialogClose;
    LinearLayout foodSourceAddDialogTitleLin;
    Button foodAddVegToggle, foodAddNonVegToggle, foodSourceAddDialogUpdate;
    Spinner foodSourceAddUnit, foodSourceAddNutritionSpin;
    ArrayAdapter<CharSequence> measurementSpinAdapter, nutritionSpinAdapter;
    String selectedMeasurementUnit, foodAddType, selectedNutritionType;


    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button toolBarNotification;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_source_list_screen);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        List<Food> foodAdd = new ArrayList<>();

         foodAdd.add(new Food("Capsicum - tiny chopped", "NA", 0.0, "Vegetarian", "cup", 0.5));

        for (Food food : foodAdd){
            databaseReference.child("Food").child("Common").child(food.getFoodType()).child(food.getName()).setValue(food);
        }


        //Food- Recycler view variables
        foodName = findViewById(R.id.foodItemName);
        foodNutritionType = findViewById(R.id.foodItemNt);
        foodCalorie = findViewById(R.id.foodItemCalorie);
        foodToggleVeg = findViewById(R.id.foodToggleVeg);
        foodToggleNonVeg = findViewById(R.id.foodToggleNonVeg);
        progress_bar = findViewById(R.id.progress_bar);
        foodSourceAdd = findViewById(R.id.foodSourceAdd);


        //PopUp Dialog
        foodSourceDialog = new Dialog(this);
        foodSourceDialog.setContentView(R.layout.food_source_dialog);
        foodSourceDialogClose = foodSourceDialog.findViewById(R.id.foodSourceDialogClose);
        foodSourceAddDialogTitleLin = foodSourceDialog.findViewById(R.id.foodSourceAddDialogTitleLin);

        foodSourceAddName = foodSourceDialog.findViewById(R.id.foodSourceAddName);
        foodSourceAddCalorie = foodSourceDialog.findViewById(R.id.foodSourceAddCalorie);
        foodSourceAddQuantity = foodSourceDialog.findViewById(R.id.foodSourceAddQuantity);
        searchCalories = foodSourceDialog.findViewById(R.id.searchCalories);
        foodAddVegToggle = foodSourceDialog.findViewById(R.id.foodAddVegToggle);
        foodAddNonVegToggle = foodSourceDialog.findViewById(R.id.foodAddNonVegToggle);
        foodSourceAddDialogUpdate = foodSourceDialog.findViewById(R.id.foodSourceAddDialogUpdate);
        foodSourceAddNutritionSpin = foodSourceDialog.findViewById(R.id.foodSourceAddNutritionSpin);
        foodSourceAddUnit = foodSourceDialog.findViewById(R.id.foodSourceAddUnitSpin);
        //Pop-up spinner
        //nutrition
        String[] nutritionRich = {"Carbohydrate","Fat", "Micro Nutrients","Protein", "NA" };
        ArrayAdapter<String> nutritionSpinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nutritionRich);
        nutritionSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodSourceAddNutritionSpin.setAdapter(nutritionSpinAdapter);
        foodSourceAddNutritionSpin.setOnItemSelectedListener(this);

        //measurement
        measurementSpinAdapter = ArrayAdapter.createFromResource(this, R.array.measurement_unit, android.R.layout.simple_spinner_item);
        measurementSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodSourceAddUnit.setAdapter(measurementSpinAdapter);
        foodSourceAddUnit.setOnItemSelectedListener(this);

        foodSourceDialogClose.setOnClickListener(this);
        foodSourceAdd.setOnClickListener(this);
        foodSourceAddDialogUpdate.setOnClickListener(this);
        searchCalories.setOnClickListener(this);
        foodAddVegToggle.setOnClickListener(this);
        foodAddNonVegToggle.setOnClickListener(this);
        foodAddVegToggle.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        foodAddNonVegToggle.setBackgroundColor(getResources().getColor(R.color.white));
        foodAddNonVegToggle.setTextColor(getResources().getColor(R.color.themeColourOne));

        foodToggleVeg.setOnClickListener(this);
        foodToggleNonVeg.setOnClickListener(this);
        foodAddType = "Veg";
        vegToggle();
        vegAddToggle();


        //Recycler view variables
        foodListRecycler = findViewById(R.id.foodListRecycler);
        foodListRecycler.setLayoutManager(new LinearLayoutManager(this));
        foodListAdapter = new FoodListAdapter(foodList, FoodSourceListScreen.this);
        foodListRecycler.setAdapter(foodListAdapter);

        //User Info variables
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        userType = sp.getString("ProfileType",null);
        userId = sp.getString("userId",null);
        userPath = userType+ "/" + userId;


        foodPath = "Food/Common/";
        PopulateFoodList("Veg");

        //Navigation view variables
        drawerLayout = findViewById(R.id.food_source_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolBarNotification.setOnClickListener(this);

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        toolbar.setTitleTextColor(getResources().getColor(R.color.themeColourThree));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));


        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        break;
                    /*case R.id.nav_trainees:

                        startActivity(new Intent(FoodSourceListScreen.this,TraineesScreen.class));
                        finish();
                        break;*/
                    case R.id.nav_notification:
                        startActivity(new Intent(FoodSourceListScreen.this,NotificationScreen.class));
                        finish();
                        break;
                    /*case R.id.nav_trainer:
                        startActivity(new Intent(FoodSourceListScreen.this,TrainerScreen.class));
                        finish();
                        break;*/
                    case R.id.nav_logout:
                        startActivity(new Intent(FoodSourceListScreen.this,MainActivity.class));
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
                        Toast.makeText(FoodSourceListScreen.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });



    }

    private void PopulateFoodList(String type) {
        DatabaseReference databaseReference;
        DatabaseReference databaseReferenceUser;

        if (type.equals("Veg")){
             databaseReference = FirebaseDatabase.getInstance().getReference(foodPath + "Vegetarian");
             databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Food/"+ userId + "/Vegetarian/");
        }

        else {
            databaseReference = FirebaseDatabase.getInstance().getReference(foodPath + "Nonveg");
            databaseReferenceUser = FirebaseDatabase.getInstance().getReference("Food/"+ userId + "/Nonveg/");

        }

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for(DataSnapshot foods : snapshot.getChildren()){

                    foodList.add(foods.getValue(Food.class));
                }
                foodListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progress_bar.setVisibility(View.GONE);
                for(DataSnapshot foods : snapshot.getChildren()){
                    System.out.println(foods+"foodListU");
                    foodList.add(foods.getValue(Food.class));
                }

                System.out.println(foodList.size()+"foodListU");
                foodListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        foodListAdapter.notifyDataSetChanged();
        foodListRecycler.setAdapter(foodListAdapter);

    }

    @Override
    public void onClick(View option) {

        switch (option.getId()){

            case R.id.searchCalories:
                if (! (foodSourceAddName.getText().toString().isEmpty())){
                    gotoUrl(foodSourceAddName.getText().toString());
                }
                else{
                    foodSourceAddName.setError("Please enter a valid food name");
                }
                break;

            case R.id.toolBarNotification:
                startActivity(new Intent(FoodSourceListScreen.this,NotificationScreen.class));
                finish();
                break;

            case R.id.foodToggleVeg:
                foodAddType = "Veg";
                progress_bar.setVisibility(View.VISIBLE);
/*
                foodToggleVeg.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                foodToggleVeg.setTextColor(getResources().getColor(R.color.white));
                foodToggleNonVeg.setBackgroundColor(getResources().getColor(R.color.white));
                foodToggleNonVeg.setTextColor(getResources().getColor(R.color.themeColourOne));
*/
                vegToggle();
                PopulateFoodList("Veg");
                break;

            case R.id.foodToggleNonVeg:
                foodAddType = "Non-Veg";
                progress_bar.setVisibility(View.VISIBLE);
 /*               foodToggleNonVeg.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                foodToggleNonVeg.setTextColor(getResources().getColor(R.color.white));
                foodToggleVeg.setBackgroundColor(getResources().getColor(R.color.white));
                foodToggleVeg.setTextColor(getResources().getColor(R.color.themeColourOne));*/
                nonVegToggle();
                PopulateFoodList("Nonveg");
                break;

            case R.id.foodSourceAdd:
                ShowDialog();
                break;

            case R.id.foodSourceAddDialogUpdate:

                if (ValidFood(foodSourceAddName.getText().toString(), foodSourceAddCalorie.getText().toString().isEmpty()?0.0:new Double(foodSourceAddCalorie.getText().toString()), foodSourceAddQuantity.getText().toString().isEmpty()?0.0:new Double(foodSourceAddQuantity.getText().toString()))){
                    if (foodAddType.equals("Veg")){
                        foodObject = new Food(foodSourceAddName.getText().toString(), selectedNutritionType, new Double(foodSourceAddCalorie.getText().toString()), "Vegetarian",selectedMeasurementUnit , new Double(foodSourceAddQuantity.getText().toString()));
                        updateFood();
                    }
                    else {
                        foodObject = new Food(foodSourceAddName.getText().toString(), selectedNutritionType, new Double(foodSourceAddCalorie.getText().toString()), "Nonveg",selectedMeasurementUnit , new Double(foodSourceAddQuantity.getText().toString()));
                        updateFood();
                    }
                }
                else {
                    System.out.println("Invalid Add Food");
                }

                break;

            case R.id.foodAddVegToggle:
                foodAddType = "Veg";
                vegAddToggle();
 /*               foodAddVegToggle.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                foodAddVegToggle.setTextColor(getResources().getColor(R.color.white));
                foodAddNonVegToggle.setBackgroundColor(getResources().getColor(R.color.white));
                foodAddNonVegToggle.setTextColor(getResources().getColor(R.color.themeColourOne));
*/                break;

            case R.id.foodAddNonVegToggle:
                foodAddType = "Non-Veg";
                nonVegAddToggle();
 /*               foodAddNonVegToggle.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
                foodAddNonVegToggle.setTextColor(getResources().getColor(R.color.white));
                foodAddVegToggle.setBackgroundColor(getResources().getColor(R.color.white));
                foodAddVegToggle.setTextColor(getResources().getColor(R.color.themeColourOne));
*/                break;

            case R.id.foodSourceDialogClose:
                foodSourceDialog.dismiss();
                foodSourceAddDialogTitleLin.setVisibility(View.GONE);
                break;
        }

    }

    private void gotoUrl(String name) {
        String urlLink = getString(R.string.urlLink) + name;
        Uri uri = Uri.parse(urlLink);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    private boolean ValidFood(String foodName, Double foodCalorie, Double foodQuantity) {

        String foodNameValid = foodValidation.foodNameValidation(foodName);
        String foodCalorieValid = foodValidation.foodCalorieValidation(foodCalorie);
        String foodQuantityValid = foodValidation.foodQuantityValidation(foodQuantity);

        if ( foodNameValid.equals("Valid") && foodCalorieValid.equals("Valid") && foodQuantityValid.equals("Valid")){
            return true;
        }

        if (!foodNameValid.equals("Valid")){
            foodSourceAddName.setError(foodNameValid);
        }
        if(!foodCalorieValid.equals("Valid")){
            foodSourceAddCalorie.setError(foodCalorieValid);
        }
        if(!foodQuantityValid.equals("Valid")){
            foodSourceAddQuantity.setError(foodQuantityValid);
        }

          return false;
    }

    private void ShowDialog() {
        foodSourceAddDialogTitleLin.setVisibility(View.VISIBLE);
        foodDialogClear();
        /*foodAddVegToggle.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        foodAddNonVegToggle.setBackgroundColor(getResources().getColor(R.color.white));
        foodAddNonVegToggle.setTextColor(getResources().getColor(R.color.themeColourOne));*/
        foodSourceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        foodSourceDialog.show();
    }

    private void updateFood(){
        DatabaseReference databaseReferenceCommon;

        if (foodAddType.equals("Veg")){

            databaseReferenceCommon = FirebaseDatabase.getInstance().getReference(foodPath + "Vegetarian").child(foodObject.getName());

            databaseReferenceCommon.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        AlertDialogBox alertDialogBox = new AlertDialogBox();
                        alertDialogBox.show(getSupportFragmentManager(),"Alert");

                    }
                    else{
                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference("Food/").child(userId +"/Vegetarian/");
                        HashMap hash= new HashMap();
                        hash.put(foodObject.getName(),foodObject);
                        databaseReference.updateChildren(hash);
                        foodAddType = "Veg";
                        vegToggle();
                        PopulateFoodList("Veg");
                        foodSourceDialog.dismiss();
                        foodDialogClear();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else {
            DatabaseReference databaseReference;
            System.out.println("updateNonVeg");
            databaseReference = FirebaseDatabase.getInstance().getReference("Food/").child(userId +"/Nonveg/");
            HashMap hash= new HashMap();
            hash.put(foodObject.getName(),foodObject);
            databaseReference.updateChildren(hash);
            foodAddType = "Non-Veg";
            nonVegToggle();
            PopulateFoodList("Non-Veg");
            foodSourceDialog.dismiss();
            foodDialogClear();
        }

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
            Intent intent = new Intent(FoodSourceListScreen.this,HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

    private void foodDialogClear(){
        foodAddType = "Veg";
        foodAddVegToggle.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        foodAddVegToggle.setTextColor(getResources().getColor(R.color.white));
        foodAddNonVegToggle.setBackgroundColor(getResources().getColor(R.color.white));
        foodAddNonVegToggle.setTextColor(getResources().getColor(R.color.themeColourOne));
        foodSourceAddName.getText().clear();
        foodSourceAddCalorie.getText().clear();
        foodSourceAddQuantity.getText().clear();


    }
    private void vegToggle(){
        foodToggleVeg.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        foodToggleVeg.setTextColor(getResources().getColor(R.color.white));
        foodToggleNonVeg.setBackgroundColor(getResources().getColor(R.color.white));
        foodToggleNonVeg.setTextColor(getResources().getColor(R.color.themeColourOne));

        //PopulateFoodList("Veg");

    }
    private void nonVegToggle(){
        foodToggleNonVeg.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        foodToggleNonVeg.setTextColor(getResources().getColor(R.color.white));
        foodToggleVeg.setBackgroundColor(getResources().getColor(R.color.white));
        foodToggleVeg.setTextColor(getResources().getColor(R.color.themeColourOne));
        //PopulateFoodList("Non-Veg");

    }
    private void vegAddToggle(){
        foodAddVegToggle.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        foodAddVegToggle.setTextColor(getResources().getColor(R.color.white));
        foodAddNonVegToggle.setBackgroundColor(getResources().getColor(R.color.white));
        foodAddNonVegToggle.setTextColor(getResources().getColor(R.color.themeColourOne));

    }
    private void nonVegAddToggle(){
        foodAddNonVegToggle.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        foodAddNonVegToggle.setTextColor(getResources().getColor(R.color.white));
        foodAddVegToggle.setBackgroundColor(getResources().getColor(R.color.white));
        foodAddVegToggle.setTextColor(getResources().getColor(R.color.themeColourOne));

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spin = (Spinner) parent;

        System.out.println(id + parent.getItemAtPosition(position).toString());
        switch (spin.getId()){

            case R.id.foodSourceAddNutritionSpin:
                selectedNutritionType = parent.getItemAtPosition(position).toString();
                System.out.println(selectedNutritionType +"selectedNutritionType");
                break;

            case R.id.foodSourceAddUnitSpin:
                selectedMeasurementUnit = parent.getItemAtPosition(position).toString();
                System.out.println(selectedMeasurementUnit +"selectedMeasurementUnit");

                break;
            default:
                break;
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}