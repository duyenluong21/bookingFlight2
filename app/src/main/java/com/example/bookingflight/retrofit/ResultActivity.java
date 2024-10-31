package com.example.bookingflight.retrofit;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.ResultAdapter;
import com.example.bookingflight.model.Result;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private ResultAdapter resultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Khởi tạo và thiết lập RecyclerView và ResultAdapter
       resultAdapter = new ResultAdapter(this ,new ArrayList<>());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(resultAdapter);

        Bundle bundleReceive = getIntent().getExtras();
        if (bundleReceive != null){
            List<Result> resultList = bundleReceive.getParcelableArrayList("object_results");
            Log.e("Tra ve ListResult ", resultList.toString());
            if (resultList != null && !resultList.isEmpty()){
                resultAdapter.setResultList(resultList);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(itemDecoration);
                recyclerView.setAdapter(resultAdapter);
            }else {
                // Xử lý khi không có kết quả
                Toast.makeText(ResultActivity.this, "Không có kết quả", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(resultAdapter != null){
            resultAdapter.release();
        }
    }
}