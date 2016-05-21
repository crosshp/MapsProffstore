package com.proffstore.andrew.mapsproffstore.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.proffstore.andrew.mapsproffstore.DataBase.DAO;
import com.proffstore.andrew.mapsproffstore.Entity.ControlPoint;
import com.proffstore.andrew.mapsproffstore.Entity.Point;
import com.proffstore.andrew.mapsproffstore.Entity.PointItem;
import com.proffstore.andrew.mapsproffstore.R;
import com.proffstore.andrew.mapsproffstore.REST.Synhronize;
import com.proffstore.andrew.mapsproffstore.Receiver.MonitoringPointReceiver;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsLayoutActivity extends AppCompatActivity implements OnMapReadyCallback {

    public MapsLayoutActivity() {
    }

    public static int notificationId = 0;
    private GoogleMap mMap;
    SupportMapFragment mapFragment = null;
    private static boolean isRussian = true;
    boolean isMapReady = false;
    private SharedPreferences sharedPreferences = null;
    private static String NAME_ACCOUNT = "NAME_ACCOUNT";
    private static String EMAIL_ACCOUNT = "EMAIL_ACCOUNT";
    DAO dao = null;
    MonitoringPointReceiver receiver = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AlarmManager am = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("Intent MY");
        final PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi); // Millisec * Second * Minute

        IntentFilter filter = new IntentFilter();
        filter.addAction("Intent MY");
        receiver = new MonitoringPointReceiver();
        registerReceiver(receiver, filter);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        String lang = sharedPreferences.getString("lang", "ru");
        String cntr = sharedPreferences.getString("country", "RUS");
        if (lang.equals("ru")) {
            isRussian = true;
        }
        dao = new DAO(getBaseContext());
        Locale locale = new Locale(lang, cntr);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.maps_layout);
        // Initialize google map
        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] isPointView = {true};
                List<PointItem> pointItems = getPointsItem();
                final PointAdapter adapter = new PointAdapter(getBaseContext(), pointItems);

                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
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
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener(){

                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negative.setFocusable(true);
                        negative.setFocusableInTouchMode(true);
                        negative.requestFocus();
                    }
                });

                Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                spinner.setFocusable(true);
                spinner.setFocusableInTouchMode(true);
                spinner.requestFocus();
                List<String> lis = new ArrayList<String>();
                for (PointItem pointItem : pointItems) {
                    lis.add(pointItem.getName());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_spinner_item, lis);
                spinner.setAdapter(arrayAdapter);
                final ListView pointsList = (ListView) view.findViewById(R.id.listView);
                final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.routesMenuContent);

                pointsList.setAdapter(adapter);

                final EditText editShowStart = (EditText) view.findViewById(R.id.editShowStart);
                editShowStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
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
                                            editShowStart.setText(stringBuilder);
                                        }
                                    },
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)
                            );
                            dpd.show(getFragmentManager(), "DatepickerdialogCalendar");
                        }
                    }
                });
                final EditText editShowFinish = (EditText) view.findViewById(R.id.editShowFinish);
                editShowFinish.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
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
                                            editShowFinish.setText(stringBuilder);
                                        }
                                    },
                                    now.get(Calendar.YEAR),
                                    now.get(Calendar.MONTH),
                                    now.get(Calendar.DAY_OF_MONTH)
                            );
                            dpd.show(getFragmentManager(), "Datepickerdialog");
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
                            PointAdapter adapter = new PointAdapter(getBaseContext(), getPointsItem());
                            pointsList.setAdapter(adapter);
                        } else {
                            isPointView[0] = false;
                            pointsList.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                            builder.setMultiChoiceItems(null, null, null);
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
        Synhronize.synhronizePoints(getBaseContext(), null);
        List<Point> pointList = dao.getAllPoints();
        List<String> strings = new ArrayList<>();
        for (Point point : pointList) {
            strings.add(point.getName());
        }
        return strings;
    }

    public List<PointItem> getPointsItem() {
        Synhronize.synhronizePoints(getBaseContext(), null);
        List<Point> pointList = dao.getAllPoints();
        List<PointItem> strings = new ArrayList<>();
        for (Point point : pointList) {
            PointItem pointItem = new PointItem(point.getName(), false);
            strings.add(pointItem);
        }
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
                                showDialogToPoints();
                                break;
                            }
                            case 2: {
                                showDialogToRoutes();
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

    public void showDialogToPoints() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.show_points));
        builder.setPositiveButton(getResources().getString(R.string.show), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        List<String> list = getPointsName();
        boolean[] listChecked = new boolean[list.size()];
        for (boolean isChecked : listChecked) {
            isChecked = false;
        }
        builder.setMultiChoiceItems(list.toArray(new String[]{}), listChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        final AlertDialog alertDialog = builder.show();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener(){

            @Override
            public void onShow(DialogInterface dialog) {
                Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setFocusable(true);
                negative.setFocusableInTouchMode(true);
                negative.requestFocus();
            }
        });
    }


    public void showDialogToRoutes() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.show_points));
        builder.setPositiveButton(getResources().getString(R.string.show), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        View view = View.inflate(MapsLayoutActivity.this, R.layout.route_content, null);
        builder.setView(view);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setFocusable(true);
        spinner.setFocusableInTouchMode(true);
        spinner.requestFocus();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_spinner_item, getPointsName());
        spinner.setAdapter(arrayAdapter);
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        final EditText editShowStart = (EditText) view.findViewById(R.id.editShowStart);
        editShowStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
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
                                    editShowStart.setText(stringBuilder);
                                }
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "DatepickerdialogCalendar");
                }
            }
        });
        final EditText editShowFinish = (EditText) view.findViewById(R.id.editShowFinish);
        editShowFinish.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
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
                                    editShowFinish.setText(stringBuilder);
                                }
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            }
        });
        final AlertDialog alertDialog = builder.show();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener(){

            @Override
            public void onShow(DialogInterface dialog) {
                Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setFocusable(true);
                negative.setFocusableInTouchMode(true);
                negative.requestFocus();
            }
        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.app_lang));
        builder.setSingleChoiceItems(new String[]{getResources().getString(R.string.rus), getResources().getString(R.string.ukr)}, isRussian ? 0 : 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    isRussian = true;
                } else {
                    isRussian = false;
                }
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Toast.makeText(getBaseContext(), "isRussian = " + String.valueOf(isRussian), Toast.LENGTH_SHORT).show();
                if (isRussian) {
                    editor.putString("lang", "ru");
                    editor.putString("country", "RUS");

                } else {
                    editor.putString("lang", "uk");
                    editor.putString("country", "UA");
                }
                editor.commit();
                recreate();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        builder.show();
    }

    public void initializeControlPoints() {
        List<ControlPoint> controlPoints = dao.getAllControlPoints();
        for (ControlPoint controlPoint : controlPoints) {
            LatLng latLng = new LatLng(controlPoint.getLat(), controlPoint.getLng());
            mMap.addMarker(new MarkerOptions().title(controlPoint.getName()).position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker52)));
            mMap.addCircle(new CircleOptions().center(latLng).radius(controlPoint.getRadius())
                    .strokeWidth(3)
                    .fillColor(ContextCompat.getColor(getBaseContext(), R.color.colorMarker))
                    .strokeColor(ContextCompat.getColor(getBaseContext(), R.color.colorMarkerCorner)));
        }
    }

    public void initializeAllPoints() {
        List<Point> points = dao.getAllPoints();
        for (Point point : points) {
            LatLng latLng = new LatLng(point.getLat(), point.getLng());
            mMap.addMarker(new MarkerOptions().title(point.getName()).position(latLng));
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.setPosition(new LatLng(0, 0));
                    return true;
                }
            });
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        initializeControlPoints();
        initializeAllPoints();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                final CircleOptions[] circleKT = {null};
                final MarkerOptions[] markerOptions = {null};
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
                final View view = View.inflate(MapsLayoutActivity.this, R.layout.control_point_dialog, null);
                builder.setView(view);
                final EditText editName = (EditText) view.findViewById(R.id.editNameKT);
                final EditText editRadius = (EditText) view.findViewById(R.id.editRadiusKT);
                builder.setPositiveButton(R.string.apply, null);
                builder.setNegativeButton(R.string.cancel, null);
                final AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean closeDialog = false;
                                Log.e("Dialog", "I am here");
                                // TODO Do something
                                if (editName.getText().length() == 0 || editRadius.getText().length() == 0) {
                                    Toast.makeText(getBaseContext(), R.string.edit_field, Toast.LENGTH_SHORT).show();
                                } else {
                                    circleKT[0] = new CircleOptions().strokeWidth(3).center(latLng)
                                            .radius(Integer.valueOf(editRadius.getText().toString())).visible(true)
                                            .fillColor(ContextCompat.getColor(getBaseContext(), R.color.colorMarker))
                                            .strokeColor(ContextCompat.getColor(getBaseContext(), R.color.colorMarkerCorner));
                                    markerOptions[0] = new MarkerOptions()
                                            .title(editName.getText().toString())
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker52));
                                    mMap.addCircle(circleKT[0]);
                                    mMap.addMarker(markerOptions[0]);
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                    ControlPoint controlPoint = new ControlPoint(editName.getText().toString(),
                                            circleKT[0].getCenter().latitude,
                                            circleKT[0].getCenter().longitude,
                                            circleKT[0].getRadius());
                                    dao.saveControlPoint(controlPoint);
                                    closeDialog = true;
                                }
                                if (closeDialog) {
                                    alertDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });


        // Tochka !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


        //  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1

        //       Toast.makeText(getApplicationContext(), String.valueOf(SphericalUtil.computeDistanceBetween(sydney, sydney1) <= circle.getRadius()), Toast.LENGTH_SHORT).show();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                Point point = new Point(latLng.latitude, latLng.longitude, "marker");
                dao.savePoint(point);
//                Toast.makeText(getApplicationContext(), String.valueOf(SphericalUtil.computeDistanceBetween(sydney, latLng) <= circle.getRadius()), Toast.LENGTH_SHORT).show();

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
