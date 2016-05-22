package com.proffstore.andrew.mapsproffstore.REST;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.proffstore.andrew.mapsproffstore.Entity.AppLatLng;
import com.proffstore.andrew.mapsproffstore.Entity.Point;
import com.proffstore.andrew.mapsproffstore.Entity.Route;
import com.proffstore.andrew.mapsproffstore.Entity.User;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Andrew on 18.05.2016.
 */
public class ServerApi {

    public static List<Point> getAllPoints(String param) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("key", param);
        ServerRestClient.get(ServerRestClient.GET_USER_URL, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        ArrayList<Point> list = new ArrayList<>();
        list.add(new Point(0, 0, "l1"));
        list.add(new Point(1, 1, "l2"));
        list.add(new Point(2, 2, "l3"));
        list.add(new Point(3, 4, "l4"));
        list.add(new Point(5, 5, "l5"));
        return list;
    }

    public static Route getRoute(Point point, String param) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("key", "TEST");
        ServerRestClient.get(ServerRestClient.GET_ROUTE_URL, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        Route route = new Route();
        route.setName("Route");
        route.setDistance("33km");
        List<AppLatLng> appLatLngs = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            AppLatLng appLatLng = new AppLatLng(i * 3 + 7 + i * i, i * 5 + 9 + i * i);
            appLatLngs.add(appLatLng);
        }
        route.setPoints(appLatLngs);
        return route;
    }


    public static User authUser(String login, String pass) {
        final User[] user = {null};
        RequestParams requestParams = new RequestParams();
        requestParams.add("login", login);
        requestParams.add("password", pass);
        ServerRestClient.get(ServerRestClient.AUTH_URL, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                user[0] = new User("Andrew", 0);
                /*
                *
                *
                *
                *
                *
                * */
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        return user[0];
    }

}
