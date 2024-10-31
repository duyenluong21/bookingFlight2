package com.example.bookingflight.model;

public class Pay {
    private String ngayThanhToan;
    private String gioThanhToan;
    private String tongThanhToan;
    private String maKH;

    public Pay(String ngayThanhToan, String gioThanhToan, String tongThanhToan, String maKH) {
        this.ngayThanhToan = ngayThanhToan;
        this.gioThanhToan = gioThanhToan;
        this.tongThanhToan = tongThanhToan;
        this.maKH = maKH;
    }

    public String getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(String ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public String getGioThanhToan() {
        return gioThanhToan;
    }

    public void setGioThanhToan(String gioThanhToan) {
        this.gioThanhToan = gioThanhToan;
    }

    public String getTongThanhToan() {
        return tongThanhToan;
    }

    public void setTongThanhToan(String tongThanhToan) {
        this.tongThanhToan = tongThanhToan;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }
}
