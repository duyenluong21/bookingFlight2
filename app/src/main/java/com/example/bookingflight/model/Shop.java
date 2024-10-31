package com.example.bookingflight.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Shop implements Parcelable {
    private String maShop ;
    private String tenShop;
    private String vido;
    private String kinhdo;
    private String diaChi;
    private float distance;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Shop(){

    }
    public Shop(String maShop, String tenShop, String vido, String kinhdo, String diaChi) {
        this.maShop = maShop;
        this.tenShop = tenShop;
        this.vido = vido;
        this.kinhdo = kinhdo;
        this.diaChi = diaChi;
    }

    public String getMaShop() {
        return maShop;
    }

    public void setMaShop(String maShop) {
        this.maShop = maShop;
    }

    public String getTenShop() {
        return tenShop;
    }

    public void setTenShop(String tenShop) {
        this.tenShop = tenShop;
    }

    public String getVido() {
        return vido;
    }

    public void setVido(String vido) {
        this.vido = vido;
    }

    public String getKinhdo() {
        return kinhdo;
    }

    public void setKinhdo(String kinhdo) {
        this.kinhdo = kinhdo;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    protected Shop(Parcel in) {
    }

    public static final Creator<Shop> CREATOR = new Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
    }
}
