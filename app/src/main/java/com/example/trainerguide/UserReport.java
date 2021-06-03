package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trainerguide.models.BmrProgress;
import com.example.trainerguide.models.MacroNutrient;
import com.example.trainerguide.models.Trainee;
import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.util.ArrayUtil;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class UserReport extends AppCompatActivity implements View.OnClickListener {

    //User Detail variables
    private String userId, path, userType;
    private Boolean IsTrainerProfile;
    private String navigationScreen="";
    private TextView reportWeight, reportBmi, noDataGraphLinear, dataGraphLinear, dataPieChart;

    //Progress Graph view
    private LinearLayout graphLinear;
    private GraphView progressGraphView;
    private List<BmrProgress> bmrProgressList;
    private LineGraphSeries<DataPoint> progressDataPoint;
    private Viewport viewport;
    private ProgressBar progressUserReport;

    //Pie chart
    private LinearLayout macroNutrientPieChart;
    private TextView macroNutrient1Label, macroNutrient2Label, macroNutrient3Label, totalCaloriesReport;
    private GridLayout macroNutrientGrid, userDetailGrid;
    private RelativeLayout totalCaloriesRel;


    //Homescreen variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button toolBarNotification;
    private TextView sideUserName;
    private MenuItem profileMenu, logoutMenu, shareMenu, ratingMenu, traineeMenu;
    Intent intent;
    Animation buttonBounce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report);

        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);


        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolbar = findViewById(R.id.tool_bar);
        //sideUserName = navigationView.findViewById(R.id.sideUserName);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        toolbar.setTitleTextColor(getResources().getColor(R.color.themeColourThree));

        //toolBarNotification.setBackgroundColor(getResources().getColor(R.color.white));
        toolBarNotification.setOnClickListener(this);

        ActionBarDrawerToggle toggle = CommonNavigator.navigatorInitmethod(drawerLayout,navigationView,toolbar,this);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeColourTwo));

        //toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.themeLightPink));

        //User Info variables
        final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userType = sp.getString("ProfileType",null);
        reportBmi = findViewById(R.id.reportBmi);
        reportWeight = findViewById(R.id.reportWeight);


        IsTrainerProfile = false;
        String passedUserId = "";

        if(getIntent().hasExtra("IsTrainer") &&
                getIntent().hasExtra("userId") &&
                getIntent().hasExtra("Screen"))
        {
            IsTrainerProfile = getIntent().getExtras().getBoolean("IsTrainer");
            passedUserId = getIntent().getExtras().getString("userId", "");
            navigationScreen = getIntent().getExtras().getString("Screen", "");

        }

        if(!passedUserId.equals("")){
            userType = IsTrainerProfile ? "Trainer" : "User";
            userId=passedUserId;
        }
        else {
            userType = sp.getString("ProfileType", null);
            userId = sp.getString("userId", null);

        }



        if (userType.equals("Trainer")){

        }
        else{

        }

        //userId = sp.getString("userId",null);
        path = userType+ "/" + userId;

        //Graph representation
        progressGraphView = findViewById(R.id.userProgressGraph);
        progressDataPoint = new LineGraphSeries<DataPoint>();
        viewport = progressGraphView.getViewport();
        GraphCustomisation();
        dataGraphLinear = findViewById(R.id.dataGraphLinear);
        noDataGraphLinear = findViewById(R.id.noDataGraphLinear);
        graphLinear = findViewById(R.id.graphLinear);
        dataPieChart = findViewById(R.id.dataPieChart);
        macroNutrient1Label = findViewById(R.id.macroNutrient1Label);
        macroNutrient2Label = findViewById(R.id.macroNutrient2Label);
        macroNutrient3Label  = findViewById(R.id.macroNutrient3Label);
        totalCaloriesReport = findViewById(R.id.totalCaloriesReport);
        macroNutrientGrid = findViewById(R.id.macroNutrientGrid);
        userDetailGrid = findViewById(R.id.userDetailGrid);
        totalCaloriesRel = findViewById(R.id.totalCaloriesRel);
        macroNutrientPieChart=findViewById(R.id.macroNutrientPieChart);

        progressUserReport = findViewById(R.id.progressUserReport);
        progressUserReport.setVisibility(View.VISIBLE);
        DisableView();
        PopulateUserDetails();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        startActivity(new Intent(UserReport.this,ProfileScreen.class));
                        finish();
                        break;
                    /*case R.id.nav_trainees:
                        intent=new Intent(ProfileScreen.this,TraineesScreen.class);
                        //intent.putExtra("UserId",userId);
                        startActivity(intent);
                        finish();
                        break;*/
                    case R.id.nav_notification:
                        startActivity(new Intent(UserReport.this,NotificationScreen.class));
                        finish();
                        break;

                    case R.id.nav_logout:
                        startActivity(new Intent(UserReport.this,MainActivity.class));
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
                        Toast.makeText(UserReport.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });



    }
    public void DisableView(){
        dataGraphLinear.setVisibility(View.GONE);
        noDataGraphLinear.setVisibility(View.GONE);
        graphLinear.setVisibility(View.GONE);
        dataPieChart.setVisibility(View.GONE);
        macroNutrientGrid.setVisibility(View.GONE);
        userDetailGrid.setVisibility(View.GONE);
        totalCaloriesRel.setVisibility(View.GONE);
        macroNutrientPieChart.setVisibility(View.GONE);
    }

    public void GraphCustomisation(){
        progressDataPoint.setThickness(8);
        progressDataPoint.setDataPointsRadius(6);
        progressDataPoint.setAnimated(true);
        progressDataPoint.setDrawDataPoints(true);
        progressDataPoint.setDrawBackground(true);
        progressDataPoint.setBackgroundColor(getResources().getColor(R.color.themeColourOne));
        progressDataPoint.setColor(getResources().getColor(R.color.themeColourTwo));

        viewport.setMinX(0);
        viewport.setMinY(0);
        viewport.setScrollable(true);
        viewport.setDrawBorder(true);
        viewport.setBackgroundColor(getResources().getColor(R.color.themeColourThree));
    }
    public void PopulateUserDetails(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (userType.equals("Trainer")){
                    Trainer user = snapshot.getValue(Trainer.class);
                    userDetailGrid.setVisibility(View.VISIBLE);
                    reportBmi.setText(user.getBmi().toString());
                    reportWeight.setText(user.getWeight().toString());
                    totalCaloriesRel.setVisibility(View.VISIBLE);
                    totalCaloriesReport.setText(user.getBmr().toString());

                    if (user.getBmrReport() == null || user.getBmrReport().size()<2){
                        dataGraphLinear.setVisibility(View.GONE);
                        noDataGraphLinear.setVisibility(View.VISIBLE);
                        graphLinear.setVisibility(View.GONE);
                        progressUserReport.setVisibility(View.GONE);

                    }
                    else {
                        dataGraphLinear.setVisibility(View.VISIBLE);
                        graphLinear.setVisibility(View.VISIBLE);
                        noDataGraphLinear.setVisibility(View.GONE);
                        progressUserReport.setVisibility(View.GONE);
                        for (BmrProgress bmiList: user.getBmrReport()){
                            long dayDifference = ((bmiList.getAddedDate().getTime() - user.getAccCreateDttm().getTime())/ 1000 / 60 / 60 / 24);
                            progressDataPoint.appendData(new DataPoint(dayDifference,bmiList.getBmrValue()), true, user.getBmrReport().size());

                            progressGraphView.addSeries(progressDataPoint);
                        }
                    }

                }

                else{
                    Trainee user = snapshot.getValue(Trainee.class);
                    userDetailGrid.setVisibility(View.VISIBLE);
                    reportBmi.setText(user.getBmi().toString());
                    reportWeight.setText(user.getWeight().toString());
                    totalCaloriesRel.setVisibility(View.VISIBLE);
                    totalCaloriesReport.setText(user.getBmr().toString());

                    //int[] data={0};


                   List<Integer> data = new ArrayList<>();
                   int[] color={getResources().getColor(R.color.themeColourOne),getResources().getColor(R.color.themeColourTwo),getResources().getColor(R.color.themeColourThree)};
                   //int[] color={getResources().getColor(R.color.themeLightGreen),getResources().getColor(R.color.themeLightPink),getResources().getColor(R.color.themeGreen)};


                    if (user.getBmrReport() == null || user.getBmrReport().size()<1){
                        dataGraphLinear.setVisibility(View.GONE);
                        noDataGraphLinear.setVisibility(View.VISIBLE);
                        graphLinear.setVisibility(View.GONE);
                        dataPieChart.setVisibility(View.GONE);
                        progressUserReport.setVisibility(View.GONE);

                    }
                    else {
                        dataGraphLinear.setVisibility(View.VISIBLE);
                        graphLinear.setVisibility(View.VISIBLE);
                        noDataGraphLinear.setVisibility(View.GONE);
                        dataPieChart.setVisibility(View.VISIBLE);
                        progressUserReport.setVisibility(View.GONE);


                        for (BmrProgress bmiList: user.getBmrReport()){
                            long dayDifference = ((bmiList.getAddedDate().getTime() - user.getAccCreateDttm().getTime())/ 1000 / 60 / 60 / 24);
                            progressDataPoint.appendData(new DataPoint(dayDifference,bmiList.getBmrValue()), true, user.getBmrReport().size());

                            progressGraphView.addSeries(progressDataPoint);
                        }

                        List<String> macro = new ArrayList<>();

                        if (user.getMacroNutrientDetails() != null) {
                            for (MacroNutrient macroList : user.getMacroNutrientDetails()){
                                dataPieChart.setText("Macro Nutrient Split Up");
                                macroNutrientGrid.setVisibility(View.VISIBLE);

                                if(macroList.getPercentage().intValue()>0){
                                    data.add((180*macroList.getPercentage().intValue())/100);
                                    macro.add(macroList.getName());

                                }
                                else{
                                    data.add(0);
                                    macro.add(macroList.getName());
                                }
                            }
                            macroNutrient1Label.setText(macro.get(0));
                            macroNutrient2Label.setText(macro.get(1));
                            macroNutrient3Label.setText(macro.get(2));
                            macroNutrientPieChart.setVisibility(View.VISIBLE);
                            //macroNutrientPieChart.set(getResources().getColor(R.color.lightGrey));
                            macroNutrientPieChart.addView(new PieChart(UserReport.this,3,data,color));
                        }
                        else{
                            dataPieChart.setText("No Macro Nutrient Split Up available");
                        }

                    }

                }

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
                    intent = new Intent(UserReport.this,TraineesScreen.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    intent = new Intent(UserReport.this,HomeScreen.class);
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
                startActivity(new Intent(UserReport.this,NotificationScreen.class));
                finish();
                break;
            default:
                break;
        }
    }
}