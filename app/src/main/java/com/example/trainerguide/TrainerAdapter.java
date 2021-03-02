package com.example.trainerguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Trainer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrainerAdapter extends  RecyclerView.Adapter<TrainerAdapter.ViewHolder>{
    private List<Trainer> trainers = new ArrayList<>();
    private Context context;
    private OnAddClickListener addlistener;


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name.setText(trainers.get(position).getName());

        if(trainers.get(position).getExperience() != null){
        holder.experiance.setText(trainers.get(position).getExperience().toString());}

        if(trainers.get(position).getFees() != null){
        holder.fees.setText(trainers.get(position).getFees().toString());}

        Picasso.get().load(trainers.get(position).getImage())
                .fit()
                .placeholder(R.drawable.ic_share)
                .centerCrop()
                .into(holder.profileImage);
        holder.addTrainee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainers.get(position);
                System.out.println(position);
                addlistener.onAddclick(position);
            }
        });
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
        ImageButton addTrainee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trainerItemName);
            experiance = itemView.findViewById(R.id.trainerItemExp);
            fees = itemView.findViewById(R.id.trainerItemFee);
            profileImage = itemView.findViewById(R.id.trainerProfileImage);
            addTrainee = itemView.findViewById(R.id.traineeAdd);
        }
    }

    public interface OnAddClickListener{
        void onAddclick(int position);
    }

    public void setOnAddClickListener(OnAddClickListener listener){
        addlistener = listener;
    }
}
