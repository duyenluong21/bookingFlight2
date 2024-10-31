package com.example.bookingflight.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookingflight.R;
import com.example.bookingflight.activity.WebContentActivity;
import com.example.bookingflight.model.Location;

import java.util.List;

public class InternationAdapter extends RecyclerView.Adapter<InternationAdapter.ViewHolder> {

    private List<Location> locationList;
    private Context context;

    public InternationAdapter(List<Location> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageLocation;
        TextView textLocation;
        RelativeLayout locationLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageLocation = itemView.findViewById(R.id.imageLocation);
            textLocation = itemView.findViewById(R.id.textLocation);
            locationLayout = itemView.findViewById(R.id.Location); // Đây là RelativeLayout
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_internation, parent, false);
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

        // Thêm sự kiện nhấp chuột vào RelativeLayout thay vì toàn bộ itemView
        holder.locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                switch (location.getName()) {
                    case "Bắc Kinh":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/Beijing";
                        break;
                    case "Seoul":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/Seoul";
                        break;
                    case "Tokyo":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/Tokyo";
                        break;
                    case "Osaka":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/Osaka";
                        break;
                }

                Intent intent = new Intent(context, WebContentActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
