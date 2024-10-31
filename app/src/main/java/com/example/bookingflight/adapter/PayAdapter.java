package com.example.bookingflight.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.model.Mess;
import com.example.bookingflight.model.Pay;

import java.util.List;

public class PayAdapter extends RecyclerView.Adapter<PayAdapter.ViewHolder> {
    private List<Pay> mListPay;
    // Constructor để khởi tạo danh sách
    public PayAdapter(List<Pay> listPay) {
        this.mListPay = listPay; // Khởi tạo với danh sách rỗng nếu null
    }
    @NonNull
    @Override
    public PayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thanhtoan, parent, false);
        return new PayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayAdapter.ViewHolder holder, int position) {
        Pay pay = mListPay.get(position);
        holder.txtNgaytt.setText(pay.getNgayThanhToan());
        holder.txtGiott.setText(pay.getGioThanhToan());
        holder.txtTientt.setText(pay.getTongThanhToan() + " VND");
    }

    @Override
    public int getItemCount() {
        return mListPay.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNgaytt, txtGiott, txtTientt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNgaytt = itemView.findViewById(R.id.txtNgaytt);
            txtGiott = itemView.findViewById(R.id.txtGiott);
            txtTientt = itemView.findViewById(R.id.txtTientt);
        }
    }
}
