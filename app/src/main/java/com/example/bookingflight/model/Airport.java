package com.example.bookingflight.model;

public class Airport {
    private String maSanBay , tenSanBay, diaDiem;
    private double vido , kinhdo ;
    private float distance; // Thêm thuộc tính distance

    public Airport() {

    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public static void distanceBetween(double vido, double kinhdo, double vido1, double kinhdo1, float[] results) {
    }

    public String getMaSanBay() {
        return maSanBay;
    }

    public void setMaSanBay(String maSanBay) {
        this.maSanBay = maSanBay;
    }

    public String getTenSanBay() {
        return tenSanBay;
    }

    public void setTenSanBay(String tenSanBay) {
        this.tenSanBay = tenSanBay;
    }

    public String getDiaDiem() {
        return diaDiem;
    }

    public void setDiaDiem(String diaDiem) {
        this.diaDiem = diaDiem;
    }

    public double getVido() {
        return vido;
    }

    public void setVido(double vido) {
        this.vido = vido;
    }

    public double getKinhdo() {
        return kinhdo;
    }

    public void setKinhdo(double kinhdo) {
        this.kinhdo = kinhdo;
    }

    public Airport(String maSanBay, String tenSanBay, String diaDiem, double vido, double kinhdo) {
        this.maSanBay = maSanBay;
        this.tenSanBay = tenSanBay;
        this.diaDiem = diaDiem;
        this.vido = vido ;
        this.kinhdo = kinhdo ;
    }
    public Airport(String maSanBay, String tenSanBay, String diaDiem) {
        this.maSanBay = maSanBay;
        this.tenSanBay = tenSanBay;
        this.diaDiem = diaDiem;
    }
    public Airport(String tenSanBay, double vido, double kinhdo) {
        this.tenSanBay = tenSanBay;
        this.vido = vido;
        this.kinhdo = kinhdo;
    }



    @Override
    public String toString() {
        return "Airport{" +
                "maSanBay='" + maSanBay + '\'' +
                ", tenSanBay='" + tenSanBay + '\'' +
                ", diaDiem='" + diaDiem + '\'' +
                '}';
    }


}
