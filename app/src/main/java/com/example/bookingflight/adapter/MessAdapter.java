package com.example.bookingflight.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.model.Mess;

import java.util.List;

public class MessAdapter extends RecyclerView.Adapter<MessAdapter.MessViewHolder> {

    private List<Mess> mListMess;

    public void setData(List<Mess> list) {
        this.mListMess = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mess, parent, false);
        return new MessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessViewHolder holder, int position) {
        Mess mess = mListMess.get(position);
        if (mess == null) {
            return;
        }

        if (mess.getNoiDung2() != null && !mess.getNoiDung2().isEmpty()) {
            holder.tvMess2.setText(mess.getNoiDung2());
            holder.tvMess2.setVisibility(View.VISIBLE);
            holder.tvMess.setVisibility(View.GONE);
        } else if (mess.getNoiDung1() != null && !mess.getNoiDung1().isEmpty()) {
            holder.tvMess.setText(mess.getNoiDung1());
            holder.tvMess.setVisibility(View.VISIBLE);
            holder.tvMess2.setVisibility(View.GONE);
        } else {
            holder.tvMess.setVisibility(View.GONE);
            holder.tvMess2.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        if (mListMess != null) {
            return mListMess.size();
        }
        return 0;
    }

    public class MessViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMess;
        private TextView tvMess2;

        public MessViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMess = itemView.findViewById(R.id.tvMess);
            tvMess2 = itemView.findViewById(R.id.tvMess2);
        }
    }
}
