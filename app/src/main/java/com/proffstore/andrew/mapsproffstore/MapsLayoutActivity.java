package com.proffstore.andrew.mapsproffstore;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsLayoutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    boolean isMapReady = false;
    private SharedPreferences sharedPreferences = null;
    private static String NAME_ACCOUNT = "NAME_ACCOUNT";
    private static String EMAIL_ACCOUNT = "EMAIL_ACCOUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_layout);
        // Initialize google map
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] isPointView = {true};
                final PointAdapter adapter = new PointAdapter(getBaseContext(), getPointsName());
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.display_mode));
                View view = View.inflate(MapsLayoutActivity.this, R.layout.menu_point_layout, null);
                builder.setView(view);
                builder.setPositiveButton(getResources().getString(R.string.show), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isPointView[0]) {
                            List<Integer> pointsIndex = adapter.getPointsIndex();
                            for (Integer point : pointsIndex) {
                                Log.e("point", String.valueOf(point));
                            }
                        }
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.cancel), null);
                final AlertDialog alertDialog = builder.show();

                final ListView pointsList = (ListView) view.findViewById(R.id.listView);
                pointsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                pointsList.setItemsCanFocus(false);
                final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.routesMenuContent);

                pointsList.setAdapter(adapter);

                EditText editShowStart = (EditText) view.findViewById(R.id.editShowStart);
                final EditText editShowFinish = (EditText) view.findViewById(R.id.editShowFinish);
                editShowFinish.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            Log.e("calendar", "I am in Focus");
                            Calendar now = Calendar.getInstance();
                            DatePickerDialog dpd = DatePickerDialog.newInstance(
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append(dayOfMonth);
                                            stringBuilder.append("/");
                                            stringBuilder.append(monthOfYear + 1);
                                            stringBuilder.append("/");
                                            stringBuilder.append(year);
                                            editShowFinish.append(stringBuilder);
                                            //   editShowFinish.setText(stringBuilder);
                                        }
                                    },
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)
                            );
                            dpd.show(getFragmentManager(), "Datepickerdialog");
                        } else {
                            Log.e("calendar", "NO Focus");
                        }
                    }
                });
                RadioButton radioButtonPoints = (RadioButton) view.findViewById(R.id.radioButtonPoints);
                radioButtonPoints.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            isPointView[0] = true;
                            pointsList.setVisibility(View.VISIBLE);
                            linearLayout.setVisibility(View.GONE);
                            PointAdapter adapter = new PointAdapter(getBaseContext(), getPointsName());
                            pointsList.setAdapter(adapter);
                        } else {
                            isPointView[0] = false;
                            pointsList.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        final Drawer drawer = initializeDrawer();
        ImageButton imageButton = (ImageButton) findViewById(R.id.drawerButton);
        assert imageButton != null;
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer != null) {
                    if (!drawer.isDrawerOpen()) {
                        drawer.openDrawer();
                    } else {
                        drawer.closeDrawer();
                    }
                }
            }
        });


    }

    public List<String> getPointsName() {
        List<String> strings = new ArrayList<>();
        strings.add("Olol");
        strings.add("Olol1");
        strings.add("Olol2");
        strings.add("Olol3");
        strings.add("Olol4");
        strings.add("Olol4");
        strings.add("Olol4");
        strings.add("Olol4");
        strings.add("Olol4");
        strings.add("Olol4");
        strings.add("Olol4");
        strings.add("Olol4");
        strings.add("Olol4");
        return strings;
    }

    private Drawer initializeDrawer() {
        SecondaryDrawerItem pointItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.point_item).withIcon(R.drawable.ic_map_marker);
        SecondaryDrawerItem routeItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.route_item).withIcon(R.drawable.ic_routes);
        SecondaryDrawerItem langItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.lang_item).withIcon(R.drawable.ic_routes);
        SecondaryDrawerItem earthItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.earth_item).withIcon(R.drawable.ic_earth);
        SecondaryDrawerItem hybridItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.hybrid_item).withIcon(R.drawable.ic_google_earth);
        SecondaryDrawerItem mapItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.map_item).withIcon(R.drawable.ic_map);
        SecondaryDrawerItem exitItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.exit_item).withIcon(R.drawable.ic_exit_to_app);
        Drawer drawer = new DrawerBuilder().withActivity(this).withAccountHeader(getAccount()).addDrawerItems(pointItem, routeItem, new DividerDrawerItem(), earthItem, hybridItem, mapItem, new DividerDrawerItem(), langItem, exitItem)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.e("position", String.valueOf(position));
                        switch (position) {
                            case 1: {
                                break;
                            }
                            case 2: {

                                break;
                            }
                            case 4: {
                                if (isMapReady)
                                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            }
                            case 5: {
                                if (isMapReady)
                                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;

                            }
                            case 6: {
                                if (isMapReady)
                                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                break;
                            }
                            case 8: {
                                showLangDialog();
                                break;
                            }
                            default:
                                break;
                        }
                        return false;
                    }
                }).build();
        return drawer;
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

    public void showLangDialog() {
        Log.e("here", "I am here");
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.app_lang));
        builder.setSingleChoiceItems(new String[]{getResources().getString(R.string.rus), getResources().getString(R.string.ukr)}, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(getResources().getString(R.string.apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        builder.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        double x = getIntent().getDoubleExtra("x", 0);
        double y = getIntent().getDoubleExtra("y", 0);
        if (x != 0 && y != 0) {
            LatLng intentLatLng = new LatLng(x, y);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(intentLatLng, 15));
        }
        sydney = new LatLng(-34, 151);
        LatLng sydney1 = new LatLng(-35, 152);
        LatLng sydney2 = new LatLng(-20, 12);
        final MarkerOptions markerOptions = new MarkerOptions().position(sydney).title("Marker in Sydney");
        mMap.addMarker(markerOptions);
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in new Sydney"));
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in new Sydney"));
        mMap.addPolyline(new PolylineOptions().add(sydney).add(sydney1).add(sydney2));
        circle = new CircleOptions().strokeWidth(3).center(sydney).radius(5000).visible(true).fillColor(ContextCompat.getColor(this, R.color.colorMarker)).strokeColor(ContextCompat.getColor(this, R.color.colorMarkerCorner));
        mMap.addCircle(circle);

        Toast.makeText(getApplicationContext(), String.valueOf(SphericalUtil.computeDistanceBetween(sydney, sydney1) <= circle.getRadius()), Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
                Toast.makeText(getApplicationContext(), String.valueOf(SphericalUtil.computeDistanceBetween(sydney, latLng) <= circle.getRadius()), Toast.LENGTH_SHORT).show();
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
                Intent intent = new Intent(getApplication(), MapsLayoutActivity.class);
                intent.putExtra("x", latLng.latitude);
                intent.putExtra("y", latLng.longitude);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
                builder.setSmallIcon(R.drawable.common_plus_signin_btn_icon_light)
                        .setContentInfo("Info")
                        .setContentText("Content text")
                        .setContentIntent(pendingIntent);
                NotificationManagerCompat.from(getBaseContext()).notify(1, builder.build());
            }
        });
    }

    public AccountHeader getAccount() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        String name = sharedPreferences.getString(NAME_ACCOUNT, "NULL");
        String email = sharedPreferences.getString(EMAIL_ACCOUNT, "null@mail");
        AccountHeaderBuilder accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.iniy_gradient)
                .addProfiles(new ProfileDrawerItem()
                        .withEmail(email)
                        .withName(name));
        return accountHeader.build();
    }


}
