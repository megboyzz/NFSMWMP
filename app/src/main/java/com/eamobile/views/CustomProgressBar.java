package com.eamobile.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class CustomProgressBar extends View {
    private Paint paint = new Paint();
    private float progress = BitmapDescriptorFactory.HUE_RED;

    public CustomProgressBar(Context context) {
        super(context);
    }

    public void onDraw(Canvas canvas) {
        this.paint.setColor(-1);
        this.paint.setStrokeWidth(BitmapDescriptorFactory.HUE_RED);
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setARGB(180, 120, 120, 120);
        int width = getWidth() - 10;
        canvas.drawRect(new RectF(10.0f, 1.0f, (float) width, 33.0f), this.paint);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(-256);
        canvas.drawRect(new RectF((float) 11, 2.0f, (float) (11 + ((int) Math.floor((double) (((float) ((width - 1) - 11)) * this.progress)))), 32.0f), this.paint);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(View.MeasureSpec.getSize(i), 34);
    }

    public void setProgress(float f) {
        if (f < BitmapDescriptorFactory.HUE_RED) {
            f = BitmapDescriptorFactory.HUE_RED;
        }
        if (f > 1.0f) {
            f = 1.0f;
        }
        this.progress = f;
    }
}
