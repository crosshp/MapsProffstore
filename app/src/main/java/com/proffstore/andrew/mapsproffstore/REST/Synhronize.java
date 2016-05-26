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
        /*List<Point> list = ServerApi.getAllPoints(param);
        if (list != null) {
           // dao.deleteAllPoints();
           // dao.savePoint(list);
        }*/
    }

}
