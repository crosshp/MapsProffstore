package com.proffstore.andrew.mapsproffstore;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

public class MapsLayoutActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(R.array.map_types));
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMap != null) {
                    switch (position) {
                        case 0: {
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        }
                        case 1: {
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                        }
                        case 2: {
                            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Initialize google map
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // Override volume buttons
    // Set zoom depends by volume buttons
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP: {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            }
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            }
            default:
                break;
        }
        return true;
    }

    CircleOptions circle = null;
    LatLng sydney = null;
    MarkerOptions markerOptions = null;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        sydney = new LatLng(-34, 151);
        LatLng sydney1 = new LatLng(-35, 152);
        LatLng sydney2 = new LatLng(-20, 12);
        final MarkerOptions markerOptions = new MarkerOptions().position(sydney).title("Marker in Sydney");
        mMap.addMarker(markerOptions);
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in new Sydney"));
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in new Sydney"));
        mMap.addPolyline(new PolylineOptions().add(sydney).add(sydney1).add(sydney2));
        circle = new CircleOptions().center(sydney).radius(5000).visible(true).fillColor(ContextCompat.getColor(this, R.color.colorMarker)).strokeColor(ContextCompat.getColor(this, R.color.colorPrimary));
        mMap.addCircle(circle);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Toast.makeText(getApplicationContext(), String.valueOf(SphericalUtil.computeDistanceBetween(sydney, sydney1) <= circle.getRadius()), Toast.LENGTH_SHORT).show();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
                Toast.makeText(getApplicationContext(), String.valueOf(SphericalUtil.computeDistanceBetween(sydney, latLng) <= circle.getRadius()), Toast.LENGTH_SHORT).show();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
                Intent intent = new Intent(getApplication(), MapsLayoutActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
                builder.setSmallIcon(R.drawable.common_plus_signin_btn_icon_light)
                        .setContentInfo("Info")
                        .setContentText("Content text")
                        .setContentIntent(pendingIntent);
                NotificationManagerCompat.from(getBaseContext()).notify(1,builder.build());
            }
        });
    }
}
