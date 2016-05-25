package com.proffstore.andrew.mapsproffstore.YandexMapCustom;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.proffstore.andrew.mapsproffstore.Activity.YandexMapsLayoutActivity;
import com.proffstore.andrew.mapsproffstore.DataBase.DAO;
import com.proffstore.andrew.mapsproffstore.Entity.ControlPoint;
import com.proffstore.andrew.mapsproffstore.R;

import java.util.List;
import java.util.TreeMap;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

/**
 * Created by Andrew on 24.05.2016.
 */
public class LongTapOverlay extends Overlay {
    Context context = null;
    DAO dao = null;
    YandexMapsLayoutActivity activity = null;

    public LongTapOverlay(MapController mapController, Context context, YandexMapsLayoutActivity activity) {
        super(mapController);
        this.context = context;
        this.activity = activity;
        dao = new DAO(context);


    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }

    @Override
    public boolean onLongPress(float v, float v1) {
        GeoPoint geoPoint = YandexMapsLayoutActivity.mMapController.getGeoPoint(new ScreenPoint(v, v1));
        ControlPoint controlPointClick = onLongCircleListener(geoPoint);
        if (controlPointClick != null) {
            showControlPointDialog(controlPointClick);
        } else {
            showCreateNewControlPointDialog(new LatLng(geoPoint.getLat(), geoPoint.getLon()));
        }
        /*OverlayItem pointItem = new OverlayItem(YandexMapsLayoutActivity.mMapController.getGeoPoint(new ScreenPoint(v, v1)), context.getResources().getDrawable(R.drawable.bus48));
        YandexMapsLayoutActivity.overlay.addOverlayItem(pointItem);
        YandexMapsLayoutActivity.overlayManager.addOverlay(YandexMapsLayoutActivity.overlay);
        YandexMapsLayoutActivity.mMapController.notifyRepaint();*/
        return true;
    }


    public ControlPoint onLongCircleListener(GeoPoint clickPoint) {
        TreeMap<Double, Integer> distanceMap = new TreeMap<>();
        List<ControlPoint> controlPoints = dao.getAllControlPoints();
        int i = 0;
        for (ControlPoint controlPoint : controlPoints) {
            LatLng latLngControlPoint = new LatLng(controlPoint.getLat(), controlPoint.getLng());
            LatLng cliclPointLatLng = new LatLng(clickPoint.getLat(), clickPoint.getLon());
            Double distance = SphericalUtil.computeDistanceBetween(cliclPointLatLng, latLngControlPoint);
            if (distance <= controlPoint.getRadius()) {
                distanceMap.put(distance, i);
            }
            i++;
        }
        if (!distanceMap.isEmpty()) {
            int index = distanceMap.get(distanceMap.firstKey());
            ControlPoint controlPointClick = controlPoints.get(index);
            return controlPointClick;
        }
        return null;
    }


    private void showControlPointDialog(final ControlPoint controlPoint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(context.getResources().getString(R.string.settings_cp));
        final boolean[] isSetControlPoint = {true};
        builder.setSingleChoiceItems(new String[]{context.getResources().getString(R.string.set_cp), context.getResources().getString(R.string.add_cp)}, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            isSetControlPoint[0] = true;
                        } else {
                            isSetControlPoint[0] = false;
                        }
                    }
                });
        builder.setPositiveButton(context.getResources().getString(R.string.apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isSetControlPoint[0]) {
                    showSetControlPointDialog(controlPoint);
                } else {
                    showCreateNewControlPointDialog(new LatLng(controlPoint.getLat(), controlPoint.getLng()));
                }
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.cancel), null);
        builder.show();
    }

    private void showSetControlPointDialog(final ControlPoint controlPoint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
        final View view = View.inflate(activity, R.layout.control_point_dialog, null);
        builder.setView(view);
        final EditText editName = (EditText) view.findViewById(R.id.editNameKT);
        editName.setText(controlPoint.getName());
        final EditText editRadius = (EditText) view.findViewById(R.id.editRadiusKT);
        editRadius.setText(String.valueOf(controlPoint.getRadius()));
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
                        // TODO Do something
                        if (editName.getText().length() == 0 || editRadius.getText().length() == 0) {
                            Toast.makeText(context, R.string.edit_field, Toast.LENGTH_SHORT).show();
                        } else {
                            dao.getRealm().beginTransaction();
                            controlPoint.setName(editName.getText().toString());
                            controlPoint.setRadius(Double.valueOf(editRadius.getText().toString()));
                            dao.getRealm().commitTransaction();




                        /*    mMap.clear();
                            initializeAllPoints();
                            initializeControlPoints();*/


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


    private void showCreateNewControlPointDialog(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
        final View view = View.inflate(activity, R.layout.control_point_dialog, null);
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
                            Toast.makeText(context, R.string.edit_field, Toast.LENGTH_SHORT).show();
                        } else {
                        /*    circleKT[0] = new CircleOptions().strokeWidth(3).center(latLng)
                                    .radius(Double.valueOf(editRadius.getText().toString())).visible(true)
                                    .fillColor(ContextCompat.getColor(context, R.color.colorMarker))
                                    .strokeColor(ContextCompat.getColor(context, R.color.colorMarkerCorner));
                            markerOptions[0] = new MarkerOptions()
                                    .title(editName.getText().toString())
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker52));*/

                    /*        mMap.addCircle(circleKT[0]);
                            mMap.addMarker(markerOptions[0]);

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));*/


                            ControlPoint controlPoint = new ControlPoint(editName.getText().toString(),
                                    latLng.latitude,
                                    latLng.longitude,
                                    Double.valueOf(editRadius.getText().toString()));
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

}
