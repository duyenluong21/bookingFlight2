package com.example.bookingflight.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookingflight.R;
import com.example.bookingflight.model.Location;

import java.util.List;

public class TravelAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Location> locationList;

    public TravelAdapter(List<Location> locationList) {
        this.locationList = locationList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView  imageTravel;
        TextView textTravel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTravel = itemView.findViewById(R.id.imageTravel);
            textTravel = itemView.findViewById(R.id.textTravel);
        }
    }

    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel, parent, false);
        return new LocationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, int position) {
        Location location = locationList.get(position);
        // Gán dữ liệu cho mỗi item_travel
        Glide.with(holder.itemView.getContext())
                .load(location.getImageUrl())
                .into(holder.imageTravel);
        holder.textTravel.setText(location.getName());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

}

