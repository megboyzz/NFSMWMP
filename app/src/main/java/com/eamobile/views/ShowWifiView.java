package com.eamobile.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.eamobile.DownloadActivityInternal;
import com.eamobile.Language;

public class ShowWifiView extends CustomView {
    Dialog dialog;
    Button lskBtn;
    LinearLayout mainLayout;
    Button midBtn;
    Button rskBtn;

    public ShowWifiView(Context context) {
        super(context);
        this.context = context;
    }

    private void createContent(View view) {
        this.dialog = new Dialog(this.context);
        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.requestWindowFeature(1);
        this.mainLayout = new LinearLayout(this.context);
        this.mainLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        this.mainLayout.setOrientation(1);
        this.mainLayout.setGravity(16);
        addTitle(this.context, this.mainLayout, Language.getString(7));
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        linearLayout.setOrientation(1);
        linearLayout.setGravity(16);
        ScrollView scrollView = new ScrollView(this.context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        scrollView.setForegroundGravity(16);
        TextView textView = new TextView(this.context);
        textView.setClickable(false);
        textView.setCursorVisible(false);
        textView.setPadding(10, 10, 10, 10);
        textView.setTextSize(1, 16.0f);
        if (DownloadActivityInternal.getMainActivity().isAmazonDevice()) {
            textView.setText(Language.getString(32));
        } else if (DownloadActivityInternal.getMainActivity().is3GDisabled()) {
            textView.setText(Language.getString(8));
        } else {
            textView.setText(Language.getString(8) + " " + Language.getString(31));
        }
        scrollView.addView(textView);
        LinearLayout linearLayout2 = new LinearLayout(this.context);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        linearLayout2.setPadding(5, 0, 5, 5);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(80);
        if (!DownloadActivityInternal.getMainActivity().isAmazonDevice()) {
            this.lskBtn = addButton(this.context, linearLayout2, Language.getString(16));
            if (!DownloadActivityInternal.getMainActivity().is3GDisabled()) {
                this.midBtn = addButton(this.context, linearLayout2, Language.getString(23));
            }
        }
        this.rskBtn = addButton(this.context, linearLayout2, Language.getString(6));
        linearLayout.addView(scrollView);
        this.mainLayout.addView(linearLayout);
        this.mainLayout.addView(linearLayout2);
    }

    private void showContent(View view) {
        this.dialog.setContentView(this.mainLayout);
        this.dialog.show();
        this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            /* class com.eamobile.views.ShowWifiView.AnonymousClass1 */

            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == 82 || i == 84;
            }
        });
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.eamobile.views.ShowWifiView.AnonymousClass2 */

            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                DownloadActivityInternal.getMainActivity().setState(1);
            }
        });
        if (!DownloadActivityInternal.getMainActivity().isAmazonDevice()) {
            this.lskBtn.setOnClickListener(new View.OnClickListener() {
                /* class com.eamobile.views.ShowWifiView.AnonymousClass3 */

                public void onClick(View view) {
                    ShowWifiView.this.dialog.dismiss();
                    DownloadActivityInternal.getMainActivity().startWifiManager();
                }
            });
            if (!DownloadActivityInternal.getMainActivity().is3GDisabled()) {
                this.midBtn.setOnClickListener(new View.OnClickListener() {
                    /* class com.eamobile.views.ShowWifiView.AnonymousClass4 */

                    public void onClick(View view) {
                        ShowWifiView.this.dialog.dismiss();
                        DownloadActivityInternal.getMainActivity().setState(10);
                    }
                });
            }
        }
        this.rskBtn.setOnClickListener(new View.OnClickListener() {
            /* class com.eamobile.views.ShowWifiView.AnonymousClass5 */

            public void onClick(View view) {
                ShowWifiView.this.dialog.dismiss();
                DownloadActivityInternal.getMainActivity().startGameActivity(0);
            }
        });
    }

    @Override // com.eamobile.views.CustomView, com.eamobile.views.IDownloadView
    public void clean() {
        super.clean();
        try {
            this.dialog.dismiss();
        } catch (Exception e) {
        }
    }

    @Override // com.eamobile.views.CustomView, com.eamobile.views.IDownloadView
    public void init() {
        super.init();
        createContent(this);
        showContent(this);
    }

    @Override // com.eamobile.views.CustomView
    public void resume() {
        if (this.dialog != null) {
            showContent(this);
        }
    }
}
