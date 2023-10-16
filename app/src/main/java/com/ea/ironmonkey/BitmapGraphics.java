package com.ea.ironmonkey;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;

public class BitmapGraphics {
    private Bitmap bitmap;
    private Canvas canvas = new Canvas();

    public BitmapGraphics(int i, int i2) {
        this.bitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        this.canvas.setBitmap(this.bitmap);
    }

    private static Paint createPaint(Typeface typeface, float f) {
        Paint paint = new Paint();
        paint.setTypeface(typeface);
        paint.setTextSize(f);
        paint.setColor(-1);
        paint.setAntiAlias(true);
        return paint;
    }

    public static Paint createPaintFromFamilyName(String str, float f) {
        return createPaint(Typeface.create(str, 0), f);
    }

    public static Paint createPaintFromFile(String str, float f) {
        return createPaint(Typeface.createFromFile(str), f);
    }

    public void clear() {
        this.canvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    public void drawString(Paint paint, String str, int i, int i2) {
        this.canvas.drawText(str, (float) i, (float) i2, paint);
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }
}
