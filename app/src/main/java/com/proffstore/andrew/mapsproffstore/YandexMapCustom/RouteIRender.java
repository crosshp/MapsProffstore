package com.proffstore.andrew.mapsproffstore.YandexMapCustom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.List;

import ru.yandex.yandexmapkit.overlay.IRender;
import ru.yandex.yandexmapkit.overlay.OverlayItem;

/**
 * Created by Andrew on 26.05.2016.
 */
public class RouteIRender implements IRender {
    RouteOverlay routeOverlay = null;

    public RouteIRender(RouteOverlay routeOverlay) {
        this.routeOverlay = routeOverlay;
    }

    @Override
    public void draw(Canvas canvas, OverlayItem overlayItemInput) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        List<OverlayItem> overlayItems = routeOverlay.getOverlayItemsRoute();
        if (overlayItems.size() < 2) return;
        for (int i = 0; i < overlayItems.size() - 1; i++) {
            Path path = new Path();
            path.moveTo(overlayItems.get(i + 1).getScreenPoint().getX(), overlayItems.get(i + 1).getScreenPoint().getY());
            path.lineTo(overlayItems.get(i).getScreenPoint().getX(), overlayItems.get(i).getScreenPoint().getY());
            canvas.drawPath(path, paint);
            canvas.save();
        }
        canvas.restore();
    }

}
