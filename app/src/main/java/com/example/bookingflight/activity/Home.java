package com.example.bookingflight.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.DomesticAdapter;
import com.example.bookingflight.adapter.ImageSliderAdapter;
import com.example.bookingflight.adapter.InternationAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Ad;
import com.example.bookingflight.model.Location;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends BaseActivity {
    BottomNavigationView bottomNavigationView;
    CardView locationCard, hanhliCard, mapCard, khuyenmaiCard;
    private SessionManager sessionManager;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ImageSliderAdapter adapter;
    private List<Ad> adList = new ArrayList<>();
    private static final String PREFS_NAME = "AdPrefs";
    private static final String KEY_AD_SHOWN = "ad_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        locationCard = findViewById(R.id.locationCard);
        hanhliCard = findViewById(R.id.hanhliCard);
        mapCard= findViewById(R.id.mapCard);
        khuyenmaiCard = findViewById(R.id.khuyenmaiCard);
        ViewPager2 viewPager2 = findViewById(R.id.imageView);
        List<Ad> adList = new ArrayList<>();

        adapter = new ImageSliderAdapter(adList, this);
        viewPager2.setAdapter(adapter);

        fetchAdData();

        handler.postDelayed(runnable = () -> {
            int nextItem = viewPager2.getCurrentItem() + 1;
            if (nextItem >= adapter.getItemCount()) {
                nextItem = 0;
            }
            viewPager2.setCurrentItem(nextItem, true);
            handler.postDelayed(runnable, 3000);
        }, 3000);
        sessionManager = new SessionManager(getApplicationContext());
        hanhliCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , LocationActivity.class) ;
                startActivity(intent);
                finish();
            }
        });
        mapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , MapActivity.class) ;
                startActivity(intent);
                finish();
            }
        });

        khuyenmaiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , VoucherHomeActivity.class) ;
                startActivity(intent);
                finish();
            }
        });
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.ticket) {
                    Intent myintent = new Intent(Home.this, YourSearchActivity.class);
                    startActivity(myintent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.home) {
                    return true;
                } else if (itemId == R.id.club) {
                    Intent myIntent1 = new Intent(Home.this, ChatStaffActivity.class);
                    startActivity(myIntent1);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.profile) {
                    Intent myintent2 = new Intent(Home.this, LoginProfile.class);
                    startActivity(myintent2);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        RecyclerView recyclerView2 = findViewById(R.id.recycler_featured);
        RecyclerView recyclerView = findViewById(R.id.recycler_popular);

        List<Location> locationList = new ArrayList<>();
        List<Location> locationList2 = new ArrayList<>();

        locationList.add(new Location("Bắc Kinh", R.drawable.backinh));
        locationList.add(new Location("Seoul", R.drawable.seoul));
        locationList.add(new Location("Tokyo", R.drawable.tokyo));
        locationList.add(new Location("Osaka", R.drawable.osaka));
        InternationAdapter internationAdapter = new InternationAdapter(locationList, Home.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(internationAdapter);
        locationList2.add(new Location("Hà Nội", R.drawable.hanoi));
        locationList2.add(new Location("Hải Phòng", R.drawable.haiphong));
        locationList2.add(new Location("Quảng Ninh", R.drawable.quangninh1));
        locationList2.add(new Location("Ninh Bình", R.drawable.ninhbinh));
        locationList2.add(new Location("Thành phố Huế", R.drawable.hue));
        locationList2.add(new Location("Đà Nẵng", R.drawable.danang1));
        locationList2.add(new Location("Thành phố Hồ Chí Minh", R.drawable.tphochiminh));
        locationList2.add(new Location("Cần Thơ", R.drawable.cantho));
        locationList2.add(new Location("Phú Quốc", R.drawable.phuquoc));

        DomesticAdapter domesticAdapter = new DomesticAdapter(locationList2, Home.this);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setAdapter(domesticAdapter);
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_luggage, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        dialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void fetchAdData() {
        ApiService.searchFlight.getAd().enqueue(new Callback<ApiResponse<List<Ad>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ad>>> call, Response<ApiResponse<List<Ad>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Ad> adList = response.body().getData();
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    boolean isAdShown = sharedPreferences.getBoolean(KEY_AD_SHOWN, false);
                    Log.d("AdShown", "Ad shown value: " + isAdShown);
                    if (!isAdShown && !adList.isEmpty()) {
                        showAdModal(adList);
                    }
                    runOnUiThread(() -> {
                        adapter.updateImageList(adList);

                    });
                } else {
                    Log.e("API Error", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ad>>> call, Throwable t) {
                Log.e("API Error", "Failed to fetch ad data", t);
            }
        });

    }
    private void showAdModal(List<Ad> adList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ad_dialog, null);
        builder.setView(dialogView);

        AlertDialog adDialog = builder.create();

        ImageView imgAd = dialogView.findViewById(R.id.imgAd);
        Button btnViewDetails = dialogView.findViewById(R.id.btnViewDetails);
        ImageButton btnClose = dialogView.findViewById(R.id.btnClose);
        int randomIndex = (int) (Math.random() * adList.size());
        Ad randomAd = adList.get(randomIndex);
        loadImage(randomAd.getImg(), imgAd);
        btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this , YourSearchActivity.class);
            startActivity(intent);
            finish();
        });

        btnClose.setOnClickListener(v -> {
            adDialog.dismiss();
            saveAdShownState();
        });

        adDialog.setOnCancelListener(dialogInterface -> saveAdShownState());
        adDialog.show();
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        Picasso.get().load(imageUrl).into(imageView);
    }

    private void saveAdShownState() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_AD_SHOWN, true);
        editor.apply();
    }


}