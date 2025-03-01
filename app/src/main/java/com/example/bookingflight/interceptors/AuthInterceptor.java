package com.example.bookingflight.interceptors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("jwt_token", null);

        if (jwtToken == null) {
            Log.e("AuthInterceptor", "Token not found");
            return chain.proceed(chain.request());
        }
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + jwtToken);
        Request modifiedRequest = builder.build();
        return chain.proceed(modifiedRequest);
    }
}
