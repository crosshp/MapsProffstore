package com.proffstore.andrew.mapsproffstore.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.proffstore.andrew.mapsproffstore.Activity.MapsLayoutActivity;
import com.proffstore.andrew.mapsproffstore.DataBase.DAO;
import com.proffstore.andrew.mapsproffstore.Entity.ControlPoint;
import com.proffstore.andrew.mapsproffstore.Entity.Point;
import com.proffstore.andrew.mapsproffstore.R;
import com.proffstore.andrew.mapsproffstore.REST.Synhronize;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Andrew on 19.05.2016.
 */
public class MonitoringPointReceiver extends BroadcastReceiver {
    DAO daoImplementation = null;
    Context context = null;
    Realm realm = null;


    @Override
    public void onReceive(Context context, Intent intent) {
        daoImplementation = new DAO(context);
        realm = daoImplementation.getRealm();
        this.context = context;
        Synhronize.synhronizePoints(context, null);
        List<ControlPoint> controlPointList = daoImplementation.getAllControlPoints();
        List<Point> pointList = daoImplementation.getAllPoints();
        for (Point point : pointList) {
            processPoint(point, controlPointList);
        }

    }

    private void processPoint(Point point, List<ControlPoint> controlPointList) {
        processNewControlPoint(point, controlPointList);
        processOldControlPoint(point);
    }

    private void processOldControlPoint(Point point) {
        deleteKT(point);
    }

    private void deleteKT(Point point) {
        LatLng latLngPoint = new LatLng(point.getLat(), point.getLng());
        realm.beginTransaction();
        for (int i = 0; i < point.getControlPoints().size(); i++) {
            LatLng latLngControlPoint = new LatLng(point.getControlPoints().get(i).getLat(), point.getControlPoints().get(i).getLng());
            if (SphericalUtil.computeDistanceBetween(latLngPoint, latLngControlPoint) > point.getControlPoints().get(i).getRadius()) {
                showOutNotification(point.getControlPoints().get(i), point, MapsLayoutActivity.notificationId++);
                point.getControlPoints().remove(i);
            }
        }
        realm.commitTransaction();
    }

    public void processNewControlPoint(Point point, List<ControlPoint> controlPointList) {
        LatLng latLngPoint = new LatLng(point.getLat(), point.getLng());
        for (ControlPoint controlPoint : controlPointList) {
            LatLng latLngControlPoint = new LatLng(controlPoint.getLat(), controlPoint.getLng());
            if (SphericalUtil.computeDistanceBetween(latLngPoint, latLngControlPoint) <= controlPoint.getRadius()) {
                if (!point.containsByName(controlPoint)) {
                    realm.beginTransaction();
                    point.addControlPoint(controlPoint);
                    realm.commitTransaction();
                    Log.e("cp1", String.valueOf(point.getControlPoints().size()));
                    showNotification(point.getControlPoints(), point, MapsLayoutActivity.notificationId++);
                }
            }
        }
    }

    public void showNotification(List<ControlPoint> controlPointList, Point point, int i) {
        if (!controlPointList.isEmpty()) {
            for (ControlPoint controlPoint : controlPointList) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.drawable.marker52);
                String text = context.getString(R.string.attention_point) + point.getName() + "\n" +
                        context.getString(R.string.control_entry) + controlPoint.getName();
                builder.setContentTitle(text);
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
                NotificationManagerCompat.from(context).notify(i, builder.build());
            }
        }
    }

    public void showOutNotification(ControlPoint controlPoint, Point point, int i) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.marker52);
        String text = context.getString(R.string.attention_point) + point.getName() + "\n" +
                context.getString(R.string.control_exit) + controlPoint.getName();
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
        builder.setContentTitle(text);
        NotificationManagerCompat.from(context).notify(i, builder.build());
    }
}




