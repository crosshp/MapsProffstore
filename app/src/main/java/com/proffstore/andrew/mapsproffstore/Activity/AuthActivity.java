package com.proffstore.andrew.mapsproffstore.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.proffstore.andrew.mapsproffstore.DataBase.DAO;
import com.proffstore.andrew.mapsproffstore.Entity.User;
import com.proffstore.andrew.mapsproffstore.R;
import com.proffstore.andrew.mapsproffstore.REST.ServerApi;
import com.proffstore.andrew.mapsproffstore.REST.ServerRestClient;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Andrew on 22.05.2016.
 */
public class AuthActivity extends AppCompatActivity {
    AppCompatActivity activity = this;
    DAO dao = null;
    ImageView imageView = null;
    ProgressBar progressBar = null;
    View root = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_layout);
        dao = new DAO(getBaseContext());
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBarAuth);
        root = findViewById(android.R.id.content);
        final EditText editLogin = (EditText) findViewById(R.id.editLogin);
        final EditText editPass = (EditText) findViewById(R.id.editPassword);
        Button button = (Button) findViewById(R.id.loginButton);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = editLogin.getText().toString();
                String pass = editPass.getText().toString();
                if (login.length() != 0 && pass.length() != 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    authUser(login, pass);
                    Intent intent = new Intent(activity, MapsLayoutActivity.class);
                    activity.startActivity(intent);
                } else {
                    Snackbar.make(root, R.string.edit_field, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public void authUser(String login, String pass) {
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
                dao.saveUser(user[0]);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                Snackbar.make(root, R.string.error_auth, Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
