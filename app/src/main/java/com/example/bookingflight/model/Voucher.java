package com.example.bookingflight.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Voucher implements Parcelable {
    private String code, maVoucher;
    private String discount;
    private String ngayHetHan;
    private String ngayTao;
    private String trangThai;

    public Voucher(String code, String discount, String ngayHetHan, String ngayTao, String trangThai) {
        this.code = code;
        this.discount = discount;
        this.ngayHetHan = ngayHetHan;
        this.ngayTao = ngayTao;
        this.trangThai = trangThai;
    }

    // Constructor used for parceling
    protected Voucher(Parcel in) {
        code = in.readString();
        discount = in.readString();
        ngayHetHan = in.readString();
        ngayTao = in.readString();
        trangThai = in.readString();
    }

    // Parcelable.Creator
    public static final Creator<Voucher> CREATOR = new Creator<Voucher>() {
        @Override
        public Voucher createFromParcel(Parcel in) {
            return new Voucher(in);
        }

        @Override
        public Voucher[] newArray(int size) {
            return new Voucher[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(discount);
        dest.writeString(ngayHetHan);
        dest.writeString(ngayTao);
        dest.writeString(trangThai);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(String ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "code='" + code + '\'' +
                ", discount='" + discount + '\'' +
                ", ngayHetHan='" + ngayHetHan + '\'' +
                ", ngayTao='" + ngayTao + '\'' +
                '}';
    }
}
