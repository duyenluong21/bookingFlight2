package com.example.bookingflight.model;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public class Result implements Parcelable {
    private Date flightTime;
     private String maCB;
    private String maDB;
    private String maMB;

    public Date getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(Date flightTime) {
        this.flightTime = flightTime;
    }

    protected Result(Parcel in) {
        maCB = in.readString();
        maDB = in.readString();
        maMB = in.readString();
        ngayDen = in.readString();
        ngayDi = in.readString();
        diaDiemDen = in.readString();
        diaDiemDi = in.readString();
        ghiChu = in.readString();
        gioBay = in.readString();
        giaVe = in.readString();
        soLuongCon = in.readString();
        hangVe = in.readString();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public String getMaCB() {
        return maCB;
    }

    public void setMaCB(String maCB) {
        this.maCB = maCB;
    }

    public String getMaDB() {
        return maDB;
    }

    public void setMaDB(String maDB) {
        this.maDB = maDB;
    }

    public String getMaMB() {
        return maMB;
    }

    public void setMaMB(String maMB) {
        this.maMB = maMB;
    }

    public String getNgayDen() {
        return ngayDen;
    }

    public void setNgayDen(String ngayDen) {
        this.ngayDen = ngayDen;
    }

    public String getNgayDi() {
        return ngayDi;
    }

    public void setNgayDi(String ngayDi) {
        this.ngayDi = ngayDi;
    }

    public String getDiaDiemDen() {
        return diaDiemDen;
    }

    public void setDiaDiemDen(String diaDiemDen) {
        this.diaDiemDen = diaDiemDen;
    }

    public String getDiaDiemDi() {
        return diaDiemDi;
    }

    public void setDiaDiemDi(String diaDiemDi) {
        this.diaDiemDi = diaDiemDi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getGioBay() {
        return gioBay;
    }

    public void setGioBay(String gioBay) {
        this.gioBay = gioBay;
    }

    public String getGiaVe() {
        return giaVe;
    }

    public void setGiaVe(String giaVe) {
        this.giaVe = giaVe;
    }

    private String ngayDen;
    private String ngayDi;
    private String diaDiemDen;
    private String diaDiemDi;
    private String ghiChu;
    private String gioBay;
    private String giaVe;
    private String soLuongCon;
    private String hangVe;

    public String getSoLuongCon() {
        return soLuongCon;
    }

    public void setSoLuongCon(String soLuongCon) {
        this.soLuongCon = soLuongCon;
    }

    public String getHangVe() {
        return hangVe;
    }

    public void setHangVe(String hangVe) {
        this.hangVe = hangVe;
    }

    @Override
    public String toString() {
        return "Result{" +
                "maCB='" + maCB + '\'' +
                ", maDB='" + maDB + '\'' +
                ", maMB='" + maMB + '\'' +
                ", ngayDen='" + ngayDen + '\'' +
                ", ngayDi='" + ngayDi + '\'' +
                ", diaDiemDen='" + diaDiemDen + '\'' +
                ", diaDiemDi='" + diaDiemDi + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                ", gioBay='" + gioBay + '\'' +
                ", giaVe='" + giaVe + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(maCB);
        dest.writeString(maDB);
        dest.writeString(maMB);
        dest.writeString(ngayDen);
        dest.writeString(ngayDi);
        dest.writeString(diaDiemDen);
        dest.writeString(diaDiemDi);
        dest.writeString(ghiChu);
        dest.writeString(gioBay);
        dest.writeString(giaVe);
        dest.writeString(soLuongCon);
        dest.writeString(hangVe);
    }
}
