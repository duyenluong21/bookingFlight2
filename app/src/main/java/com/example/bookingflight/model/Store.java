package com.example.bookingflight.model;

public class Store {
    private String tenShop;
    private String diaChi;
    private String kinhdo;
    private String vido;
    private double distance;

    public Store(){

    }

    public Store(String tenShop, String diaChi, String kinhdo, String vido) {
        this.tenShop = tenShop;
        this.diaChi = diaChi;
        this.kinhdo = kinhdo;
        this.vido = vido;
    }

    public String getTenShop() {
        return tenShop;
    }

    public void setTenShop(String tenShop) {
        this.tenShop = tenShop;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getKinhdo() {
        return kinhdo;
    }

    public void setKinhdo(String kinhdo) {
        this.kinhdo = kinhdo;
    }

    public String getVido() {
        return vido;
    }

    public void setVido(String vido) {
        this.vido = vido;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
