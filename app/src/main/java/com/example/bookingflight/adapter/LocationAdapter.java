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
import com.example.bookingflight.activity.LocationActivity;
import com.example.bookingflight.model.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<Location> locationList;
    private int viewType;

    public LocationAdapter(List<Location> locationList) {
        this.locationList = locationList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageLocation, imageTravel;
        TextView textLocation, textTravel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLocation = itemView.findViewById(R.id.imageLocation);
            textLocation = itemView.findViewById(R.id.textLocation);
            imageTravel = itemView.findViewById(R.id.imageTravel);
            textTravel = itemView.findViewById(R.id.textTravel);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locationList.get(position);

        // Gán dữ liệu cho mỗi item_location
        Glide.with(holder.itemView.getContext())
                .load(location.getImageUrl())
                .into(holder.imageLocation);
        holder.textLocation.setText(location.getName());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

}
