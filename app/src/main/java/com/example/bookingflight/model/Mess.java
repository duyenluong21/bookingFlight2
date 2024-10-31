package com.example.bookingflight.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Mess implements Parcelable{
    private String maTN;
    private String noiDung1;
    private String thoiGianGui;
    private String maKH;
    private String noiDung2;
    private boolean isAutoReply;
    private boolean fromCustomer;
    private String fullname;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    // Constructor cho các tin nhắn
    public Mess(String maKH, String noiDung2, String noiDung1,String thoiGianGui) {
        this.maKH = maKH;
        this.noiDung2 = noiDung2;
        this.thoiGianGui = thoiGianGui;
        this.noiDung1 = noiDung1;// Khởi tạo giá trị
    }
    public Mess(String maKH, String fullname, String noiDung2, String noiDung1,String thoiGianGui) {
        this.maKH = maKH;
        this.fullname = fullname ;
        this.noiDung2 = noiDung2;
        this.thoiGianGui = thoiGianGui;
        this.noiDung1 = noiDung1;// Khởi tạo giá trị
    }

    public boolean isFromCustomer() {
        return fromCustomer;
    }

    public void setFromCustomer(boolean fromCustomer) {
        this.fromCustomer = fromCustomer;
    }

    public Mess(String maKH, String noiDung2, String thoiGianGui) {
        this.maKH = maKH;
        this.noiDung2 = noiDung2;
        this.thoiGianGui = thoiGianGui;
    }
    public Mess(){

    }

    public boolean isAutoReply() {
        return isAutoReply;
    }

    public void setAutoReply(boolean autoReply) {
        isAutoReply = autoReply;
    }

    public String getMaTN() {
        return maTN;
    }

    public void setMaTN(String maTN) {
        this.maTN = maTN;
    }

    public String getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(String thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getNoiDung2() {
        return noiDung2;
    }

    public void setNoiDung2(String noiDung2) {
        this.noiDung2 = noiDung2;
    }

    public String getNoiDung1() {
        return noiDung1;
    }

    public void setNoiDung1(String noiDung1) {
        this.noiDung1 = noiDung1;
    }

    @Override
    public String toString() {
        return "Mess{" +
                "maTN='" + maTN + '\'' +
                ", noiDung1='" + noiDung1 + '\'' +
                ", thoiGianGui='" + thoiGianGui + '\'' +
                ", maKH='" + maKH + '\'' +
                ", noiDung2='" + noiDung2 + '\'' +
                '}';
    }

    protected Mess(Parcel in) {
        maTN = in.readString();
        noiDung1 = in.readString();
        thoiGianGui = in.readString();
        maKH = in.readString();
        noiDung2 = in.readString();
    }

    public static final Creator<Mess> CREATOR = new Creator<Mess>() {
        @Override
        public Mess createFromParcel(Parcel in) {
            return new Mess(in);
        }

        @Override
        public Mess[] newArray(int size) {
            return new Mess[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(maTN);
        dest.writeString(noiDung1);
        dest.writeString(thoiGianGui);
        dest.writeString(maKH);
        dest.writeString(noiDung2);
    }
}
