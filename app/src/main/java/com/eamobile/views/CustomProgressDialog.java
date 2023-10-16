package com.eamobile.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.eamobile.DownloadActivityInternal;
import com.eamobile.Language;
import com.eamobile.download.ZipExtractor;
import java.io.IOException;
import java.io.InputStream;

public class CustomProgressDialog extends CustomView implements IProgressDialog {
    private static final int WIFI_LAYOUT_ID = 90;
    private static final int WIFI_LAYOUT_WIFI_TEXTVIEW_ID = 92;
    private static final int WIFI_LAYOUT_WIFI_VIEW_ID = 91;
    private static final int WIFI_LEVELS = 4;
    private static final int WIFI_LEVELS_ENABLED = 3;
    private static boolean alwaysSwitchTo3G = false;
    private static boolean showDialogSwitchTo3G = false;
    private static boolean wifiSwitchedTo3G = true;
    private AlertDialog alertDialog;
    private Bitmap[] bmpWifi = null;
    private int[] connectionType = new int[1];
    private CustomProgressBar customProgressBar;
    private Dialog dialog;
    private boolean exitConfirmation = false;
    private Boolean isLoadedBmpWifi;
    private LinearLayout mainLayout;
    private int max;
    private int progress;
    private float speed;
    private TextView wifiTextView;

    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
        this.dialog = null;
        this.customProgressBar = null;
        this.mainLayout = null;
        this.progress = 0;
        this.max = 0;
        if (this.bmpWifi == null) {
            this.bmpWifi = new Bitmap[4];
        }
        for (int i = 0; i < 4; i++) {
            try {
                InputStream open = context.getAssets().open(DownloadActivityInternal.getResourcesPath() + "adc-wifi" + i + ".png");
                if (open != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inTempStorage = new byte[4096];
                    this.bmpWifi[i] = Bitmap.createBitmap(BitmapFactory.decodeStream(open, null, options));
                    open.close();
                }
            } catch (IOException e) {
                this.bmpWifi[i] = null;
            }
        }
        this.isLoadedBmpWifi = true;
        for (int i2 = 0; i2 < 4; i2++) {
            this.isLoadedBmpWifi = Boolean.valueOf(this.isLoadedBmpWifi.booleanValue() && this.bmpWifi[i2] != null);
        }
    }

    private void updateWifiInfo() {
        if (((LinearLayout) this.dialog.findViewById(90)) != null && DownloadActivityInternal.getMainActivity() != null) {
            if (DownloadActivityInternal.getMainActivity().isWifiAvailable()) {
                wifiSwitchedTo3G = false;
                int wifiLevel = DownloadActivityInternal.getMainActivity().getWifiReceiver().getWifiLevel();
                String str = " " + Language.getString(16) + ": " + DownloadActivityInternal.getMainActivity().getWifiReceiver().getWifiName();
                if (!this.isLoadedBmpWifi.booleanValue()) {
                    str = str + " (" + Language.getString(35) + ": " + wifiLevel + "/" + 3 + ")";
                } else if (wifiLevel >= 0 && wifiLevel < 4) {
                    ((ImageView) this.dialog.findViewById(WIFI_LAYOUT_WIFI_VIEW_ID)).setImageBitmap(this.bmpWifi[wifiLevel]);
                }
                this.wifiTextView.setText(str);
                return;
            }
            int[] iArr = {-1};
            if (!alwaysSwitchTo3G && !wifiSwitchedTo3G && DownloadActivityInternal.getMainActivity().testNetwork(iArr) && iArr[0] == 0) {
                wifiSwitchedTo3G = true;
                showDialogSwitchTo3G = true;
                showDialogContent();
            }
            if (this.isLoadedBmpWifi.booleanValue()) {
                ((ImageView) this.dialog.findViewById(WIFI_LAYOUT_WIFI_VIEW_ID)).setImageBitmap(this.bmpWifi[0]);
            }
            this.wifiTextView.setText(Language.getString(16) + ": " + Language.getString(40));
        }
    }

    @Override // com.eamobile.views.IProgressDialog
    public void dismissDialog() {
        if (this.isLoadedBmpWifi.booleanValue()) {
            this.mainLayout.removeView((LinearLayout) this.dialog.findViewById(90));
        }
        if (this.bmpWifi != null) {
            for (int i = 0; i < 4; i++) {
                if (this.bmpWifi[i] != null) {
                    this.bmpWifi[i].recycle();
                    this.bmpWifi[i] = null;
                }
            }
            this.bmpWifi = null;
        }
        this.wifiTextView = null;
        if (this.alertDialog != null) {
            this.alertDialog.dismiss();
            this.alertDialog = null;
            this.exitConfirmation = false;
        }
        this.dialog.dismiss();
    }

    @Override // com.eamobile.views.IProgressDialog
    public void initDialog() {
        this.dialog = new Dialog(this.context);
        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.requestWindowFeature(1);
        this.mainLayout = new LinearLayout(this.context);
        this.mainLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        this.mainLayout.setOrientation(1);
        this.mainLayout.setGravity(16);
        addTitle(this.context, this.mainLayout, Language.getString(20));
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        linearLayout.setOrientation(1);
        linearLayout.setGravity(16);
        ScrollView scrollView = new ScrollView(this.context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        scrollView.setForegroundGravity(16);
        TextView textView = new TextView(this.context);
        textView.setId(1);
        textView.setClickable(false);
        textView.setCursorVisible(false);
        textView.setPadding(10, 2, 10, 18);
        textView.setTextSize(1, 16.0f);
        textView.setText("...");
        scrollView.addView(textView);
        this.customProgressBar = new CustomProgressBar(this.context);
        linearLayout.addView(scrollView);
        this.mainLayout.addView(this.customProgressBar);
        this.mainLayout.addView(linearLayout);
        LinearLayout linearLayout2 = new LinearLayout(this.context);
        linearLayout2.setId(90);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(19);
        if (this.isLoadedBmpWifi.booleanValue()) {
            ImageView imageView = new ImageView(this.context);
            imageView.setId(WIFI_LAYOUT_WIFI_VIEW_ID);
            imageView.setPadding(10, 0, 10, 10);
            imageView.setImageBitmap(null);
            imageView.setClickable(false);
            linearLayout2.addView(imageView);
        }
        this.wifiTextView = new TextView(this.context);
        this.wifiTextView.setId(WIFI_LAYOUT_WIFI_TEXTVIEW_ID);
        this.wifiTextView.setClickable(false);
        this.wifiTextView.setCursorVisible(false);
        this.wifiTextView.setTextSize(1, 16.0f);
        if (!this.isLoadedBmpWifi.booleanValue()) {
            this.wifiTextView.setPadding(10, 0, 10, 10);
        }
        linearLayout2.addView(this.wifiTextView);
        this.mainLayout.addView(linearLayout2);
    }

    @Override // com.eamobile.views.IProgressDialog
    public boolean isDialogValid() {
        return this.dialog != null;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void setDownloadMax(int i) {
        this.max = i;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void setDownloadProgress(int i) {
        this.progress = i;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void setDownloadSpeed(float f) {
        this.speed = f;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void showDialogContent() {
        if (this.dialog != null) {
            this.dialog.setContentView(this.mainLayout);
            this.dialog.show();
            updateWifiInfo();
            if (showDialogSwitchTo3G) {
                ZipExtractor.setPause(true);
                showDialogSwitchTo3G = false;
                String string = Language.getString(46);
                String string2 = Language.getString(47);
                String string3 = Language.getString(48);
                String string4 = Language.getString(49);
                this.alertDialog = new AlertDialog.Builder(this.context).create();
                this.alertDialog.setCancelable(true);
                this.alertDialog.setCanceledOnTouchOutside(false);
                this.alertDialog.setMessage(string);
                this.alertDialog.setButton(-1, string2, new DialogInterface.OnClickListener() {
                    /* class com.eamobile.views.CustomProgressDialog.AnonymousClass1 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        CustomProgressDialog.this.alertDialog.dismiss();
                        CustomProgressDialog.this.alertDialog = null;
                        CustomProgressDialog.this.exitConfirmation = false;
                        CustomProgressDialog.this.showDialogContent();
                        ZipExtractor.setPause(false);
                    }
                });
                this.alertDialog.setButton(-3, string3, new DialogInterface.OnClickListener() {
                    /* class com.eamobile.views.CustomProgressDialog.AnonymousClass2 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean unused = CustomProgressDialog.alwaysSwitchTo3G = true;
                        CustomProgressDialog.this.alertDialog.dismiss();
                        CustomProgressDialog.this.alertDialog = null;
                        CustomProgressDialog.this.exitConfirmation = false;
                        CustomProgressDialog.this.showDialogContent();
                        ZipExtractor.setPause(false);
                    }
                });
                this.alertDialog.setButton(-2, string4, new DialogInterface.OnClickListener() {
                    /* class com.eamobile.views.CustomProgressDialog.AnonymousClass3 */

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        DownloadActivityInternal.getMainActivity().startGameActivity(0);
                    }
                });
                this.alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    /* class com.eamobile.views.CustomProgressDialog.AnonymousClass4 */

                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        return i == 82 || i == 84 || i == 4;
                    }
                });
                this.alertDialog.show();
                this.exitConfirmation = true;
            }
            this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                /* class com.eamobile.views.CustomProgressDialog.AnonymousClass5 */

                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    return i == 82 || i == 84;
                }
            });
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                /* class com.eamobile.views.CustomProgressDialog.AnonymousClass6 */

                public void onCancel(DialogInterface dialogInterface) {
                    String string = Language.getString(41);
                    String string2 = Language.getString(17);
                    String string3 = Language.getString(18);
                    TextView textView = new TextView(CustomProgressDialog.this.context);
                    textView.setClickable(false);
                    textView.setCursorVisible(false);
                    textView.setSingleLine(false);
                    textView.setPadding(10, 10, 10, 10);
                    textView.setTextSize(1, 18.0f);
                    textView.setTextColor(-1);
                    textView.setText(string);
                    CustomProgressDialog.this.alertDialog = new AlertDialog.Builder(CustomProgressDialog.this.context).create();
                    CustomProgressDialog.this.alertDialog.setCancelable(true);
                    CustomProgressDialog.this.alertDialog.setCanceledOnTouchOutside(false);
                    CustomProgressDialog.this.alertDialog.setView(textView);
                    CustomProgressDialog.this.alertDialog.setButton(-1, string2, new DialogInterface.OnClickListener() {
                        /* class com.eamobile.views.CustomProgressDialog.AnonymousClass6.AnonymousClass1 */

                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            DownloadActivityInternal.getMainActivity().startGameActivity(0);
                        }
                    });
                    CustomProgressDialog.this.alertDialog.setButton(-2, string3, new DialogInterface.OnClickListener() {
                        /* class com.eamobile.views.CustomProgressDialog.AnonymousClass6.AnonymousClass2 */

                        public void onClick(DialogInterface dialogInterface, int i) {
                            CustomProgressDialog.this.alertDialog.dismiss();
                            CustomProgressDialog.this.alertDialog = null;
                            CustomProgressDialog.this.exitConfirmation = false;
                            CustomProgressDialog.this.showDialogContent();
                        }
                    });
                    CustomProgressDialog.this.alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        /* class com.eamobile.views.CustomProgressDialog.AnonymousClass6.AnonymousClass3 */

                        public void onCancel(DialogInterface dialogInterface) {
                            CustomProgressDialog.this.alertDialog.dismiss();
                            CustomProgressDialog.this.alertDialog = null;
                            CustomProgressDialog.this.exitConfirmation = false;
                            CustomProgressDialog.this.showDialogContent();
                        }
                    });
                    CustomProgressDialog.this.alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        /* class com.eamobile.views.CustomProgressDialog.AnonymousClass6.AnonymousClass4 */

                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                            return i == 82 || i == 84;
                        }
                    });
                    CustomProgressDialog.this.alertDialog.show();
                    CustomProgressDialog.this.exitConfirmation = true;
                }
            });
        }
        if (this.alertDialog != null && this.exitConfirmation) {
            this.alertDialog.show();
            this.dialog.hide();
        }
    }

    @Override // com.eamobile.views.IProgressDialog
    public void updateDialog() {
        if (this.max > 0) {
            float f = ((float) this.progress) / ((float) this.max);
            this.customProgressBar.setProgress(f);
            this.customProgressBar.invalidate();
            String replace = (((int) Math.floor((double) (100.0f * f))) + "%    " + Language.getString(36)).replace("%1", "" + this.progress).replace("%2", "" + this.max).replace("%3", "" + String.format("%.0f", Float.valueOf(this.speed)));
            updateWifiInfo();
            ((TextView) this.dialog.findViewById(1)).setText(replace);
        }
    }
}
