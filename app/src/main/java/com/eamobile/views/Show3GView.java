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

public class Show3GView extends CustomView {
    Dialog dialog;
    Button lskBtn;
    LinearLayout mainLayout;
    Button rskBtn;

    public Show3GView(Context context) {
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
        addTitle(this.context, this.mainLayout, Language.getString(39));
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
        textView.setText(Language.getString(24) + " " + Language.getString(29));
        scrollView.addView(textView);
        LinearLayout linearLayout2 = new LinearLayout(this.context);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        linearLayout2.setPadding(5, 0, 5, 5);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(80);
        this.lskBtn = addButton(this.context, linearLayout2, Language.getString(17));
        this.rskBtn = addButton(this.context, linearLayout2, Language.getString(6));
        linearLayout.addView(scrollView);
        this.mainLayout.addView(linearLayout);
        this.mainLayout.addView(linearLayout2);
    }

    private void showContent(View view) {
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.eamobile.views.Show3GView.AnonymousClass1 */

            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                DownloadActivityInternal.getMainActivity().setState(6);
            }
        });
        this.lskBtn.setOnClickListener(new View.OnClickListener() {
            /* class com.eamobile.views.Show3GView.AnonymousClass2 */

            public void onClick(View view) {
                Show3GView.this.dialog.dismiss();
                if (!DownloadActivityInternal.getMainActivity().test3GNetwork()) {
                    DownloadActivityInternal.getMainActivity().setState(7);
                } else if (DownloadActivityInternal.getTotalDownloadSizeMB() != 0) {
                    DownloadActivityInternal.getMainActivity().setState(2);
                } else {
                    DownloadActivityInternal.getMainActivity().setState(14);
                }
            }
        });
        this.rskBtn.setOnClickListener(new View.OnClickListener() {
            /* class com.eamobile.views.Show3GView.AnonymousClass3 */

            public void onClick(View view) {
                Show3GView.this.dialog.dismiss();
                DownloadActivityInternal.getMainActivity().startGameActivity(0);
            }
        });
        this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            /* class com.eamobile.views.Show3GView.AnonymousClass4 */

            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == 82 || i == 84;
            }
        });
        this.dialog.setContentView(this.mainLayout);
        this.dialog.show();
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
