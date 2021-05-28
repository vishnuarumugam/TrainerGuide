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

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.Food;
import com.example.trainerguide.models.FoodList;
import com.example.trainerguide.models.MacroNutrient;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.fonts.otf.TableHeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.Math.round;

public class PrepareFoodChart extends AppCompatActivity implements FoodSourceAdapter.OnAddClickListener,
        SelectedFoodItemsAdapter.OnRemoveClickListener, SelectedFoodItemsAdapter.OnAddItemClickListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    //Navigation view variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button toolBarNotification;

    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;

    //Common variables
    private TextView totalCaloriesLabel, recommendedCalories;
    private Spinner activityLevelSpin;
    ArrayAdapter<CharSequence> activityLevelAdapter;
    String selectedActivityLevel;


    //Recycler view variables
    private RecyclerView foodsourceRecycler;
    private List<Food> foodList = new ArrayList<>();
    private List<Food> searchFoodList = new ArrayList<>();
    private FoodSourceAdapter foodSourceAdapter;

    private RecyclerView selectedFoodsourceRecycler;
    private List<Food> selectedFoodList = new ArrayList<Food>();
    private SelectedFoodItemsAdapter selectedFoodSourceAdapter;

    //private SearchView foodChartSearch;
    private EditText foodChartSearch;
    private RadioGroup radioFoodChartGroup;
    private RadioButton radioButtonVeg, radioButtonNV;

    //Pdf Generation
    private ImageButton generatePdfBtn;

    //User Detail variables
    private String userId;
    private String path;
    private String selectedTab;

    private TabLayout tablayout;

    NestedScrollView nestedScrollView;

    //Common variables
    private Intent intent;
    private String foodType = "Veg";
    public FoodList foodItemlist = new FoodList();
    List<Food> selectedFoodListHash = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_food_chart);

        //Pagination
        nestedScrollView = findViewById(R.id.scroll_view);

        //Pdf Generation
        generatePdfBtn = findViewById(R.id.generatePdfBtn);

        //Navigation view variables
        drawerLayout = findViewById(R.id.prepare_food_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tool_bar);
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolBarNotification.setOnClickListener(this);


        //Toolbar customisation
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        toolbar.setTitleTextColor(getResources().getColor(R.color.themeColourThree));
        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout, navigationView, toolbar, this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));




        //Menu Item variables
        profileMenu = findViewById(R.id.nav_profile);
