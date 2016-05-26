package com.proffstore.andrew.mapsproffstore.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.proffstore.andrew.mapsproffstore.Entity.Point;
import com.proffstore.andrew.mapsproffstore.Entity.Route;
import com.proffstore.andrew.mapsproffstore.R;
import com.proffstore.andrew.mapsproffstore.REST.ServerApi;
import com.proffstore.andrew.mapsproffstore.Receiver.MonitoringPointReceiver;
import com.proffstore.andrew.mapsproffstore.YandexMapCustom.ControlPointOverlay;
import com.proffstore.andrew.mapsproffstore.YandexMapCustom.LongTapOverlay;
import com.proffstore.andrew.mapsproffstore.YandexMapCustom.RouteIRender;
import com.proffstore.andrew.mapsproffstore.YandexMapCustom.RouteOverlay;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.MapLayer;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

public class YandexMapsLayoutActivity extends AppCompatActivity {
    private MapView mMap;
    private static boolean isRussian = true;
    private SharedPreferences sharedPreferences = null;
    public YandexMapsLayoutActivity activity = this;
    private static String NAME_ACCOUNT = "NAME_ACCOUNT";
    private static String EMAIL_ACCOUNT = "EMAIL_ACCOUNT";
    DAO dao = null;
    MonitoringPointReceiver receiver = null;
    public static MapController mMapController = null;
    public static OverlayManager overlayManager = null;
    public static Overlay overlay = null;
    public static ControlPointOverlay controlPointOverlay = null;
    public static RouteOverlay routeOverlay = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    public void exit() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        dao.deleteUser();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yandex_map_layout);

        final AlarmManager am = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("Intent MY");
        final PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10, pi); // Millisec * Second * Minute

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
        mMap.showFindMeButton(false);
        mMap.showZoomButtons(false);
        mMapController = mMap.getMapController();
        mMapController.setZoomCurrent(0);
        overlayManager = mMapController.getOverlayManager();
        overlay = new Overlay(mMapController);
        controlPointOverlay = new ControlPointOverlay(mMapController, getBaseContext());

        LongTapOverlay longTapOverlay = new LongTapOverlay(mMapController, getBaseContext(), activity);
        routeOverlay = new RouteOverlay(mMapController);

        OverlayItem pointItem = new OverlayItem(new GeoPoint(0, 0), getResources().getDrawable(R.drawable.bus48));
        OverlayItem pointItem1 = new OverlayItem(new GeoPoint(0, 50), getResources().getDrawable(R.drawable.bus48));
        OverlayItem pointItem2 = new OverlayItem(new GeoPoint(50, 30), getResources().getDrawable(R.drawable.bus48));
        List<OverlayItem> overlayItems = new ArrayList<>();
        overlayItems.add(pointItem);
        overlayItems.add(pointItem1);
        overlayItems.add(pointItem2);
        routeOverlay.setOverlayItemsRoute(overlayItems);
        routeOverlay.addOverlayItem(pointItem);
        routeOverlay.addOverlayItem(pointItem1);
        routeOverlay.addOverlayItem(pointItem2);

        controlPointOverlay.addOverlayItem(pointItem2);
        overlayManager.addOverlay(longTapOverlay);
        overlayManager.addOverlay(controlPointOverlay);
        overlayManager.addOverlay(routeOverlay);

        mMapController.addMapListener(new OnMapListener() {
            @Override
            public void onMapActionEvent(MapEvent mapEvent) {
                if (mapEvent.getMsg() == MapEvent.MSG_SCROLL_END) {
                    Log.e("Точка", "Не видно!!!");
                    routeOverlay.addOverlayItem(new OverlayItem(mMapController.getGeoPoint(new ScreenPoint(mMapController.getHeight() / 2, mMapController.getWidth() / 2)), getResources().getDrawable(R.drawable.bus48)));
                }
            }
        });
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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabYandex);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(YandexMapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.display_mode));
                builder.setNegativeButton(getResources().getString(R.string.cancel), null);
                final boolean[] isPointShow = {true};
                builder.setSingleChoiceItems(new String[]{getResources().getString(R.string.show_points), getResources().getString(R.string.show_routes)}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            isPointShow[0] = true;
                        } else {
                            isPointShow[0] = false;
                        }
                    }
                });
                builder.setPositiveButton(getResources().getString(R.string.apply), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isPointShow[0]) {
                            showDialogToPoints();
                        } else {
                            showDialogToRoutes();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public void showDialogToRoutes() {
        final Point[] resultPoint = {null};
        final AlertDialog.Builder builder = new AlertDialog.Builder(YandexMapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.show_routes));
        builder.setPositiveButton(getResources().getString(R.string.show), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (resultPoint[0] != null) {
                    Route route = ServerApi.getRoute(resultPoint[0], null);
                    showRouteOnMap(route);
                } else {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.error_point), Toast.LENGTH_SHORT).show();
                }
            }
        });
        final List<Point> list = dao.getAllPoints();
        View view = View.inflate(YandexMapsLayoutActivity.this, R.layout.route_content, null);
        builder.setView(view);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setFocusable(true);
        spinner.setFocusableInTouchMode(true);
        spinner.requestFocus();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_spinner_item, getPointsName());
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resultPoint[0] = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                resultPoint[0] = list.get(0);
            }
        });
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
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setFocusable(true);
                negative.setFocusableInTouchMode(true);
                negative.requestFocus();
            }
        });
    }

    private void showRouteOnMap(Route route) {
        List<AppLatLng> routePoints = route.getPoints();
        List<OverlayItem> pointsOnMap = new ArrayList<>();
        routeOverlay.clearOverlayItems();
        for (AppLatLng routePoint : routePoints) {
            OverlayItem overlayItem = new OverlayItem(new GeoPoint(routePoint.getLat(), routePoint.getLng()), getResources().getDrawable(R.drawable.bus48));
            routeOverlay.addOverlayItem(overlayItem);
            pointsOnMap.add(overlayItem);
        }
        BalloonItem ballonStart = new BalloonItem(getBaseContext(), new GeoPoint(routePoints.get(0).getLat(), routePoints.get(0).getLng()));
        ballonStart.setText(getResources().getString(R.string.start_route));
        pointsOnMap.get(0).setBalloonItem(ballonStart);
        BalloonItem ballonFinish = new BalloonItem(getBaseContext(), new GeoPoint(routePoints.get(routePoints.size() - 1).getLat(), routePoints.get(routePoints.size() - 1).getLng()));
        ballonStart.setText(getResources().getString(R.string.end_route));
        pointsOnMap.get(pointsOnMap.size() - 1).setBalloonItem(ballonFinish);
        routeOverlay.setOverlayItemsRoute(pointsOnMap);
        mMapController.notifyRepaint();
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

    public void showPointsOnMap(List<Point> pointList) {
        overlay.clearOverlayItems();
        for (Point point : pointList) {
            BalloonItem balloonItem = new BalloonItem(getBaseContext(), new GeoPoint(point.getLat(), point.getLng()));
            balloonItem.setText(point.getName());
            OverlayItem pointItem = new OverlayItem(new GeoPoint(point.getLat(), point.getLng()), getResources().getDrawable(R.drawable.car48));
            pointItem.setBalloonItem(balloonItem);
            overlay.addOverlayItem(pointItem);
        }
        overlayManager.addOverlay(overlay);
        mMapController.notifyRepaint();
    }

    private Drawer initializeDrawer() {
        SecondaryDrawerItem pointItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.point_item).withIcon(R.drawable.ic_map_marker);
        SecondaryDrawerItem googleItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.google_map).withIcon(R.drawable.google_maps_icon);
        SecondaryDrawerItem routeItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.route_item).withIcon(R.drawable.ic_routes);
        SecondaryDrawerItem langItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.lang_item).withIcon(R.drawable.ic_language_black_36dp);
        SecondaryDrawerItem earthItem = (SecondaryDrawerItem) new SecondaryDrawerItem().withName(R.string.folk_item).withIcon(R.drawable.ic_earth);
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
                                showDialogToPoints();
                                break;
                            }
                            case 2: {
                                showDialogToRoutes();
                                break;
                            }
                            case 4: {
                                finish();
                                break;
                            }
                            case 6: {
                                mMapController.setCurrentMapLayer(mMapController.getMapLayerByLayerId(3));
                                break;
                            }
                            case 7: {
                                mMapController.setCurrentMapLayer(mMapController.getMapLayerByLayerId(2));
                                break;

                            }
                            case 8: {
                                mMapController.setCurrentMapLayer(mMapController.getMapLayerByLayerId(1));
                                break;
                            }
                            case 10: {
                                showLangDialog();
                                break;
                            }
                            case 11: {
                                exit();
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


    public void showDialogToPoints() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(YandexMapsLayoutActivity.this, R.style.AppCompatAlertDialogStyle);
        final List<Point> indexes = new ArrayList<>();
        builder.setTitle(getResources().getString(R.string.show_points));
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        final List<Point> list = dao.getAllPoints();
        boolean[] listChecked = new boolean[list.size()];
        for (boolean isChecked : listChecked) {
            isChecked = false;
        }
        List<String> pointsName = new ArrayList<>();
        for (Point point : list) {
            pointsName.add(point.getName());
        }
        builder.setMultiChoiceItems(pointsName.toArray(new String[]{}), listChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    indexes.add(list.get(which));
                } else {
                    indexes.remove(list.get(which));
                }
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.show), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showPointsOnMap(indexes);
            }
        });
        final AlertDialog alertDialog = builder.show();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setFocusable(true);
                negative.setFocusableInTouchMode(true);
                negative.requestFocus();
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
