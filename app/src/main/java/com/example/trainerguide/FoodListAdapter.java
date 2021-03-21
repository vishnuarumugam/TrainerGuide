package com.example.trainerguide;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Food;
import com.example.trainerguide.models.UserMetaData;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {


    private List<Food> foodList = new ArrayList<>();
    private Context context;

    public FoodListAdapter(List<Food> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        FoodListAdapter.ViewHolder holder = new FoodListAdapter.ViewHolder(view);
        return holder;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.ViewHolder holder, int position) {

        holder.foodName.setText(foodList.get(position).getName());
        holder.foodNutritionType.setText(foodList.get(position).getNutritionType());
        holder.foodCalorie.setText(foodList.get(position).getCalorieValue().toString());


    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView foodName, foodNutritionType, foodCalorie;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.foodItemName);
            foodNutritionType = itemView.findViewById(R.id.foodItemNt);
            foodCalorie = itemView.findViewById(R.id.foodItemCalorie);


        }
    }
}
