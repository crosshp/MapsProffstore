package com.proffstore.andrew.mapsproffstore.DataBase;

import android.content.Context;

import com.proffstore.andrew.mapsproffstore.Entity.ControlPoint;
import com.proffstore.andrew.mapsproffstore.Entity.Point;
import com.proffstore.andrew.mapsproffstore.Entity.Route;
import com.proffstore.andrew.mapsproffstore.Entity.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Andrew on 18.05.2016.
 */
public class DAO {
    RealmConfiguration realmConfig = null;

    public DAO(Context context) {
        realmConfig = new RealmConfiguration.Builder(context).deleteRealmIfMigrationNeeded().build();
    }

    public Realm getRealm() {
        return Realm.getInstance(realmConfig);
    }

    public void saveControlPoint(ControlPoint controlPoint) {
        Realm realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(controlPoint);
        realm.commitTransaction();
    }

    public void savePoint(Point point) {
        Realm realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(point);
        realm.commitTransaction();
    }

    public List<ControlPoint> getAllControlPoints() {
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<ControlPoint> realmResults = realm.where(ControlPoint.class).findAll();
        return realmResults.subList(0, realmResults.size());
    }

    public List<Point> getAllPoints() {
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<Point> realmResults = realm.where(Point.class).findAll();
        return realmResults.subList(0, realmResults.size());
    }

    public Point getPointById(int id) {
        Realm realm = Realm.getInstance(realmConfig);
        Point realmResult = realm.where(Point.class).equalTo("id", id).findFirst();
        return realmResult;
    }

    public ControlPoint getControlPointById(int id) {
        Realm realm = Realm.getInstance(realmConfig);
        ControlPoint realmResult = realm.where(ControlPoint.class).equalTo("id", id).findFirst();
        return realmResult;
    }

    public void deletePoint(int id) {
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<Point> results = realm.where(Point.class).findAll();
        realm.beginTransaction();
        results.deleteFromRealm(id);
        realm.commitTransaction();
    }

    public void deleteControlPoint(int id) {
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<ControlPoint> results = realm.where(ControlPoint.class).findAll();
        realm.beginTransaction();
        results.deleteFromRealm(id);
        realm.commitTransaction();
    }

    public void deleteAllPoints() {
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<Point> results = realm.where(Point.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void savePoint(List<Point> allPoints) {
        Realm realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(allPoints);
        realm.commitTransaction();
    }

    public void addControlPoint(Point point, ControlPoint controlPoint) {
        Realm realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        point.addControlPoint(controlPoint);
        realm.copyToRealm(point);
        realm.commitTransaction();
    }

    public void saveUser(User user) {
        Realm realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(user);
        realm.commitTransaction();
    }

    public void deleteUser() {
        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<User> results = realm.where(User.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public User getUser() {
        Realm realm = Realm.getInstance(realmConfig);
        User result = realm.where(User.class).findFirst();
        return result;
    }


}
