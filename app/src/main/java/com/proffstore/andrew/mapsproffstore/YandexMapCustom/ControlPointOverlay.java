package com.proffstore.andrew.mapsproffstore.YandexMapCustom;

import android.content.Context;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;

/**
 * Created by Andrew on 25.05.2016.
 */
public class ControlPointOverlay extends Overlay {

    Context context = null;

    public ControlPointOverlay(MapController mapController, Context context) {
        super(mapController);
        this.context = context;
        this.setIRender(new ControlPointIRender(context));
    }

    @Override
    public int compareTo(Object another) {
        return 0;
    }
}
