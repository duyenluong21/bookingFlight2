package com.example.bookingflight.model;

public class VoucherUsage {
    private String maKH, maVoucher, ngayDung;

    public VoucherUsage(String maKH, String maVoucher, String ngayDung) {
        this.maKH = maKH;
        this.maVoucher = maVoucher;
        this.ngayDung = ngayDung;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaVoucher() {
        return maVoucher;
    }

    public void setMaVoucher(String maVoucher) {
        this.maVoucher = maVoucher;
    }

    public String getNgayDung() {
        return ngayDung;
    }

    public void setNgayDung(String ngayDung) {
        this.ngayDung = ngayDung;
    }
}
