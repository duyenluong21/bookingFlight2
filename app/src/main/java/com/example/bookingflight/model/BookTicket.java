package com.example.bookingflight.model;

import android.util.Log;

public class BookTicket {
    private String maKH ;
    private String fullname ;
    private String email ;
    private String password ;
    private String gioiTinh ;
    private String ngaySinh ;
    private String diaChi ;
    private String soDT ;
    private String maCB ;
    private String ngayDen ;
    private String ngayDi ;
    private String diaDiemDen ;
    private String diaDiemDi ;
    private String giaVe ;
    private String gioBay ;
    private String maVe ;
    private String soLuongDat ;
    private String hangVe ;
    private String giaHangVe ;
    private String create_at ;

    public BookTicket(String maKH, String create_at) {
        this.maKH = maKH ;
        this.create_at = create_at;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public BookTicket() {

    }
    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }



    public BookTicket(String maKH, String fullname, String email, String password, String gioiTinh, String ngaySinh, String soDT, String maCB, String ngayDen, String ngayDi, String diaDiemDen,
                      String diaDiemDi, String giaVe, String gioBay, String maVe, String soLuongDat, String hangVe, String giaHangVe, String tongGia) {
        this.maKH = maKH;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.soDT = soDT;
        this.maCB = maCB;
        this.ngayDen = ngayDen;
        this.ngayDi = ngayDi;
        this.diaDiemDen = diaDiemDen;
        this.diaDiemDi = diaDiemDi;
        this.giaVe = giaVe;
        this.gioBay = gioBay;
        this.maVe = maVe;
        this.soLuongDat = soLuongDat;
        this.hangVe = hangVe;
        this.giaHangVe = giaHangVe;
    }


    @Override
    public String toString() {
        return "BookTicket{" +
                "maKH='" + maKH + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", ngaySinh='" + ngaySinh + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", soDT='" + soDT + '\'' +
                ", maCB='" + maCB + '\'' +
                ", ngayDen='" + ngayDen + '\'' +
                ", ngayDi='" + ngayDi + '\'' +
                ", diaDiemDen='" + diaDiemDen + '\'' +
                ", diaDiemDi='" + diaDiemDi + '\'' +
                ", giaVe='" + giaVe + '\'' +
                ", gioBay='" + gioBay + '\'' +
                ", maVe='" + maVe + '\'' +
                ", soLuongDat='" + soLuongDat + '\'' +
                ", hangVe='" + hangVe + '\'' +
                ", giaHangVe='" + giaHangVe + '\'' +
                ", create_at='" + create_at + '\'' +
                '}';
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }


    public String getMaCB() {
        return maCB;
    }

    public void setMaCB(String maCB) {
        this.maCB = maCB;
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

    public String getGiaVe() {
        return giaVe;
    }

    public void setGiaVe(String giaVe) {
        this.giaVe = giaVe;
    }

    public String getGioBay() {
        return gioBay;
    }

    public void setGioBay(String gioBay) {
        this.gioBay = gioBay;
    }

    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getSoLuongDat() {
        return soLuongDat;
    }

    public void setSoLuongDat(String soLuongDat) {
        this.soLuongDat = soLuongDat;
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
