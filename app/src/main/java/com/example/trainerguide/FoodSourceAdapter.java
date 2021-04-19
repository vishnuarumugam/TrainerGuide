package com.example.trainerguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainerguide.models.Food;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FoodSourceAdapter extends RecyclerView.Adapter<FoodSourceAdapter.ViewHolder> implements Filterable {
    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    private List<Food> foodItems = new ArrayList<>();
    private List<Food> searchFoodItems = new ArrayList<>(foodItems);
    private Context context;



    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Food> filteredList = new ArrayList<>();
            System.out.println("FilterResults");
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(foodItems);
            }else {
                for (Food searchFood : foodItems){
                    System.out.println("searchFood");

                    if (searchFood.getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        System.out.println("searchFoodcase");
                        filteredList.add(searchFood);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            searchFoodItems.clear();
            System.out.println("publishResults");
            searchFoodItems.addAll((Collection<? extends Food>) filterResults.values);

            for (Food fos : searchFoodItems){
                fos.getName();
                System.out.println("fos.getName()" + fos.getName());

            }

            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return filter;
    }

    private FoodSourceAdapter.OnAddClickListener addlistener;

    public FoodSourceAdapter(List<Food> trainers, Context context) {
        this.foodItems = trainers;
        this.searchFoodItems = new ArrayList<>(foodItems);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.itemName.setText(foodItems.get(position).getName().toUpperCase());
        holder.calorie.setText(String.valueOf(foodItems.get(position).getCalorieValue()));
        holder.qty.setText(String.valueOf(foodItems.get(position).getQuantity()));
        holder.qtyLabel.setText(", " + foodItems.get(position).getMeasurementUnit());

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_chart_item,parent,false);
        FoodSourceAdapter.ViewHolder holder = new FoodSourceAdapter.ViewHolder(view);
        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, calorie, qty, qtyLabel;
        ImageButton addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            calorie = itemView.findViewById(R.id.calorie);
            qty = itemView.findViewById(R.id.qty);
            addBtn = itemView.findViewById(R.id.addfoodItemBtn);
            qtyLabel = itemView.findViewById(R.id.qtyLabel);
        }
    }

    public interface OnAddClickListener{
        void onAddclick(int position);
    }
    public void setOnAddClickListener(FoodSourceAdapter.OnAddClickListener listener){
        addlistener = listener;
    }
}
