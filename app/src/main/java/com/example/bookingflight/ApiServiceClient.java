package com.example.bookingflight;

import android.content.Context;

import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.interceptors.AuthInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.Executors;

public class ApiServiceClient {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    private static ApiService apiService;

    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            AuthInterceptor authInterceptor = new AuthInterceptor(context);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .build();

            // Cấu hình Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.7/TTCS/app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .client(client)
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}

