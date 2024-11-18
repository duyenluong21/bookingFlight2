package com.example.bookingflight.adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.model.Store;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {
    private List<Store> stores = new ArrayList<>();
    private List<String> suggest = new ArrayList<>();
    private Context context;
    private Location currentLocation;  // Biến lưu vị trí hiện tại của người dùng
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(double lat, double lng, String selectedSuggestion);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
        Log.d("StoreAdapter", "OnItemClickListener has been set.");
    }


    // Hàm để thiết lập listener cho sự kiện click

    public StoreAdapter(Context context, List<Store> stores) {
        this.context = context;
        this.stores = stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
        notifyDataSetChanged();
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        notifyDataSetChanged(); // Cập nhật lại giao diện khi vị trí hiện tại thay đổi
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = stores.get(position);
        holder.storeNameTextView.setText(store.getTenShop());
        holder.address.setText(store.getDiaChi());

        // Tính khoảng cách và hiển thị
        holder.distanceTextView.setText(String.format("Cách: %.2f km", store.getDistance()));

        holder.itemView.setOnClickListener(v -> {
            // Kiểm tra lại đối tượng store
            if (store != null) {
                // Lấy tọa độ vido và kinhdo
                String vido = store.getVido();
                String kinhdo = store.getKinhdo();

                // Kiểm tra null trước khi sử dụng
                if (vido != null && kinhdo != null) {
                    try {
                        // Chuyển đổi từ String sang double
                        double lat = Double.parseDouble(vido.trim());
                        double lng = Double.parseDouble(kinhdo.trim());
                        String selectedSuggestion = store.getTenShop();

                        // Gọi listener với lat, lng và selectedSuggestion
                        onItemClickListener.onItemClick(lat, lng, selectedSuggestion);
                    } catch (NumberFormatException e) {
                        Log.e("StoreAdapter", "Error parsing lat/lng: " + e.getMessage());
                    }
                } else {
                    Log.d("StoreAdapter", "Vido or Kinhdo is null for Store: " + store.getTenShop());
                }
            } else {
                Log.d("StoreAdapter", "Store is null at position: " + holder.getAdapterPosition());
            }
        });



    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    // ViewHolder cho từng item của RecyclerView
    class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView storeNameTextView;
        TextView distanceTextView, address;

        public StoreViewHolder(View itemView) {
            super(itemView);
            storeNameTextView = itemView.findViewById(R.id.storeName);
            address = itemView.findViewById(R.id.address);
            distanceTextView = itemView.findViewById(R.id.distance);
        }
    }
}
