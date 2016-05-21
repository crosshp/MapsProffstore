package com.proffstore.andrew.mapsproffstore.Entity;

import com.google.android.gms.maps.model.LatLng;

import io.realm.RealmObject;

/**
 * Created by Andrew on 21.05.2016.
 */
public class AppLatLng {
    private double lat;
    private double lng;

    public AppLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public AppLatLng() {
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

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }
}
