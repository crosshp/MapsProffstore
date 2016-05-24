package com.proffstore.andrew.mapsproffstore.REST;

import android.content.Context;

import com.proffstore.andrew.mapsproffstore.DataBase.DAO;
import com.proffstore.andrew.mapsproffstore.Entity.ControlPoint;
import com.proffstore.andrew.mapsproffstore.Entity.Point;

import java.util.List;

/**
 * Created by Andrew on 19.05.2016.
 */
public class Synhronize {

    public static void synhronizePoints(Context context, String param) {
        DAO dao = new DAO(context);
        List<Point> list = dao.getAllPoints();
        dao.getRealm().beginTransaction();
        list.get(0).setLat(0);
        list.get(0).setLng(0);
        list.get(1).setLat(0);
        list.get(2).setLat(0);
        dao.getRealm().commitTransaction();
        /*List<Point> list = ServerApi.getAllPoints(param);
        if (list != null) {
           // dao.deleteAllPoints();
           // dao.savePoint(list);
        }*/
    }

}
