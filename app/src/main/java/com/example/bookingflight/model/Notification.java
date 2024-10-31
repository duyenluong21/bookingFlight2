package com.example.bookingflight.model;

public class Notification {
    private String thongBao;
    private String ngayTao;
    private boolean isRead;

    public Notification() {

    }

    public Notification(String ngayTao, String thongBao) {
        this.ngayTao = ngayTao;
        this.thongBao = thongBao;
        this.isRead = false; // Mặc định là chưa đọc
    }

    public String getThongBao() {
        return thongBao;
    }

    public String getNgayTao() {
        return ngayTao;
    }
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
