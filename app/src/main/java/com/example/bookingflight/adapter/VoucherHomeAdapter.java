package com.example.bookingflight.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.model.Voucher;

import java.util.List;

public class VoucherHomeAdapter extends RecyclerView.Adapter<VoucherHomeAdapter.ViewHolder>{
    private List<Voucher> vouchers;
    public VoucherHomeAdapter(List<Voucher> vouchers) {
        this.vouchers = vouchers;
    }
    @NonNull
    @Override
    public VoucherHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher_home, parent, false);
        return new VoucherHomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherHomeAdapter.ViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);
        holder.txtDiscount.setText(voucher.getDiscount());
        holder.txtCode.setText(voucher.getCode());
        holder.txtNgayHetHan.setText(voucher.getNgayHetHan());
        holder.txtTrangThai.setText(voucher.getTrangThai());
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDiscount, txtCode, txtNgayHetHan, txtTrangThai;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDiscount = itemView.findViewById(R.id.txtDiscount);
            txtCode = itemView.findViewById(R.id.txtCode);
            txtNgayHetHan = itemView.findViewById(R.id.txtNgayHetHan);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
        }
    }
}
