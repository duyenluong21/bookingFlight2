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

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private List<Voucher> vouchers;
    private OnVoucherSelectedListener onVoucherSelectedListener;
    private int selectedPosition = -1;

    public VoucherAdapter(List<Voucher> vouchers, OnVoucherSelectedListener onVoucherSelectedListener) {
        this.vouchers = vouchers;
        this.onVoucherSelectedListener = onVoucherSelectedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);
        holder.txtDiscount.setText(voucher.getDiscount());
        holder.txtCode.setText(voucher.getCode());
        holder.txtNgayHetHan.setText(voucher.getNgayHetHan());
        holder.txtTrangThai.setText(voucher.getTrangThai());

        // Set RadioButton checked state
        holder.radioButton.setChecked(position == selectedPosition);

        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            onVoucherSelectedListener.onVoucherSelected(voucher);
        });
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    public interface OnVoucherSelectedListener {
        void onVoucherSelected(Voucher voucher);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtDiscount, txtCode, txtNgayHetHan, txtTrangThai;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDiscount = itemView.findViewById(R.id.txtDiscount);
            txtCode = itemView.findViewById(R.id.txtCode);
            txtNgayHetHan = itemView.findViewById(R.id.txtNgayHetHan);
            txtTrangThai = itemView.findViewById(R.id.txtTrangThai);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
}
