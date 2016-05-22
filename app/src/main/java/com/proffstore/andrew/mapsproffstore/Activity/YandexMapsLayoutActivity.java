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
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.proffstore.andrew.mapsproffstore.DataBase.DAO;
import com.proffstore.andrew.mapsproffstore.Entity.AppLatLng;
import com.proffstore.andrew.mapsproffstore.Entity.ControlPoint;
import com.proffstore.andrew.mapsproffstore.Entity.Point;
import com.proffstore.andrew.mapsproffstore.Entity.Route;
import com.proffstore.andrew.mapsproffstore.R;
import com.proffstore.andrew.mapsproffstore.REST.ServerApi;
import com.proffstore.andrew.mapsproffstore.Receiver.MonitoringPointReceiver;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;

public class YandexMapsLayoutActivity extends AppCompatActivity {

    public static int notificationId = 0;
    private MapView mMap;
    private static boolean isRussian = true;
    private SharedPreferences sharedPreferences = null;
    private static String NAME_ACCOUNT = "NAME_ACCOUNT";
    private static String EMAIL_ACCOUNT = "EMAIL_ACCOUNT";
    DAO dao = null;
    MonitoringPointReceiver receiver = null;
    MapController mMapController = null;

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
        setContentView(R.layout.yandex_map_layout);
        Toast.makeText(getBaseContext(), "Yandex", Toast.LENGTH_SHORT).show();
        final AlarmManager am = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("Intent MY");
        final PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pi); // Millisec * Second

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
        mMap = (MapView) findViewById(R.id.mapYandex);
        mMap.showFindMeButton(true);
        mMap.showZoomButtons(true);
        mMap.showJamsButton(true);
        mMap.showScaleView(true);
        mMap.showBuiltInScreenButtons(true);
        mMapController = mMap.getMapController();

        // Initialize google map
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
        List<Point> pointList = dao.getAllPoints();
        List<String> strings = new ArrayList<>();
        for (Point point : pointList) {
            strings.add(point.getName());
        }
        return strings;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP: {
                mMapController.zoomIn();
                break;
            }
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                mMapController.zoomOut();
                break;
            }
            default:
                break;
        }
        return true;
    }

    private Drawer initializeDrawer() {
        SecondaryDrawerItem pointItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.point_item).withIcon(R.drawable.ic_map_marker);
        SecondaryDrawerItem googleItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.google_map).withIcon(R.drawable.google_maps_icon);
        SecondaryDrawerItem routeItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.route_item).withIcon(R.drawable.ic_routes);
        SecondaryDrawerItem langItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.lang_item).withIcon(R.drawable.ic_language_black_36dp);
        SecondaryDrawerItem earthItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.earth_item).withIcon(R.drawable.ic_earth);
        SecondaryDrawerItem hybridItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.hybrid_item).withIcon(R.drawable.ic_google_earth);
        SecondaryDrawerItem mapItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.map_item).withIcon(R.drawable.ic_map);
        SecondaryDrawerItem exitItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.exit_item).withIcon(R.drawable.ic_exit_to_app);
        Drawer drawer = new DrawerBuilder().withActivity(this).withAccountHeader(getAccount()).addDrawerItems(pointItem, routeItem, new DividerDrawerItem(), googleItem, new DividerDrawerItem(), earthItem, hybridItem, mapItem, new DividerDrawerItem(), langItem, exitItem)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.e("position", String.valueOf(position));
                        switch (position) {
                            case 1: {
                                //   showDialogToPoints();
                                break;
                            }
                            case 2: {
                                //showDialogToRoutes();
                                break;
                            }
                            case 4: {
                                finish();
                                break;
                            }
                            case 6: {
                            //    if (isMapReady)
                                    break;
                            }
                            case 7: {
                            //    if (isMapReady)
                                    //     mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                    break;

                            }
                            case 8: {
                               // if (isMapReady)
                                    //       mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                    break;
                            }
                            case 10: {
                                //showLangDialog();
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

    public void showLangDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(YandexMapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
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