//        traineeMenu = findViewById(R.id.nav_trainees);

        //Common variables
        tablayout = findViewById(R.id.foodTimingTabs);
        totalCaloriesLabel = findViewById(R.id.totalCalories);
        recommendedCalories = findViewById(R.id.recommendedCalories);
        activityLevelSpin = findViewById(R.id.activityLevelSpin);

        activityLevelAdapter = ArrayAdapter.createFromResource(this, R.array.activity_level, android.R.layout.simple_spinner_item);
        activityLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpin.setAdapter(activityLevelAdapter);
        activityLevelSpin.setOnItemSelectedListener(this);

        //Intent variables
        if(getIntent().hasExtra("userId") &&
                getIntent().hasExtra("userName") &&
                getIntent().hasExtra("totalCalories"))
        {
            recommendedCalories.setText(String.valueOf(getIntent().getExtras().getDouble("totalCalories")));
        }


        //Pdf Generation
        generatePdfBtn = findViewById(R.id.generatePdfBtn);
        generatePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                        String [] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,1000);
                    }
                    else{
                        if (selectedFoodListHash.size()>0){
                            generatePdf();
                        }
                        else{
                            CustomDialogClass customDialogClass = new CustomDialogClass(PrepareFoodChart.this, "Pdf Generation !!!", "Please choose atleast one item in diet chart", "Normal");
                            customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            customDialogClass.show();
                        }
                    }
                }
                else {
                    if (selectedFoodListHash.size()>0){
                        generatePdf();
                    }
                    else{
                        CustomDialogClass customDialogClass = new CustomDialogClass(PrepareFoodChart.this, "Pdf Generation !!!", "Please choose atleast one item in diet chart", "Normal");
                        customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        customDialogClass.show();
                    }

                }
            }
        });


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
                    /*case R.id.nav_trainees:
                        break;*/
                    case R.id.nav_notification:
                        startActivity(new Intent(PrepareFoodChart.this, NotificationScreen.class));
                        finish();
                        break;
                    /*case R.id.nav_trainer:
                        startActivity(new Intent(PrepareFoodChart.this, TrainerScreen.class));
                        finish();
                        break;*/
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
        radioFoodChartGroup = findViewById(R.id.radioFoodChartGroup);

        foodChartSearch = findViewById(R.id.foodChartSearch);
        radioButtonVeg = findViewById(R.id.radioButtonVeg);
        radioButtonVeg.setChecked(true);
        radioButtonNV = findViewById(R.id.radioButtonNV);

        radioButtonVeg.setOnClickListener(this);
        radioButtonNV.setOnClickListener(this);

        foodChartSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                searchFoodList.clear();
                if(s.length()>0){
                    for (Food searchFood : foodList){
                        /*foodSourceAdapter = new FoodSourceAdapter(searchFoodList, PrepareFoodChart.this);
                        foodsourceRecycler.setAdapter(foodSourceAdapter);
                        foodSourceAdapter.setOnAddClickListener(PrepareFoodChart.this);*/
                        //foodsourceRecycler.setAdapter(foodSourceAdapter);
                        if (searchFood.getName().contains(s.toString())){
                            searchFoodList.add(searchFood);
                        }
                    }
                    foodList.clear();
                    foodList.addAll(searchFoodList);
                    foodSourceAdapter.notifyDataSetChanged();
                    foodsourceRecycler.setAdapter(foodSourceAdapter);

                }
                else{
                    //foodSourceAdapter = new FoodSourceAdapter(foodList, PrepareFoodChart.this);
                    //foodsourceRecycler.setAdapter(foodSourceAdapter);
                    foodItemlist = populateSourceData(userId, foodType);
                    RerenderSelectedList();
                }

            }
        });

        /*foodChartSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("newText" + newText);
                foodSourceAdapter.getFilter().filter(newText);


                return false;
            }
        });*/

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
        foodItemlist = populateSourceData(userId, "Veg");

        RerenderSelectedList();

    }

    private FoodList populateSourceData(String userid, String foodType) {
        final FoodList foodListItem = new FoodList();
        DatabaseReference databaseReferenceVeg = FirebaseDatabase.getInstance().getReference("Food/" + userid + "/Vegetarian/");
        DatabaseReference databaseReferenceNonVeg = FirebaseDatabase.getInstance().getReference("Food/" + userid + "/Nonveg/");
        DatabaseReference databaseReferenceSourceVeg = FirebaseDatabase.getInstance().getReference("Food/Common/Vegetarian/");
        DatabaseReference databaseReferenceSourceNonVeg = FirebaseDatabase.getInstance().getReference("Food/Common/Nonveg/");
        foodList.clear();

        if (foodType.equals("Veg")){
            databaseReferenceVeg.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        foodListItem.setFoodItemsVeg(datasnapshot.getValue(Food.class));
                    }
                    Render(foodListItem,foodType);
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
                    Render(foodListItem,foodType);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            foodList.addAll(foodListItem.getFoodItemsVeg());
            foodSourceAdapter.notifyDataSetChanged();
            foodsourceRecycler.setAdapter(foodSourceAdapter);
        }
        else{
            databaseReferenceNonVeg.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        foodListItem.setFoodItemsNonVeg(datasnapshot.getValue(Food.class));
                    }
                    Render(foodListItem,foodType);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReferenceSourceNonVeg.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                        foodListItem.setFoodItemsNonVeg(datasnapshot.getValue(Food.class));
                    }
                    Render(foodListItem,foodType);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return foodListItem;
    }

    public void Render(FoodList foodListItem, String foodType){
        if(foodType.equals("Veg")){
            foodList.clear();
            foodList.addAll(foodListItem.getFoodItemsVeg());
        }else{
            foodList.clear();
            foodList.addAll(foodListItem.getFoodItemsNonVeg());
        }
        foodSourceAdapter.notifyDataSetChanged();
        foodsourceRecycler.setAdapter(foodSourceAdapter);
    }

    @Override
    public void onClick(View option) {

        switch (option.getId()){

            case R.id.toolBarNotification:
                startActivity(new Intent(PrepareFoodChart.this,NotificationScreen.class));
                finish();
                break;

            case R.id.radioButtonVeg:
                foodType = "Veg";
                foodItemlist = populateSourceData(userId, "Veg");
                RerenderSelectedList();
                break;
            case R.id.radioButtonNV:
                foodType = "NonVeg";
                foodItemlist = populateSourceData(userId, "NonVeg");
                RerenderSelectedList();
                break;
            default:
                break;
        }

    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer((GravityCompat.START));
        } else {
            startActivity(new Intent(PrepareFoodChart.this, TraineesScreen.class));
            finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAddclick(int position) {
        Food food= foodList.get(position);


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

    private void generatePdf() {

        Document document = new Document();
        String pdfFile = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String filePath=  (CreateAppPath.getAppPath(PrepareFoodChart.this)+getIntent().getExtras().getString("userName").toUpperCase()+ pdfFile +".pdf");
        BaseColor colourAccent = new BaseColor(0,153,204,255);
        BaseColor colourYellow = new BaseColor(250,204,46,255);

        try{
            PdfWriter.getInstance(document,new FileOutputStream(filePath));

            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();

            //Custom font
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
            Font appTitleFont = new Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            Font titleFont = new Font(fontName, 28.0f, Font.BOLD, colourYellow);
            Font itemNameFont = new Font(fontName, 24, Font.BOLD);

            //Create title of Doc
            addNewItem(document, "Trainer Guide", Element.ALIGN_CENTER, appTitleFont);
            addLineSpace(document);
            addLineSeparator(document);
            addLineSpace(document);
            addNewItem(document, "DIET PLAN", Element.ALIGN_CENTER, titleFont);
            addLineSpace(document);
            addLineSeparator(document);

            List<String> foodTime = new ArrayList<>();
            foodTime.add("Break Fast");
            foodTime.add("Snacks M");
            foodTime.add("Lunch");
            foodTime.add("Snacks E");
            foodTime.add("Dinner");

            List<Food>  pdfFoodList = new ArrayList<>();

            HashMap<String,Double> macroList= new HashMap<>();
            macroList.put("Carbohydrate", 0.0);
            macroList.put("Fat", 0.0);
            macroList.put("Micro Nutrients", 0.0);
            macroList.put("Protein", 0.0);

            for (String foodSlot : foodTime){
                String foodSlotTitle = "";

             for (Food foodItems: selectedFoodListHash){

                    switch (foodSlot){
                        case "Break Fast":
                            if (foodSlot.equals(foodItems.getTab())){
                                foodSlotTitle = "Break Fast";
                                if ((!foodItems.getNutritionType().equals("NA"))) {
                                    macroList.put(foodItems.getNutritionType(), macroList.get(foodItems.getNutritionType()) + foodItems.getTotalCalorie());
                                }
                                pdfFoodList.add(foodItems);
                            }
                            break;
                        case "Snacks M":
                            if (foodSlot.equals(foodItems.getTab())){
                                foodSlotTitle = "Snacks M";
                                if ((!foodItems.getNutritionType().equals("NA"))) {
                                    macroList.put(foodItems.getNutritionType(), macroList.get(foodItems.getNutritionType()) + foodItems.getTotalCalorie());
                                }
                                pdfFoodList.add(foodItems);
                            }
                            break;
                        case "Lunch":
                            if (foodSlot.equals(foodItems.getTab())){
                                foodSlotTitle = "Lunch";
                                if ((!foodItems.getNutritionType().equals("NA"))) {
                                    macroList.put(foodItems.getNutritionType(), macroList.get(foodItems.getNutritionType()) + foodItems.getTotalCalorie());
                                }
                                pdfFoodList.add(foodItems);
                            }
                            break;
                        case "Snacks E":
                            if (foodSlot.equals(foodItems.getTab())){
                                foodSlotTitle = "Snacks E";
                                if ((!foodItems.getNutritionType().equals("NA"))) {
                                    macroList.put(foodItems.getNutritionType(), macroList.get(foodItems.getNutritionType()) + foodItems.getTotalCalorie());
                                }
                                pdfFoodList.add(foodItems);
                            }
                            break;
                        case "Dinner":
                            if (foodSlot.equals(foodItems.getTab())){
                                foodSlotTitle = "Dinner";
                                if ((!foodItems.getNutritionType().equals("NA"))) {
                                    macroList.put(foodItems.getNutritionType(), macroList.get(foodItems.getNutritionType()) + foodItems.getTotalCalorie());
                                }
                                pdfFoodList.add(foodItems);
                            }
                            break;
                        default:
                            break;
                    }

                }
                if (pdfFoodList.size()>0){
                    addEmptyLineSpace(document,2);
                    addNewItem(document, foodSlotTitle, Element.ALIGN_CENTER, itemNameFont);
                    addEmptyLineSpace(document,1);
                    addFoodTable(document, pdfFoodList);
                    pdfFoodList.clear();
               }
            }
            if (selectedFoodListHash.size()>0){
                addEmptyLineSpace(document,2);
                addNewItem(document, "Macro Nutrients Details", Element.ALIGN_CENTER, itemNameFont);
                addEmptyLineSpace(document,1);
                addMacroNutTable(document,macroList);
                Double totalCalorie = macroList.get("Carbohydrate") + macroList.get("Fat") + macroList.get("Micro Nutrients") + macroList.get("Protein");
                addEmptyLineSpace(document,1);
                addNewItem(document, "Total Calories: " + totalCalorie, Element.ALIGN_RIGHT, itemNameFont);

            }


            document.close();
            Toast.makeText(this, "File created", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e){
            Toast.makeText(this, "This :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Error", e.getMessage());
        }
    }

    private void addMacroNutTable(Document document, HashMap<String, Double> macroList) throws DocumentException, IOException{
        {
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
            Font itemNameFont = new Font(fontName, 24, Font.NORMAL);
            Font itemDetailFont = new Font(fontName, 18, Font.NORMAL);
            BaseColor colourAccent = new BaseColor(228,227,227,255);
            Double totalCalorie = macroList.get("Carbohydrate") + macroList.get("Fat") + macroList.get("Micro Nutrients") + macroList.get("Protein");
            Double totalNutritionCal = macroList.get("Carbohydrate") + macroList.get("Fat") + macroList.get("Protein");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User/" + getIntent().getExtras().getString("userId"));
            HashMap hash= new HashMap();
            List<MacroNutrient> macroNutrients = new ArrayList<>();

            hash.put("macroNutrientDetails",macroNutrients);
            databaseReference.updateChildren(hash);


                PdfPTable table = new PdfPTable(3);

                table.setHorizontalAlignment(Element.ALIGN_CENTER);

                PdfPCell commonCell;
                commonCell = new PdfPCell(new Phrase("Nutrition Name", itemNameFont));
                commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                commonCell.setBackgroundColor(colourAccent);
                commonCell.setPadding(5);
                table.addCell(commonCell);


                commonCell = new PdfPCell(new Phrase("Calorie", itemNameFont));
                commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                commonCell.setBackgroundColor(colourAccent);
                commonCell.setPadding(5);
                table.addCell(commonCell);

                commonCell = new PdfPCell(new Phrase("Percentage", itemNameFont));
                commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                commonCell.setBackgroundColor(colourAccent);
                commonCell.setPadding(5);
                table.addCell(commonCell);

                for (HashMap.Entry<String, Double> macroNutrition : macroList.entrySet()){

                    if (!(macroNutrition.getKey().equals("Micro Nutrients"))){
                        commonCell = new PdfPCell(new Phrase(macroNutrition.getKey(), itemDetailFont));
                        commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        commonCell.setPadding(5);
                        table.addCell(commonCell);


                        commonCell = new PdfPCell(new Phrase(macroNutrition.getValue().toString() +" kcal", itemDetailFont));
                        commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        commonCell.setPadding(5);
                        table.addCell(commonCell);

                        commonCell = new PdfPCell(new Phrase(round((macroNutrition.getValue() * 100) / totalNutritionCal) + "%", itemDetailFont));
                        commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        commonCell.setPadding(5);
                        table.addCell(commonCell);

                        macroNutrients.add(new MacroNutrient(macroNutrition.getKey(),macroNutrition.getValue(),new Double(round((macroNutrition.getValue() * 100) / totalNutritionCal))));
                    }


                }
                hash.put("macroNutrientDetails",macroNutrients);
                databaseReference.updateChildren(hash);


            try {
                    document.add(table);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }

            }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case 1000:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if (selectedFoodListHash.size()>0){
                        generatePdf();
                    }
                    else{
                        CustomDialogClass customDialogClass = new CustomDialogClass(PrepareFoodChart.this, "Pdf Generation !!!", "Please choose atleast one item in diet chart", "Normal");
                        customDialogClass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        customDialogClass.show();
                    }

                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void addLineSeparator(Document document) throws DocumentException {

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0,68));
        lineSeparator.setLineWidth(3.0f);
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);

    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addEmptyLineSpace(Document document, Integer emptyLineNo) throws DocumentException {
        for (int i=0; i<emptyLineNo; i++){
            document.add(new Paragraph("\n"));
        }

    }

    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {

        Chunk chunk = new Chunk(text, font);

        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);

    }

    private void addFoodTable (Document document, List<Food>  pdfFoodList) throws DocumentException, IOException {
        BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);
        Font itemNameFont = new Font(fontName, 24, Font.NORMAL);
        Font itemDetailFont = new Font(fontName, 18, Font.NORMAL);
        BaseColor colourAccent = new BaseColor(228,227,227,255);

        if (pdfFoodList.size()>0){

            PdfPTable table = new PdfPTable(3);

            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell commonCell;
            commonCell = new PdfPCell(new Phrase("Item Name", itemNameFont));
            commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            commonCell.setBackgroundColor(colourAccent);
            commonCell.setPadding(5);
            table.addCell(commonCell);


            commonCell = new PdfPCell(new Phrase("Quantity", itemNameFont));
            commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            commonCell.setBackgroundColor(colourAccent);
            commonCell.setPadding(5);
            table.addCell(commonCell);

            commonCell = new PdfPCell(new Phrase("Calorie", itemNameFont));
            commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            commonCell.setBackgroundColor(colourAccent);
            commonCell.setPadding(5);
            table.addCell(commonCell);

            for (Food foodItems : pdfFoodList){

                commonCell = new PdfPCell(new Phrase(foodItems.getName().toUpperCase(), itemDetailFont));
                commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                commonCell.setPadding(5);
                table.addCell(commonCell);


                commonCell = new PdfPCell(new Phrase((foodItems.getQuantity() * foodItems.getValue()) +" "+ foodItems.getMeasurementUnit().toUpperCase() , itemDetailFont));
                commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                commonCell.setPadding(5);
                table.addCell(commonCell);

                commonCell = new PdfPCell(new Phrase(foodItems.getTotalCalorie().toString() +" kcal", itemDetailFont));
                commonCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                commonCell.setPadding(5);
                table.addCell(commonCell);
            }
            try {
                document.add(table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getItemAtPosition(position).toString()){

            case "Sedentary":
                recommendedCalories.setText(String.valueOf(getIntent().getExtras().getDouble("totalCalories") * 1.2));
                break;
            case "Little Exercise":
                recommendedCalories.setText(String.valueOf(getIntent().getExtras().getDouble("totalCalories") * 1.35));
                break;
            case "Regular Exercise":
                recommendedCalories.setText(String.valueOf(getIntent().getExtras().getDouble("totalCalories") * 1.46));
                break;
            case "Daily Exercise":
                recommendedCalories.setText(String.valueOf(getIntent().getExtras().getDouble("totalCalories") * 1.56));
                break;
            case "Daily Intensive":
                recommendedCalories.setText(String.valueOf(getIntent().getExtras().getDouble("totalCalories") * 1.8));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

