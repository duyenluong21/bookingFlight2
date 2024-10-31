package com.example.bookingflight.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.bookingflight.R;
import com.example.bookingflight.model.User;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    ImageView backButton;
    Button btnBooking ;
    private List<User> mListUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListUser = new ArrayList<>();
        setContentView(R.layout.activity_profile);
        backButton = findViewById(R.id.backButton);
        btnBooking = findViewById(R.id.btnBooking);
        EditText txHovaTen = findViewById(R.id.txHovaTen);
        EditText txNgaySinh = findViewById(R.id.txNgaySinh);
        EditText txGioitinh = findViewById(R.id.txGioitinh);
        EditText txDiaChi = findViewById(R.id.txDiaChi);
        EditText txEmail = findViewById(R.id.txEmail);
        EditText txSoDienThoai = findViewById(R.id.txSoDienThoai);
        User user = getIntent().getParcelableExtra("object_user");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this ,LoginProfile.class ) ;
                startActivity(intent);
                finish();
            }
        });
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Editinfo.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        });

        if(user != null){
            txHovaTen.setText(user.getFullname());
            txNgaySinh.setText(user.getNgaysinh());
            txGioitinh.setText(user.getGioiTinh());
            txDiaChi.setText(user.getDiaChi());
            txEmail.setText(user.getEmail());
            txSoDienThoai.setText(user.getSoDT());
        }
    }
}