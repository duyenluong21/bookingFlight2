package com.example.bookingflight.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookingflight.R;
import com.example.bookingflight.activity.AdDetailActivity;
import com.example.bookingflight.model.Ad;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private List<Ad> adList;
    private Context context;
    private OnAdClickListener onAdClickListener;

    public ImageSliderAdapter(List<Ad> adList, Context context) {
        this.adList = adList;
        this.context = context;
    }
    public ImageSliderAdapter(List<Ad> adList) {
        this.adList = adList;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Ad ad = adList.get(position);
        // Giả sử ad có một trường là imageUrl chứa URL hình ảnh
        Glide.with(holder.itemView.getContext())
                .load(ad.getImg())  // Sử dụng URL của hình ảnh từ đối tượng Ad
                .into(holder.imageView);  // Đưa hình ảnh vào ImageView
        // Thiết lập sự kiện nhấp vào slide
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdDetailActivity.class);
            intent.putExtra("maQC", ad.getMaqc()); // Gửi mã quảng cáo qua Intent
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }
    public interface OnAdClickListener {
        void onAdClick(Ad ad); // Phương thức để xử lý nhấp vào quảng cáo
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewSlider);
        }
    }

    public void updateImageList(List<Ad> newAdList) {
        this.adList = newAdList; // Cập nhật danh sách quảng cáo
        notifyDataSetChanged(); // Thông báo cho adapter cập nhật dữ liệu
    }
}
