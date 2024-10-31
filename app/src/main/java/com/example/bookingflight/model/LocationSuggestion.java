package com.example.bookingflight.model;

public class LocationSuggestion {
    private String name;
    private double latitude;
    private double longitude;
    private double distance;

    public LocationSuggestion(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = 0; // Mặc định là 0
    }

    public LocationSuggestion(String name, double latitude, double longitude, double distance) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance; // Khoảng cách từ vị trí hiện tại
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

