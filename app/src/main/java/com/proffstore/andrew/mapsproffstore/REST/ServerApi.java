package com.proffstore.andrew.mapsproffstore.REST;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.proffstore.andrew.mapsproffstore.Entity.Point;
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
        return new ArrayList<>();
    }

    public static User authUser(String login, String pass) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("login", login);
        requestParams.add("password", pass);
        ServerRestClient.get(ServerRestClient.AUTH_URL, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        return new User();
    }

}
