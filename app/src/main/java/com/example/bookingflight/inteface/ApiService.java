package com.example.bookingflight.inteface;

import com.example.bookingflight.activity.ApiResponse;
import com.example.bookingflight.model.Ad;
import com.example.bookingflight.model.Airport;
import com.example.bookingflight.model.BookTicket;
import com.example.bookingflight.model.Mess;
import com.example.bookingflight.model.Pay;
import com.example.bookingflight.model.PostTicket;
import com.example.bookingflight.model.PublicKeyRequest;
import com.example.bookingflight.model.PublicKeyStaff;
import com.example.bookingflight.model.Result;
import com.example.bookingflight.model.Shop;
import com.example.bookingflight.model.Staff;
import com.example.bookingflight.model.Store;
import com.example.bookingflight.model.TicketCount;
import com.example.bookingflight.model.User;
import com.example.bookingflight.model.UserFingerprintRequest;
import com.example.bookingflight.model.Voucher;
import com.example.bookingflight.model.VoucherUsage;
import com.example.bookingflight.model.detailTicket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    ApiService searchFlight = new Retrofit.Builder()
            .baseUrl("http://192.168.1.5/TTCS/app/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("api/createAccount.php")
    Call<ApiResponse<List<User>>> postUser(@Body Map<String, String> userData);

    @GET("api/readFlightApp.php")
    Call<ApiResponse<List<Result>>> searchPlace(@QueryMap Map<String, String> option);

    @GET("api/readPassengerAccount.php")
    Call<ApiResponse<List<User>>> getListUser(@QueryMap Map<String, String> option);

    @GET("api/readTicket.php")
    Call<ApiResponse<List<detailTicket>>> getTicket();

    @GET("api/readDetailTicket.php")
    Call<ApiResponse<List<BookTicket>>> getBookedTickets();

    @GET("api/readVoucher.php")
    Call<ApiResponse<List<Voucher>>> getVoucher();

    static List<BookTicket> filterBookedTicketsByMaKH(List<BookTicket> allBookedTickets, String maKH) {
        List<BookTicket> userBookedTickets = new ArrayList<>();

        for (BookTicket ticket : allBookedTickets) {
            if (maKH.equals(ticket.getMaKH())) {
                userBookedTickets.add(ticket);
            }
        }

        return userBookedTickets;
    }

    @POST("api/createDetailTicket.php")
    Call<ApiResponse<List<PostTicket>>> sendPostTicket(@Body PostTicket postTicket);

    @GET("api/readAirport.php")
    Call<ApiResponse<List<Airport>>> getAirport();

    @PUT("api/updatePassenger.php")
    Call<ApiResponse<List<User>>> updateInfo(@Query("maKH") String maKH, @Body Map<String, String> updateParams);

    @PUT("api/updatePWPassenger.php")
    Call<ApiResponse<List<User>>> updatePassw(@Query("maKH") String maKH, @Body Map<String, String> updateParams);
    @GET("api/readMess.php")
    Call<ApiResponse<List<Mess>>> getListMess(@Query("maKH") String maKH);
    @POST("api/createMess.php")
    Call<ApiResponse<List<Mess>>> storeMess(@Body Mess requestData);

    @GET("api/readRegistrationDate.php")
    Call<ApiResponse<User>> getRegistrationDate(@Query("maKH") String maKH);

    @GET("api/readVoucherNew.php")
    Call<ApiResponse<List<Voucher>>> getVoucherNew(@Query("maKH") String maKH);

    @GET("api/readVoucherVip.php")
    Call<ApiResponse<List<Voucher>>> getVoucherVip(@Query("maKH") String maKH);

    @GET("api/readVoucherCustomer.php")
    Call<ApiResponse<List<Voucher>>> getVoucherCustomer(@Query("maKH") String maKH);

    @GET("api/readTicketCount.php")
    Call<ApiResponse<TicketCount>> getTicketCount(@Query("maKH") String maKH);

    @POST("api/createVoucherUsage.php")
    Call<ApiResponse<List<VoucherUsage>>> sendVoucherUsage(@Body VoucherUsage voucherUsage);

    @PUT("api/updateNumberOfTickets.php")
    Call<ApiResponse<List<detailTicket>>> updateNumberOfTickets(@Query("maVe") String maVe, @Query("maCB") String maCB, @Body Map<String, String> updateParams);

    @GET("api/readAd.php")
    Call<ApiResponse<List<Ad>>> getAd();
    @GET("api/readAd.php")
    Call<ApiResponse<Ad>> getAdById(@Query("maqc") String maqc);

    @GET("api/readPassenger.php")
    Call<ApiResponse<List<User>>> getUser();
    @GET("api/readPassenger.php")
    Call<ApiResponse<User>> getPassengerById(@Query("maKH") String maKH);

    @GET("api/readShopMap.php")
    Call<ApiResponse<List<Shop>>> getShop() ;
    @GET("api/readShop.php")
    Call<ApiResponse<List<Store>>> getStore() ;

    @GET("api/readPayById.php")
    Call<ApiResponse<List<Pay>>> getPayById(@Query("maKH") String maKH);

    @PUT("api/updatePublicKey.php")
    Call<Void> updatePublicKey(@Query("maKH") String maKH, @Body PublicKeyRequest updateParams);

    @GET("api/readPublicKeyStaff.php")
    Call<ApiResponse<PublicKeyStaff>> getPublicKey(@Query("maNV") String maNV);

    @GET("api/checkPublicKey.php")
    Call<ResponseBody> checkIfUserHasPublicKey(@Query("maKH") String maKH);

    @GET("api/readUserChat.php")
    Call<ApiResponse<List<Staff>>> getListStaff();

    @GET("api/readUser.php")
    Call<ApiResponse<List<Staff>>> getStaff(@Query("maNV") String maNV);

    @PUT("api/updateFingerprint.php")
    Call<ResponseBody> registerFingerprint(@Query("maKH") String maKH, @Body UserFingerprintRequest request);

}
