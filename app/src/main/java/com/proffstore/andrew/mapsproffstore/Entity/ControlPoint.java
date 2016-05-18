package com.proffstore.andrew.mapsproffstore.Entity;

import io.realm.RealmObject;

/**
 * Created by Andrew on 18.05.2016.
 */
public class ControlPoint extends RealmObject {
    private String name;
    private float lat;
    private float lng;
    private float radius;
    private int id;

    public ControlPoint() {
    }

    public ControlPoint(String name, float lat, float lng, float radius) {

        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
