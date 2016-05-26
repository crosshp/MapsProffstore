package com.proffstore.andrew.mapsproffstore.YandexMapCustom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ru.yandex.yandexmapkit.overlay.IRender;
import ru.yandex.yandexmapkit.overlay.OverlayItem;

/**
 * Created by Andrew on 25.05.2016.
 */
public class ControlPointIRender implements IRender {
    Context context = null;

    public ControlPointIRender(Context context) {
        this.context = context;
    }

    @Override
    public void draw(Canvas canvas, OverlayItem overlayItem) {
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(overlayItem.getScreenPoint().getX(), overlayItem.getScreenPoint().getY(), 50, paint);
        canvas.save();
        canvas.restore();
    }

}
