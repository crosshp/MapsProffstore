package com.proffstore.andrew.mapsproffstore.YandexMapCustom;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;

/**
 * Created by Andrew on 24.05.2016.
 */
public class RouteOverlay extends Overlay {
    public List<OverlayItem> overlayItemsRoute = new ArrayList<>();

    public RouteOverlay(MapController arg0) {
        super(arg0);
        this.setIRender(new RouteIRender(this));
    }

    @Override
    public int compareTo(Object arg0) {
        return 0;
    }

    public List<OverlayItem> getOverlayItemsRoute() {
        return overlayItemsRoute;
    }

    public void setOverlayItemsRoute(List<OverlayItem> overlayItemsRoute) {
        this.overlayItemsRoute = overlayItemsRoute;
    }
}