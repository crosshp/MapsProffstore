package com.proffstore.andrew.mapsproffstore.YandexMapCustom;

import android.graphics.BitmapFactory;

import com.proffstore.andrew.mapsproffstore.R;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

/**
 * Created by Andrew on 24.05.2016.
 */
public class RouteOverlay extends Overlay {

    MapView mMmapView;

    public RouteOverlay(MapController arg0, MapView mapView) {
        super(arg0);
        mMmapView = mapView;
        this.setIRender(new YandexOverlayIRender(this));
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }
/*
    @Override
    public boolean onLongPress(float x, float y) {
        OverlayItem m = new OverlayItem(this.c.getGeoPoint(new ScreenPoint(x, y)),
                BitmapFactory.decodeResource(
                        this.c.getContext().getResources(),
                        R.drawable.flag2leftred));
        m.setOffsetY(-23);
        this.addOverlayItem(m);
        this.c.setPositionNoAnimationTo(this.c
                .getGeoPoint(new ScreenPoint(x, y)));
        return true;
    }*/
}