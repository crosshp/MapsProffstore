package com.proffstore.andrew.mapsproffstore;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsLayoutActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        LatLng sydney1 = new LatLng(-35, 152);
        LatLng sydney2 = new LatLng(-20, 12);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in new Sydney"));
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in new Sydney"));
        mMap.addPolyline(new PolylineOptions().add(sydney).add(sydney1).add(sydney2));
        mMap.addCircle(new CircleOptions().center(sydney).radius(5000).visible(true).fillColor(Color.BLUE).strokeColor(Color.YELLOW));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
