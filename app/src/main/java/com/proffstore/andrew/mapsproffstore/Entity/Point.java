package com.proffstore.andrew.mapsproffstore.Entity;

import io.realm.RealmObject;

/**
 * Created by Andrew on 18.05.2016.
 */
public class Point extends RealmObject {
    private double lng;
    private double lat;
    private String name;
    private int id;

    public Point(double lat, double lng, String name) {
        this.lng = lng;
        this.lat = lat;
        this.name = name;
    }

    public Point() {
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
