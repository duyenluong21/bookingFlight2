package com.example.bookingflight.model;

public class PostTicket {
    private String order_id;
    private String maVe;
    private String maCB;
    private String maKH;
    private String soLuongDat;
    private String tongThanhToan;
    private String create_at ;
    private String nguonDat;
    private String maShop;
    public PostTicket(String order_id, String maVe, String maCB, String maKH, String soLuongDat,
                      String tongThanhToan, String nguonDat) {
        this.order_id = order_id;
        this.maVe = maVe;
        this.maCB = maCB;
        this.maKH = maKH;
        this.soLuongDat = soLuongDat;
        this.tongThanhToan = tongThanhToan;
        this.nguonDat = nguonDat;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public PostTicket() {

    }
    public String getTongThanhToan() {
        return tongThanhToan;
    }

    public void setTongThanhToan(String tongThanhToan) {
        this.tongThanhToan = tongThanhToan;
    }
    public String getMaVe() {
        return maVe;
    }

    public void setMaVe(String maVe) {
        this.maVe = maVe;
    }

    public String getMaCB() {
        return maCB;
    }

    public void setMaCB(String maCB) {
        this.maCB = maCB;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getSoLuongDat() {
        return soLuongDat;
    }

    public void setSoLuongDat(String soLuongDat) {
        this.soLuongDat = soLuongDat;
    }

    @Override
    public String toString() {
        return "PostTicket{" +
                "maVe='" + maVe + '\'' +
                ", maCB='" + maCB + '\'' +
                ", maKH='" + maKH + '\'' +
                ", soLuongDat='" + soLuongDat + '\'' +
                '}';
    }
}
