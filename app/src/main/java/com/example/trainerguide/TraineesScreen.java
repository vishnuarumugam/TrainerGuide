package com.example.trainerguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class TraineesScreen extends AppCompatActivity {

    private RecyclerView traineeRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainees_screen);

        //Recycler view
        traineeRecycler = findViewById(R.id.traineeRecycler);
        ArrayList<Trainee> trainees = new ArrayList<>();
        trainees.add(new Trainee("Sathish",25.0,"null"));
        trainees.add(new Trainee("Vishnu",25.0,"null"));
        TraineeAdapter traineeAdapter = new TraineeAdapter(this);
        traineeAdapter.setTrainee(trainees);
        traineeRecycler.setAdapter(traineeAdapter);
        traineeRecycler.setLayoutManager(new LinearLayoutManager(this));



    }

}