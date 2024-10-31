package com.example.bookingflight.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bookingflight.R;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Airport;
import com.example.bookingflight.model.Result;
import com.example.bookingflight.retrofit.ResultActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class YourSearchActivity extends AppCompatActivity {
    private List<Result> mListResult ;
    Spinner fromEditText ;
    Spinner toEditText ;
    Button searchButton ;
    private Result mResult ;
    EditText fromTimeEditText,toTimeEditText ;
    DatePickerDialog datePickerDialog1,datePickerDialog2;
    BottomNavigationView bottomNavigationView;

    private ArrayAdapter<String> fromAdapter, toAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_search);
        searchButton = findViewById(R.id.searchButton);
        fromEditText = findViewById(R.id.fromEditText);
        toEditText = findViewById(R.id.toEditText);
        fromTimeEditText = (EditText) findViewById(R.id.fromTimeEditText);
        toTimeEditText = (EditText) findViewById(R.id.toTimeEditText);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.ticket);
        // Khởi tạo Adapter cho Spinner điểm xuất phát
        fromAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromEditText.setAdapter(fromAdapter);
        // Khởi tạo Adapter cho Spinner điểm đến
        toAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toEditText.setAdapter(toAdapter);
        mListResult = new ArrayList<>();
        searchPlace();
        fromTimeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showFromDatePickerDialog();
                }
            }
        });
        toTimeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showToDatePickerDialog();
                }
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.ticket) {
                    return true;
                } else if (itemId == R.id.home) {
                    Intent myintent = new Intent(YourSearchActivity.this, Home.class);
                    startActivity(myintent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.club) {
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    String maKH = sessionManager.getMaKH();
                    Intent myintent1 = new Intent(YourSearchActivity.this, chatActivity.class);
                    myintent1.putExtra("maKH", maKH); // Truyền maKH qua Intent
                    startActivity(myintent1);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.profile) {
                    Intent myintent2 = new Intent(YourSearchActivity.this, LoginProfile.class);
                    startActivity(myintent2);
                    overridePendingTransition(0, 0);
                    return true;
                }
                 return  true;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSearch();
            }
        });
    }

    private void showFromDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // date picker dialog
        datePickerDialog1 = new DatePickerDialog(YourSearchActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String formattedDay = String.format("%02d", dayOfMonth);
                        String formattedMonth = String.format("%02d", month + 1);
                        fromTimeEditText.setText(year + "-"
                                + (formattedMonth) + "-" + formattedDay);
                    }
                }, year, month, day);
        datePickerDialog1.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog1.show();
    }

    private void showToDatePickerDialog() {
        final Calendar c1 = Calendar.getInstance();
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog2 = new DatePickerDialog(YourSearchActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String formattedDay1 = String.format("%02d", dayOfMonth);
                        String formattedMonth1 = String.format("%02d", monthOfYear + 1);

                        // set day of month, month, and year value in the edit text
                        toTimeEditText.setText(year + "-"
                                + (formattedMonth1) + "-" + formattedDay1);
                    }
                }, year1, month1, day1);

        datePickerDialog2.getDatePicker().setMinDate(c1.getTimeInMillis());
        datePickerDialog2.show();
    }


    private void clickSearch() {
        String diaDiemDen = toEditText.getSelectedItem().toString(); // Cần thay thế này
        String diaDiemDi = fromEditText.getSelectedItem().toString(); // Cần thay thế này
        String ngayDen = toTimeEditText.getText().toString().trim();
        String ngayDi = fromTimeEditText.getText().toString().trim();

        if(mListResult == null || mListResult.isEmpty()){
            return;
        }
        List<Result> matchingResults = new ArrayList<>();
        Date currentTime = Calendar.getInstance().getTime();
        boolean isHasFlight = false;

        for (Result result:mListResult){
            if (diaDiemDen.equals(result.getDiaDiemDen())
                    && ngayDi.equals(result.getNgayDi())
                    && ngayDen.equals(result.getNgayDen())
                    && diaDiemDi.equals(result.getDiaDiemDi())){
                isHasFlight = true ;
                if (result.getFlightTime() != null && result.getFlightTime().after(currentTime)) {
                    matchingResults.add(result);
                }

            }
        }

        if (!matchingResults.isEmpty()){
            // di vao result
//            Result[] matchingResultsArray = matchingResults.toArray(new Result[0]);
            Intent intent = new Intent(YourSearchActivity.this, ResultActivity.class);
            intent.putParcelableArrayListExtra("object_results", (ArrayList<? extends Parcelable>) new ArrayList<>(matchingResults));
            startActivity(intent);
        }else {
            Toast.makeText(YourSearchActivity.this, "Không có chuyến bay tương ứng ", Toast.LENGTH_SHORT).show();
        }

    }



    private void searchPlace(){
        Map<String, String> options = new HashMap<>();

        ApiService.searchFlight.searchPlace(options)
                .enqueue(new Callback<ApiResponse<List<Result>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Result>>> call, Response<ApiResponse<List<Result>>> response) {
                        ApiResponse<List<Result>> apiResponse = response.body();

                        if (apiResponse != null && apiResponse.getData() != null) {
                            mListResult = apiResponse.getData();
                            // Lấy danh sách điểm đi và điểm đến từ mListResult
                            List<String> fromList = new ArrayList<>();
                            List<String> toList = new ArrayList<>();
                            for (Result result : mListResult) {
                                if (!fromList.contains(result.getDiaDiemDi())) {
                                    fromList.add(result.getDiaDiemDi());
                                }

                                if (!toList.contains(result.getDiaDiemDen())) {
                                    toList.add(result.getDiaDiemDen());
                                }
                            }

                            // Đặt dữ liệu vào Adapter và cập nhật Spinner
                            fromAdapter.clear();
                            fromAdapter.addAll(fromList);
                            fromAdapter.notifyDataSetChanged();

                            toAdapter.clear();
                            toAdapter.addAll(toList);
                            toAdapter.notifyDataSetChanged();
                            // Lấy danh sách sân bay và cập nhật Spinner
                            updateAirportSpinners();




                            // Thêm đoạn mã chuyển đổi thời gian bay vào đây
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            for (Result result : mListResult) {
                                try {
                                    Date flightTime = dateFormat.parse(result.getNgayDi() + " " + result.getGioBay());
                                    result.setFlightTime(flightTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Result>>> call, Throwable t) {
                        Log.e("YourSearchActivity", "Lỗi khi thực hiện cuộc gọi API", t);
                        Toast.makeText(YourSearchActivity.this, "Call Api error ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAirportSpinners() {
        ApiService.searchFlight.getAirport()
                .enqueue(new Callback<ApiResponse<List<Airport>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Airport>>> call, Response<ApiResponse<List<Airport>>> response) {
                        ApiResponse<List<Airport>> apiResponse = response.body();

                        if (apiResponse != null && apiResponse.getData() != null) {
                            List<Airport> airportList = apiResponse.getData();

                            // Lấy danh sách sân bay từ airportList
                            List<String> airportNames = new ArrayList<>();
                            for (Airport airport : airportList) {
                                airportNames.add(airport.getDiaDiem());
                            }

                            // Đặt dữ liệu vào Adapter và cập nhật Spinner
                            fromAdapter.clear();
                            fromAdapter.addAll(airportNames);
                            fromAdapter.notifyDataSetChanged();

                            // Đặt dữ liệu vào Adapter cho Spinner điểm đến
                            toAdapter.clear();
                            toAdapter.addAll(airportNames);
                            toAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<Airport>>> call, Throwable t) {
                        Log.e("YourSearchActivity", "Lỗi khi thực hiện cuộc gọi API (getAirport)", t);
                        Toast.makeText(YourSearchActivity.this, "Gọi API lỗi (getAirport)", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}