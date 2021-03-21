package com.example.trainerguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Food;
import com.example.trainerguide.models.Trainer;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class FoodSourceAdapter extends RecyclerView.Adapter<FoodSourceAdapter.ViewHolder>{
    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    private List<Food> foodItems = new ArrayList<>();
    private Context context;
    private FoodSourceAdapter.OnAddClickListener addlistener;

    public FoodSourceAdapter(List<Food> trainers, Context context) {
        this.foodItems = trainers;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.itemName.setText(foodItems.get(position).getName());
        holder.qty.setText(String.valueOf(foodItems.get(position).getQuantity()));

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodItems.get(position);
                System.out.println(position);
                addlistener.onAddclick(position);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fooditem,parent,false);
        FoodSourceAdapter.ViewHolder holder = new FoodSourceAdapter.ViewHolder(view);
        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, qty;
        ImageButton addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            qty = itemView.findViewById(R.id.qty);
            addBtn = itemView.findViewById(R.id.addfoodItemBtn);
        }
    }

    public interface OnAddClickListener{
        void onAddclick(int position);
    }
    public void setOnAddClickListener(FoodSourceAdapter.OnAddClickListener listener){
        addlistener = listener;
    }
}
