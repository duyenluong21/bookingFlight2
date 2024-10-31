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

public class DomesticAdapter extends RecyclerView.Adapter<DomesticAdapter.ViewHolder> {

    private List<Location> locationList;
    private Context context;

    public DomesticAdapter(List<Location> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageTravel;
        TextView textTravel;
        RelativeLayout locationLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTravel = itemView.findViewById(R.id.imageTravel);
            textTravel = itemView.findViewById(R.id.textTravel);
            locationLayout = itemView.findViewById(R.id.Location);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_domestic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Location location = locationList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(location.getImageUrl())
                .into(holder.imageTravel);
        holder.textTravel.setText(location.getName());
        holder.locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                switch (location.getName()) {
                    case "Hà Nội":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/ha-noi";
                        break;
                    case "Hải Phòng":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/hai-phong";
                        break;
                    case "Quảng Ninh":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/quang-ninh";
                        break;
                    case "Ninh Bình":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/ninh-binh";
                        break;
                    case "Thành phố Huế":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/hue";
                        break;
                    case "Đà Nẵng":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/da-nang";
                        break;
                    case "Thành phố Hồ Chí Minh":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/thanh-pho-ho-chi-minh";
                        break;
                    case "Cần Thơ":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/can-tho";
                        break;
                    case "Phú Quốc":
                        url = "https://www.vietnamairlines.com/vn/vi/destinations/phu-quoc";
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
