package com.example.bookingflight.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.activity.ApiResponse;
import com.example.bookingflight.adapter.TicketAdapter;
import com.example.bookingflight.inteface.ApiService;

import com.example.bookingflight.model.BookTicket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ticketed extends Fragment {
    private RecyclerView recyclerViewFlight;
    private ApiService searchFlight;
    private List<BookTicket> ticketsBooked = new ArrayList<>();
    private TicketAdapter ticketBookedAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_ticketed, container, false);

        recyclerViewFlight = view.findViewById(R.id.recyclerViewBookedTicketed);
        ticketBookedAdapter = new TicketAdapter(ticketsBooked);
        recyclerViewFlight.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFlight.setAdapter(ticketBookedAdapter);

        searchFlight = ApiService.searchFlight;

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("user_maKH")) {
            String usermaKH = intent.getStringExtra("user_maKH");
            getUserBookedTickets(usermaKH);
        }

        return view;
    }

    private void getUserBookedTickets(String usermaKH) {
        Map<String, String> options = new HashMap<>();
        searchFlight.getBookedTickets().enqueue(new Callback<ApiResponse<List<BookTicket>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BookTicket>>> call, Response<ApiResponse<List<BookTicket>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookTicket> notBookedTickets = response.body().getData();
                    List<BookTicket> userBookedTickets = ApiService.filterBookedTicketsByMaKH(notBookedTickets, usermaKH);

                    ticketsBooked.clear();
                    for (BookTicket ticket : userBookedTickets) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date ticketDate = null;
                        try {
                            ticketDate = dateFormat.parse(ticket.getNgayDi());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (ticketDate != null) {
                            Date currentDate = new Date();
                            if (ticketDate.before(currentDate)) {
                                ticketsBooked.add(ticket);
                            }
                        }
                    }

                    // Thay đổi dữ liệu trong adapter và thông báo cập nhật
                    ticketBookedAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Failed to retrieve booked tickets data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BookTicket>>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}