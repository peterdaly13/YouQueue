package com.example.youqueue;

public class LatLong {
    private double lat;
    private double lawng;
    public LatLong(){

    }
    public LatLong(double lat, double lawng){
        this.lat=lat;
        this.lawng=lawng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLawng() {
        return lawng;
    }

    public void setLawng(double lawng) {
        this.lawng = lawng;
    }
}
