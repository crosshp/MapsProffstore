package com.proffstore.andrew.mapsproffstore.Entity;

import io.realm.RealmObject;

/**
 * Created by Andrew on 18.05.2016.
 */
public class ControlPoint extends RealmObject {
    private String name;
    private double lat;
    private double lng;
    private double radius;

    public ControlPoint() {
    }

    @Override
    public boolean equals(Object o) {
        ControlPoint object = (ControlPoint) o;
        if (o.getClass() != this.getClass()) {
            return false;
        }
        if (this.getName().equals(object.getName()) &&
                this.getLat() == object.getLat() &&
                this.getLng() == object.getLng() &&
                this.getRadius() == object.getRadius()) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Name = " + name + "\n" +
                "Lat = " + lat + "\n" +
                "Lng = " + lng + "\n" +
                "Radius = " + radius;
    }

    public ControlPoint(String name, double lat, double lng, double radius) {
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
