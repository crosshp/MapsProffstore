package com.proffstore.andrew.mapsproffstore.Entity;

import android.util.Log;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Andrew on 18.05.2016.
 */
public class Point extends RealmObject {
    private double lng;
    private double lat;
    private String name;
    private int id;
    private RealmList<ControlPoint> controlPoints = new RealmList<>();


    public Point(double lat, double lng, String name) {
        this.lng = lng;
        this.lat = lat;
        this.name = name;
    }

    public Point() {
    }


    public void removeControlPoints(List<ControlPoint> removeList) {
        controlPoints.removeAll(removeList);
    }

    public void addControlPoint(ControlPoint controlPoint) {
        controlPoints.add(controlPoint);
    }

    public List<ControlPoint> getControlPoints() {
        return controlPoints;
    }

    @Override
    public String toString() {
        String s = "Point" + name + "" + lat + "\n";
        for (ControlPoint controlPoint : controlPoints) {
            s += controlPoint.toString() + "\n";
        }
        return s;
    }

    public boolean containsByName(ControlPoint secondPoint) {
        for (ControlPoint controlPoint : controlPoints) {
            if (controlPoint.getName().equals(secondPoint.getName()) &&
                    controlPoint.getLat() == secondPoint.getLat() &&
                    controlPoint.getLng() == secondPoint.getLng() &&
                    controlPoint.getRadius() == secondPoint.getRadius()) {
                return true;
            }
        }
        return false;
    }

    public void setControlPoints(RealmList<ControlPoint> controlPoints) {
        this.controlPoints = controlPoints;
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
