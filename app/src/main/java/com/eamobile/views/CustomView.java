package com.eamobile.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.eamobile.DownloadActivityInternal;

public class CustomView extends LinearLayout implements IDownloadView {
    protected static final float TEXT_BODY_SIZE = 16.0f;
    protected static final float TEXT_TITLE_SIZE = 18.0f;
    protected Context context;
    protected Handler mHandler;

    /* access modifiers changed from: package-private */
    public class BackGround extends Drawable {
        BackGround() {
        }

        public void draw(Canvas canvas) {
            if (DownloadActivityInternal.getMainActivity() != null && DownloadActivityInternal.getMainActivity().getBackgroundBitmap() != null) {
                canvas.drawBitmap(DownloadActivityInternal.getMainActivity().getBackgroundBitmap(), (float) ((canvas.getWidth() - DownloadActivityInternal.getMainActivity().getBackgroundBitmap().getWidth()) >> 1), (float) ((canvas.getHeight() - DownloadActivityInternal.getMainActivity().getBackgroundBitmap().getHeight()) >> 1), (Paint) null);
            }
        }

        public int getOpacity() {
            return 0;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public CustomView(Context context2) {
        super(context2);
        this.context = context2;
    }

    public static Button addButton(Context context2, LinearLayout linearLayout, String str) {
        Button button = new Button(context2);
        button.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        button.setPadding(15, 15, 15, 15);
        button.setTextSize(1, TEXT_BODY_SIZE);
        button.setText(str);
        linearLayout.addView(button);
        return button;
    }

    public static TextView addTitle(Context context2, LinearLayout linearLayout, String str) {
        TextView textView = new TextView(context2);
        textView.setClickable(false);
        textView.setCursorVisible(false);
        textView.setSingleLine(false);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextSize(1, TEXT_TITLE_SIZE);
        textView.setTextColor(-1);
        textView.setText(str);
        linearLayout.addView(textView);
        return textView;
    }

    @Override // com.eamobile.views.IDownloadView
    public void clean() {
    }

    @Override // com.eamobile.views.IDownloadView
    public void init() {
        showBackground();
        ((Activity) this.context).getWindow().getDecorView().setSystemUiVisibility(5894);
    }

    public void pause() {
    }

    public void resume() {
    }

    /* access modifiers changed from: protected */
    public void showBackground() {
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        setOrientation(1);
        setGravity(48);
        setBackgroundDrawable(new BackGround());
    }
}
