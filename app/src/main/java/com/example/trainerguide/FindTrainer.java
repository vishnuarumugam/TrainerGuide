package com.example.trainerguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.trainerguide.models.Trainer;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindTrainer extends AppCompatActivity implements View.OnClickListener, TrainerAdapter.OnAddClickListener {

    private Toolbar toolbar;
    private Button toolBarNotification;
    private Animation buttonBounce;

    //FindTrainer Screen variables
    private TextView findTrainerRating, findTrainerFees, filterClear, findTrainer;
    private Slider trainerRatingSlider, trainerFeesSlider;
    private RelativeLayout noSearchResult;
    private ImageButton filterHide;
    private CardView filterLayout;

    //Recycler view variables
    private RecyclerView trainerFilterRecycler;
    private List<Trainer> trainerList = new ArrayList<>();
    private TrainerAdapter trainerFilterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trainer);

        // loading Animation from
        buttonBounce= AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        //FindTrainer Screen variables
        findTrainerRating = findViewById(R.id.findTrainerRating);
        findTrainerFees = findViewById(R.id.findTrainerFees);
        filterClear = findViewById(R.id.filterClear);
        filterClear.setVisibility(View.VISIBLE);
        findTrainer = findViewById(R.id.findTrainer);
        filterClear.setOnClickListener(this);
        findTrainer.setOnClickListener(this);
        trainerRatingSlider = findViewById(R.id.trainerRatingSlider);
        trainerFeesSlider = findViewById(R.id.trainerFeesSlider);
        noSearchResult = findViewById(R.id.noSearchResult);
        noSearchResult.setVisibility(View.GONE);
        filterHide = findViewById(R.id.filterHide);
        filterHide.setVisibility(View.VISIBLE);
        filterHide.setOnClickListener(this);
        filterHide.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
        filterLayout = findViewById(R.id.filterLayout);
        filterLayout.setVisibility(View.VISIBLE);

        //Recycler view variables
        trainerFilterRecycler = findViewById(R.id.trainerFilterRecycler);
        trainerFilterRecycler.setVisibility(View.VISIBLE);
        trainerFilterRecycler.setLayoutManager(new LinearLayoutManager(this));
        trainerFilterAdapter = new TrainerAdapter(trainerList,FindTrainer.this);
        trainerFilterRecycler.setAdapter(trainerFilterAdapter);
        trainerFilterAdapter.setOnAddClickListener(FindTrainer.this);

        //tool bar variables
        toolBarNotification = findViewById(R.id.toolBarNotification);
        toolbar = findViewById(R.id.back_tool_bar);
        toolbar.setTitle("Find Trainer");
        setSupportActionBar(toolbar);
        toolBarNotification.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FindTrainer.this,HomeScreen.class));
                finish();
            }
        });

        trainerRatingSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                findTrainerRating.setText(value + " / 5");
            }
        });
        trainerFeesSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull  Slider slider, float value, boolean fromUser) {
                findTrainerFees.setText((int) value + "k / 100k");
            }
        });



    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(FindTrainer.this,HomeScreen.class));
        finish();
    }

    @Override
    public void onClick(View option) {

        switch (option.getId()){
            case R.id.toolBarNotification:
                toolBarNotification.startAnimation(buttonBounce);
                startActivity(new Intent(FindTrainer.this,NotificationScreen.class));
                finish();
                break;
            case R.id.filterHide:
                System.out.println("v=g");
                if (filterClear.getVisibility() == View.VISIBLE){
                    System.out.println("v");
                    filterClear.setVisibility(View.GONE);
                    filterLayout.setVisibility(View.GONE);
                    filterHide.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up);
                }
                else if (filterClear.getVisibility() == View.GONE){
                    System.out.println("g");
                    filterClear.setVisibility(View.VISIBLE);
                    filterLayout.setVisibility(View.VISIBLE);
                    filterHide.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
                }

                break;
            case R.id.filterClear:
                filterClear.startAnimation(buttonBounce);
                filterClear.clearAnimation();
                findTrainerRating.setText("0 / 5");
                findTrainerFees.setText("0k / 100k");
                trainerFeesSlider.setValue(0);
                trainerRatingSlider.setValue(0);
                filterLayout.setVisibility(View.VISIBLE);
                filterHide.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
                trainerList.clear();
                trainerFilterAdapter.notifyDataSetChanged();
                break;
            case R.id.findTrainer:
                findTrainer.startAnimation(buttonBounce);
                System.out.println(trainerRatingSlider.getValue());
                System.out.println(trainerFeesSlider.getValue()*1000);
                Query trainerFilter;
                if (trainerRatingSlider.getValue() == 0.0){
                     trainerFilter = FirebaseDatabase.getInstance().getReference("Trainer");
                }
                else{
                     trainerFilter = FirebaseDatabase.getInstance().getReference("Trainer")
                            .orderByChild("rating")
                            .endAt(new Double(trainerRatingSlider.getValue()));
                }

                trainerFilter.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trainerList.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Trainer trainer = dataSnapshot.getValue(Trainer.class);

                            if ((trainer.getSubscriptionFees()!=null)){
                                if (trainer.getSubscriptionFees() <= trainerFeesSlider.getValue()*1000){
                                    trainerList.add(trainer);
                                }
                            }
                            else {
                                trainerList.add(trainer);
                            }


                        }
                        if (trainerList.size()==0){
                            trainerFilterRecycler.setVisibility(View.GONE);
                            noSearchResult.setVisibility(View.VISIBLE);
                            filterClear.setVisibility(View.VISIBLE);
                            filterHide.setBackgroundResource(R.drawable.ic_dropdown_arrow_down);
                            filterLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            trainerFilterRecycler.setVisibility(View.VISIBLE);
                            noSearchResult.setVisibility(View.GONE);
                            filterClear.setVisibility(View.GONE);
                            filterLayout.setVisibility(View.GONE);
                            filterHide.setBackgroundResource(R.drawable.ic_baseline_arrow_drop_up);
                            trainerFilterAdapter.notifyDataSetChanged();
                        }
                        System.out.println("filterClear"+filterClear.getVisibility());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onAddclick(int position) {
        final Trainer trainer = trainerList.get(position);

          final SharedPreferences sp;
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = sp.getString("userId", null);

        Intent intent = new Intent(FindTrainer.this,TrainerProfileView.class);
        intent.putExtra("Screen", "FindTrainer");

        if(trainer.getUserId().equals(userId)){
            intent.putExtra("IsTrainer", true);
        }
        else {
            intent.putExtra("userId", trainer.getUserId());
            intent.putExtra("IsTrainer", true);
            intent.putExtra("ReadOnly", true);
        }
        startActivity(intent);
        finish();
    }
}