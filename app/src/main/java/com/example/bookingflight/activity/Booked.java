package com.example.bookingflight.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.bookingflight.R;
import com.example.bookingflight.adapter.MyViewPager;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.fragment.Ticket;
import com.example.bookingflight.fragment.Ticketed;
import com.google.android.material.tabs.TabLayout;

public class Booked extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyViewPager mMyViewPager;

    private ApiService searchFlight;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked);
        backButton = findViewById(R.id.backButton);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        mMyViewPager = new MyViewPager(getSupportFragmentManager());
        mViewPager.setAdapter(mMyViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        searchFlight = ApiService.searchFlight;

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi nút back được nhấn
                finish();
            }
        });
    }
}
