package com.example.trainerguide;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TraineeAdapter extends RecyclerView.Adapter<TraineeAdapter.ViewHolder> {

    private ArrayList<Trainee> trainee = new ArrayList<>();
    private Context context;


    public ArrayList<Trainee> getTrainee() {
        return trainee;
    }

    public void setTrainee(ArrayList<Trainee> trainee){
        this.trainee = trainee;
        notifyDataSetChanged();
    }

    public TraineeAdapter(Context context){
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainee_item,parent,false);

        ViewHolder holder = new ViewHolder(view);


        return holder;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull TraineeAdapter.ViewHolder holder, int position) {

        holder.name.setText(trainee.get(position).getName());
        holder.bmr.setText(trainee.get(position).getBmr().toString());

    }

    @Override
    public int getItemCount() {
        return trainee.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,bmr;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.traineeItemName);
            bmr = itemView.findViewById(R.id.traineeItemBmr);

        }



    }
}
