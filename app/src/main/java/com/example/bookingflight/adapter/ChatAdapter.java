package com.example.bookingflight.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.model.Staff;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.UserViewHolder>{
    private List<Staff> staffList;
    private OnUserClickListener onUserClickListener;

    public interface OnUserClickListener {
        void onStaffClick(Staff staff, String maNV, String tenNV);
    }

    public ChatAdapter(List<Staff> staffList) {
        this.staffList = staffList;
    }

    public ChatAdapter(List<Staff> staffList, OnUserClickListener onUserClickListener) {
        this.staffList = staffList;
        this.onUserClickListener = onUserClickListener;
    }
    @NonNull
    @Override
    public ChatAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_person_chat, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.UserViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        String hoVaTen = (staff.getHoNV() != null ? staff.getHoNV() : "") + " " + (staff.getTenNV() != null ? staff.getTenNV() : "");
        holder.textViewUserName.setText(hoVaTen);
        holder.textViewLastMessage.setText(staff.getKinhNghiem());

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onStaffClick(staff, staff.getMaNV(), staff.getTenNV()); // Truyền maNV vào đây
            }
        });
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName;
        TextView textViewLastMessage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
        }
    }
}
