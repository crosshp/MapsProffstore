package com.proffstore.andrew.mapsproffstore.Entity;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Andrew on 21.05.2016.
 */
public class Route  {
    private String name;
    private String distance;
    private List<AppLatLng> points;

    public Route(String name, String distance, List<AppLatLng> points) {
        this.name = name;
        this.distance = distance;
        this.points = points;
    }

    public Route() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<AppLatLng> getPoints() {
        return points;
    }

    public void setPoints(List<AppLatLng> points) {
        this.points = points;
    }
}
