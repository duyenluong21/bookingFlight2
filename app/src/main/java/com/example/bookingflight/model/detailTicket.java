package com.example.bookingflight.model;

public class detailTicket {
    private String maVe ;
    private String maCB ;
    private String soLuong ;
    private String soLuongCon;
    private String hangVe ;
    private String giaHangVe ;


    public detailTicket(String maVe, String maCB, String soLuong, String soLuongCon, String hangVe, String giaHangVe) {
        this.maVe = maVe;
        this.maCB = maCB;
        this.soLuong = soLuong;
        this.soLuongCon = soLuongCon;
        this.hangVe = hangVe;
        this.giaHangVe = giaHangVe;
    }

    public String getMaCB() {
        return maCB;
    }

    public void setMaCB(String maCB) {
        this.maCB = maCB;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(String soLuong) {
        this.soLuong = soLuong;
    }

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

    public String getGiaHangVe() {
        return giaHangVe;
    }

    public void setGiaHangVe(String giaHangVe) {
        this.giaHangVe = giaHangVe;
    }
}
