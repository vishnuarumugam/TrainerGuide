package com.example.trainerguide;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.User;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TraineeAdapter extends RecyclerView.Adapter<TraineeAdapter.ViewHolder> {

    private List<UserMetaData> trainee = new ArrayList<>();
    private Context context;
    private OnAddClickListener addlistener;

    /*public ArrayList<User> getTrainee() {
        return trainee;
    }

    public void setTrainee(ArrayList<User> trainee){
        this.trainee = trainee;
        notifyDataSetChanged();
    }*/

    public TraineeAdapter(Context context,List<UserMetaData> trainee){
        this.context=context;
        this.trainee=trainee;
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
    public void onBindViewHolder(@NonNull TraineeAdapter.ViewHolder holder, final int position) {
        int[] colours = new int[]{Color.parseColor("#F6CEE3"), Color.parseColor("#A9E2F3"), Color.parseColor("#D8F6CE"), Color.parseColor("#E4E3E3")};
        holder.traineeConsItem.setBackgroundColor(colours[position%3]);
        System.out.println("items Trainee");
        holder.name.setText(trainee.get(position).getName());
        holder.bmi.setText(trainee.get(position).getBmi().toString());
        Picasso.get().load(trainee.get(position).getImage())
                .placeholder(R.mipmap.profile)
                .fit()
                .centerCrop()
                .into(holder.profilePic);
        holder.traineeProfileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainee.get(position);
                System.out.println(position);
                addlistener.onAddclick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trainee.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,bmi;
        ImageView profilePic;
        MaterialCardView traineeProfileClick;
        RelativeLayout traineeItem;
        ConstraintLayout traineeConsItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.traineeItemName);
            bmi = itemView.findViewById(R.id.traineeItemBmi);
            profilePic = itemView.findViewById(R.id.traineeProfileImage);
            traineeProfileClick = itemView.findViewById(R.id.trainee_item_parent);
            traineeItem = itemView.findViewById(R.id.traineeItem);
            traineeConsItem = itemView.findViewById(R.id.traineeConsItem);

        }



    }

    public interface OnAddClickListener{
        void onAddclick(int position);
    }

    public void setOnAddClickListener(TraineeAdapter.OnAddClickListener listener){
        addlistener = listener;
    }
}
