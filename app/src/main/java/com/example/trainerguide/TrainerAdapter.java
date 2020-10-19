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
        System.out.println("items Trainer");

        holder.name.setText(trainers.get(position).getName());
        holder.experiance.setText(trainers.get(position).getExperience().toString());
        holder.fees.setText(trainers.get(position).getFees().toString());
        Picasso.get().load(trainers.get(position).getImage())
                .fit()
                .placeholder(R.drawable.ic_share)
                .centerCrop()
                .into(holder.profileImage);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trainer_item,parent,false);
        TrainerAdapter.ViewHolder holder = new TrainerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    public void filterList(ArrayList<Trainer> filteredList){
        trainers = filteredList;
        notifyDataSetChanged();
    }

    public TrainerAdapter(List<Trainer> trainers, Context context) {
        this.trainers = trainers;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, experiance, fees;
        ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trainerItemName);
            experiance = itemView.findViewById(R.id.trainerItemExp);
            fees = itemView.findViewById(R.id.trainerItemFee);
            profileImage = itemView.findViewById(R.id.trainerProfileImage);
        }
    }
}
