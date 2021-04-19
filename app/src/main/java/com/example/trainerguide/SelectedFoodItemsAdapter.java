package com.example.trainerguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Food;

import java.util.ArrayList;
import java.util.List;

public class SelectedFoodItemsAdapter extends RecyclerView.Adapter<SelectedFoodItemsAdapter.ViewHolder>{


    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    private SelectedFoodItemsAdapter.OnAddItemClickListener addlistener;
    private SelectedFoodItemsAdapter.OnRemoveClickListener removelistener;

    private List<Food> foodItems = new ArrayList<>();
    private Context context;

    public SelectedFoodItemsAdapter(List<Food> foodlst, Context context) {
        this.foodItems = foodlst;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedFoodItemsAdapter.ViewHolder holder, int position) {
        holder.itemName.setText(foodItems.get(position).getName().toUpperCase());
        holder.qty.setText(String.valueOf(foodItems.get(position).value));
        holder.calories.setText(String.valueOf(foodItems.get(position).totalCalorie));
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodItems.get(position);
                System.out.println(position);
                addlistener.onAddItemclick(position);
            }
        });
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodItems.get(position);
                System.out.println(position);
                removelistener.onRemoveItemclick(position);
            }
        });
    }

    @NonNull
    @Override
    public SelectedFoodItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_fooditem,parent,false);
        SelectedFoodItemsAdapter.ViewHolder holder = new SelectedFoodItemsAdapter.ViewHolder(view);
        return holder;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, qty, calories;
        ImageButton addBtn, removeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.selecteditemName);
            qty = itemView.findViewById(R.id.qtyValue);
            calories = itemView.findViewById(R.id.selectedqty);
            addBtn = itemView.findViewById(R.id.addSelectedQty);
            removeBtn = itemView.findViewById(R.id.removeSelectedQty);
        }
    }
    public interface OnAddItemClickListener{
        void onAddItemclick(int position);
    }
    public void setOnAddClickListener(SelectedFoodItemsAdapter.OnAddItemClickListener listener){
        addlistener = listener;
    }

    public interface OnRemoveClickListener{
        void onRemoveItemclick(int position);
    }
    public void setOnRemoveClickListener(SelectedFoodItemsAdapter.OnRemoveClickListener listener){
        removelistener = listener;
    }

}
