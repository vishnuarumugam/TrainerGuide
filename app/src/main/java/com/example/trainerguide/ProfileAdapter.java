package com.example.trainerguide;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{
    private List<String> items = new ArrayList<>();
    private Context context;

    public ProfileAdapter(List<String> items, Context context) {
        if(items == null)
        {
            this.items = new ArrayList<String>();
        }else{
        this.items = items;
        }
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_recycler_item,parent,false);

        ProfileAdapter.ViewHolder holder = new ProfileAdapter.ViewHolder(view);

        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        System.out.println("items"+items.get(position).toString());
        holder.item.setText(items.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.profileItem);
        }
    }
}
