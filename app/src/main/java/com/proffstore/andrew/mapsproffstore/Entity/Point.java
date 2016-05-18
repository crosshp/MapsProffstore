package com.proffstore.andrew.mapsproffstore.Entity;

import io.realm.RealmObject;

/**
 * Created by Andrew on 18.05.2016.
 */
public class Point extends RealmObject {
    private float lng;
    private float lat;
    private String name;
    private int id;

    public Point(float lng, float lat, String name) {
        this.lng = lng;
        this.lat = lat;
        this.name = name;
    }

    public Point() {
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
