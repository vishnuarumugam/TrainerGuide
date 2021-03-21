package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Food;
import com.example.trainerguide.models.FoodList;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrepareFoodChart extends AppCompatActivity implements FoodSourceAdapter.OnAddClickListener,
        SelectedFoodItemsAdapter.OnRemoveClickListener, SelectedFoodItemsAdapter.OnAddItemClickListener {

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    private TextView totalCaloriesLabel;

    //Recycler view variables
    private RecyclerView foodsourceRecycler;
    private List<Food> foodList = new ArrayList<>();
    private FoodSourceAdapter foodSourceAdapter;

    private RecyclerView selectedFoodsourceRecycler;
    private List<Food> selectedFoodList = new ArrayList<Food>();
    private SelectedFoodItemsAdapter selectedFoodSourceAdapter;

    //User Detail variables
    private String userId;
    private String path;
    private String selectedTab;

    private TabLayout tablayout;

    NestedScrollView nestedScrollView;

    //Common variables
    private Intent intent;

    public FoodList foodItemlist = new FoodList();

    List<Food> selectedFoodListHash = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_food_chart);

        //Pagination
        nestedScrollView = findViewById(R.id.scroll_view);

        //Navigation view variables
        drawerLayout = findViewById(R.id.prepare_food_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);

        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout, navigationView, toolbar, this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.yellow));

        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
        traineeMenu = findViewById(R.id.nav_trainees);

        tablayout = findViewById(R.id.foodTimingTabs);
        totalCaloriesLabel = findViewById(R.id.totalCalories);

        //Method to re-direct the page from menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        intent = new Intent(PrepareFoodChart.this, ProfileScreen.class);
                        //intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_trainees:
                        break;
                    case R.id.nav_notification:
                        startActivity(new Intent(PrepareFoodChart.this, NotificationScreen.class));
                        finish();
                        break;
                    case R.id.nav_trainer:
                        startActivity(new Intent(PrepareFoodChart.this, TrainerScreen.class));
                        finish();
                        break;
                    case R.id.nav_logout:
                        startActivity(new Intent(PrepareFoodChart.this, MainActivity.class));
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
                        Toast.makeText(PrepareFoodChart.this, "profile", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        selectedTab = tablayout.getTabAt(tablayout.getSelectedTabPosition()).getText().toString();

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                selectedTab = tab.getText().toString();
                selectedFoodList.clear();
                RerenderSelectedList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Recycler view variables
        foodsourceRecycler = findViewById(R.id.foodsourceRecycler);
        foodsourceRecycler.setLayoutManager(new LinearLayoutManager(this));
        foodSourceAdapter = new FoodSourceAdapter(foodList, PrepareFoodChart.this);
        foodsourceRecycler.setAdapter(foodSourceAdapter);
        foodSourceAdapter.setOnAddClickListener(PrepareFoodChart.this);


        //Recycler view variables
        selectedFoodsourceRecycler = findViewById(R.id.selectedFoodRecycler);
        selectedFoodsourceRecycler.setLayoutManager(new LinearLayoutManager(this));
        selectedFoodSourceAdapter = new SelectedFoodItemsAdapter(selectedFoodList,PrepareFoodChart.this);
        selectedFoodsourceRecycler.setAdapter(selectedFoodSourceAdapter);
        selectedFoodSourceAdapter.setOnAddClickListener(PrepareFoodChart.this);
        selectedFoodSourceAdapter.setOnRemoveClickListener(PrepareFoodChart.this);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        path = "Food/" + userId + "/";
        //path = userId + "/usersList";
        System.out.println("***userId***" + userId + "******");
        foodItemlist = populateSourceData(userId);
        RerenderSelectedList();

    }

    private FoodList populateSourceData(String userid) {
        final FoodList foodListItem = new FoodList();
        DatabaseReference databaseReferenceVeg = FirebaseDatabase.getInstance().getReference("Food/" + userid + "/Vegetarian/");
        DatabaseReference databaseReferenceNonVeg = FirebaseDatabase.getInstance().getReference("Food/" + userid + "/Nonveg/");
        DatabaseReference databaseReferenceSourceVeg = FirebaseDatabase.getInstance().getReference("Food/Common/Vegetarian/");
        DatabaseReference databaseReferenceSourceNonVeg = FirebaseDatabase.getInstance().getReference("Food/Common/Nonveg/");
        foodList.clear();

        databaseReferenceVeg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    foodListItem.setFoodItemsVeg(datasnapshot.getValue(Food.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceNonVeg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    foodListItem.setFoodItemsNonVeg(datasnapshot.getValue(Food.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReferenceSourceVeg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    foodListItem.setFoodItemsVeg(datasnapshot.getValue(Food.class));
                }
                foodList.addAll(foodListItem.getFoodItemsVeg());
                foodSourceAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        foodsourceRecycler.setAdapter(foodSourceAdapter);


        databaseReferenceSourceNonVeg.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    foodListItem.setFoodItemsNonVeg(datasnapshot.getValue(Food.class));
                }
                foodList.addAll(foodListItem.getFoodItemsNonVeg());
                foodSourceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        foodsourceRecycler.setAdapter(foodSourceAdapter);
        return foodListItem;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer((GravityCompat.START));
        } else {
            startActivity(new Intent(PrepareFoodChart.this, HomeScreen.class));
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAddclick(int position) {
        Food food = foodList.get(position);

        Food foodItem = new Food();
        foodItem.setName(food.getName());
        foodItem.setCalorieValue(food.getCalorieValue());
        foodItem.setFoodType(food.getFoodType());
        foodItem.setMeasurementUnit(food.getMeasurementUnit());
        foodItem.setNutritionType(food.getNutritionType());
        foodItem.setQuantity(food.getQuantity());
        foodItem.totalCalorie=food.getCalorieValue();
        boolean exists = selectedFoodListHash.stream().filter(x->x.tab.equals(selectedTab) && x.getName().equals(food.getName())).findFirst().orElse(null) == null ? false : true;
        foodItem.tab = tablayout.getTabAt(tablayout.getSelectedTabPosition()).getText().toString();
        foodItem.value=1;
        if (!exists) {
            selectedFoodListHash.add(foodItem);
            RerenderSelectedList();
        }

        if(selectedFoodListHash.size()==0){
            selectedFoodListHash.add(food);
            RerenderSelectedList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAddItemclick(int position) {
        final Food food = selectedFoodList.get(position);

        for (Food foodItem : selectedFoodListHash) {
            if (foodItem.tab.equals(selectedTab) && foodItem.getName().equals(food.getName())) {
                foodItem.value = foodItem.value + 1;
                foodItem.totalCalorie = foodItem.value * foodItem.getCalorieValue();
                RerenderSelectedList();
                break;
            }
        }
    }

    @Override
    public void onRemoveItemclick(int position) {
        final Food food = selectedFoodList.get(position);
        boolean remove = false;
        for (Food foodItem : selectedFoodListHash) {
            if (foodItem.tab.equals(selectedTab) && foodItem.getName().equals(food.getName())) {
                foodItem.value = foodItem.value - 1;
                foodItem.totalCalorie = foodItem.value * foodItem.getCalorieValue();
                if (foodItem.value == 0) {
                    remove = true;
                }
                else {
                    RerenderSelectedList();
                }
                break;
            }
        }
        if (remove) {
            selectedFoodListHash.remove(food);
            RerenderSelectedList();
        }
    }

    private void RerenderSelectedList(){
        List<Food> selectedFoodItems = new ArrayList<>();
        double totalCalories = 0.0;
        for(Food foodItem : selectedFoodListHash) {
            if (foodItem.tab.equals(selectedTab)) {
                    totalCalories = totalCalories+(foodItem.value * foodItem.getCalorieValue());
                    selectedFoodItems.add(foodItem);
            }
        }
        selectedFoodList.clear();
        selectedFoodList.addAll(selectedFoodItems);
        totalCaloriesLabel.setText(String.valueOf(totalCalories));
        selectedFoodSourceAdapter.notifyDataSetChanged();
    }
}

