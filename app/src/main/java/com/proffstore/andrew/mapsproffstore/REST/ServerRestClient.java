package com.proffstore.andrew.mapsproffstore.REST;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Andrew on 30.04.2016.
 */
public class ServerRestClient {
    private static final String BASE_URL = "https://habrahabr.ru/post/181338/";
    public static final String GET_USER_URL = "get/";
    public static final String AUTH_URL = "get/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
