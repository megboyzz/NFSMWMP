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

public class UpdatesFoundView extends CustomView {
    Dialog dialog;
    boolean localAssetsOK = true;
    Button lskBtn;
    LinearLayout mainLayout;
    Button rskBtn;

    public UpdatesFoundView(Context context) {
        super(context);
        this.context = context;
    }

    private void createContent(View view) {
        this.localAssetsOK = DownloadActivityInternal.getMainActivity().checkLocalAssetVersion();
        this.dialog = new Dialog(this.context);
        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.requestWindowFeature(1);
        this.mainLayout = new LinearLayout(this.context);
        this.mainLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        this.mainLayout.setOrientation(1);
        this.mainLayout.setGravity(16);
        addTitle(this.context, this.mainLayout, Language.getString(25));
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
        String string = Language.getString(26, new String[]{DownloadActivityInternal.getMainActivity().getApplicationName(), DownloadActivityInternal.getMainActivity().getSpaceRangeForDownload()});
        String string2 = Language.getString(44);
        if (!this.localAssetsOK) {
            string = string + "\n" + string2;
        }
        textView.setText(string);
        scrollView.addView(textView);
        LinearLayout linearLayout2 = new LinearLayout(this.context);
        linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1.0f));
        linearLayout2.setPadding(5, 0, 5, 5);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(80);
        this.lskBtn = addButton(this.context, linearLayout2, Language.getString(5));
        if (this.localAssetsOK) {
            this.rskBtn = addButton(this.context, linearLayout2, Language.getString(18));
        } else {
            this.rskBtn = addButton(this.context, linearLayout2, Language.getString(6));
        }
        linearLayout.addView(scrollView);
        this.mainLayout.addView(linearLayout);
        this.mainLayout.addView(linearLayout2);
    }

    private void showContent(View view) {
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            /* class com.eamobile.views.UpdatesFoundView.AnonymousClass1 */

            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                DownloadActivityInternal.getMainActivity().setState(3);
            }
        });
        this.lskBtn.setOnClickListener(new View.OnClickListener() {
            /* class com.eamobile.views.UpdatesFoundView.AnonymousClass2 */

            public void onClick(View view) {
                UpdatesFoundView.this.dialog.dismiss();
                DownloadActivityInternal.getMainActivity().updateDownload();
            }
        });
        this.rskBtn.setOnClickListener(new View.OnClickListener() {
            /* class com.eamobile.views.UpdatesFoundView.AnonymousClass3 */

            public void onClick(View view) {
                UpdatesFoundView.this.dialog.dismiss();
                if (UpdatesFoundView.this.localAssetsOK) {
                    DownloadActivityInternal.getMainActivity().setState(11);
                } else {
                    DownloadActivityInternal.getMainActivity().startGameActivity(0);
                }
            }
        });
        this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            /* class com.eamobile.views.UpdatesFoundView.AnonymousClass4 */

            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == 82 || i == 84 || i == 4;
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
