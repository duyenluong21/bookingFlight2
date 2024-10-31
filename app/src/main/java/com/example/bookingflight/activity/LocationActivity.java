package com.example.bookingflight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.InternationAdapter;
import com.example.bookingflight.adapter.DomesticAdapter;
import com.example.bookingflight.model.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationActivity extends AppCompatActivity {

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placefamous);
        backButton = findViewById(R.id.backButton);

        // Setting up the back button functionality
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationActivity.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        // Initializing RecyclerViews
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);

        List<Location> locationList = new ArrayList<>();
        List<Location> locationList2 = new ArrayList<>();


        // Adding locations for the location adapter
        locationList.add(new Location("Bắc Kinh", R.drawable.backinh));
        locationList.add(new Location("Seoul", R.drawable.seoul));
        locationList.add(new Location("Tokyo", R.drawable.tokyo));
        locationList.add(new Location("Osaka", R.drawable.osaka));
        // Add more locations as needed

        // Setting up LocationAdapter
        // Setting up LocationAdapter
        InternationAdapter internationAdapter = new InternationAdapter(locationList, LocationActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(internationAdapter);

        // Adding locations for the travel adapter
        locationList2.add(new Location("Hà Nội", R.drawable.hanoi));
        locationList2.add(new Location("Hải Phòng", R.drawable.haiphong));
        locationList2.add(new Location("Quảng Ninh", R.drawable.quangninh1));
        locationList2.add(new Location("Ninh Bình", R.drawable.ninhbinh));
        locationList2.add(new Location("Thành phố Huế", R.drawable.hue));
        locationList2.add(new Location("Đà Nẵng", R.drawable.danang1));
        locationList2.add(new Location("Thành phố Hồ Chí Minh", R.drawable.tphochiminh));
        locationList2.add(new Location("Cần Thơ", R.drawable.cantho));
        locationList2.add(new Location("Phú Quốc", R.drawable.phuquoc));


        // Setting up TravelAdapter
        DomesticAdapter domesticAdapter = new DomesticAdapter(locationList2, LocationActivity.this);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(domesticAdapter);
    }
}
