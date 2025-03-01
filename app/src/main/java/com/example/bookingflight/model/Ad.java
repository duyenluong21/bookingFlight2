package com.example.bookingflight.model;

import java.util.List;

public class Ad {
    private String maqc;
    private String name;
    private String img;
    private String description;
    private String place;



    public Ad(String name, String img, String description, String place) {
        this.name = name;
        this.img = img;
        this.description = description;
        this.place = place;
    }

    public String getImg() {
        return "http://172.20.10.11/TTCS/uploads/quangCao/" + img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaqc() {
        return maqc;
    }

    public void setMaqc(String maqc) {
        this.maqc = maqc;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
