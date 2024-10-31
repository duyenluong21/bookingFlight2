package com.example.bookingflight.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.model.BookTicket;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {
    private List<BookTicket> bookedTickets;
    public TicketAdapter(List<BookTicket> bookedTickets) {
        this.bookedTickets = bookedTickets;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vedadat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookTicket bookedTicket = bookedTickets.get(position);
        holder.placeNameTextView.setText("Mã chuyến bay: " + bookedTicket.getMaCB());
        holder.descriptionTextView.setText("Mã khách hàng: " + bookedTicket.getMaKH());
        holder.descriptionTextView2.setText("Đi từ: " + bookedTicket.getDiaDiemDi() + " - " + "Đến: " + bookedTicket.getDiaDiemDen());
        holder.giaVeTextView.setText("Hạng vé: " + bookedTicket.getHangVe());
    }

    @Override
    public int getItemCount() {
        return bookedTickets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTextView , descriptionTextView , descriptionTextView2, giaVeTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.placeNameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            descriptionTextView2 = itemView.findViewById(R.id.descriptionTextView2);
            giaVeTextView = itemView.findViewById(R.id.giaVeTextView);
        }
    }
}
