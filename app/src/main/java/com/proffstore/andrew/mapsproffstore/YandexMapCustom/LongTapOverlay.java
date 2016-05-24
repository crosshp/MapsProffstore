package com.proffstore.andrew.mapsproffstore.YandexMapCustom;

import android.content.Context;

import com.proffstore.andrew.mapsproffstore.Activity.YandexMapsLayoutActivity;
import com.proffstore.andrew.mapsproffstore.R;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

/**
 * Created by Andrew on 24.05.2016.
 */
public class LongTapOverlay extends Overlay {
    Context context = null;

    public LongTapOverlay(MapController mapController, Context context) {
        super(mapController);
        this.context = context;
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }

    @Override
    public boolean onLongPress(float v, float v1) {
        OverlayItem pointItem = new OverlayItem(YandexMapsLayoutActivity.mMapController.getGeoPoint(new ScreenPoint(v, v1)), context.getResources().getDrawable(R.drawable.bus48));
        YandexMapsLayoutActivity.overlay.addOverlayItem(pointItem);
        YandexMapsLayoutActivity.overlayManager.addOverlay(YandexMapsLayoutActivity.overlay);
        YandexMapsLayoutActivity.mMapController.notifyRepaint();
        return true;
    }
}
