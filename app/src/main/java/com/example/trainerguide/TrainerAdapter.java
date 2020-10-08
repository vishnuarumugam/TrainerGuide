package com.example.trainerguide;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Trainer;
import com.example.trainerguide.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrainerAdapter extends  RecyclerView.Adapter<TrainerAdapter.ViewHolder>{
    private List<Trainer> trainers = new ArrayList<>();
    private Context context;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(trainers.get(position).getName());
        holder.bmi.setText(trainers.get(position).getBmr().toString());
        Picasso.get().load(trainers.get(position).getImage())
                .fit()
                .centerCrop()
                .into(holder.profileImage);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainee_item,parent,false);
        TrainerAdapter.ViewHolder holder = new TrainerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    public TrainerAdapter(List<Trainer> trainers, Context context) {
        this.trainers = trainers;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, bmi;
        ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trainerItemName);
            bmi = itemView.findViewById(R.id.trainerItemBmi);
            profileImage = itemView.findViewById(R.id.trainerProfileImage);
        }
    }
}
