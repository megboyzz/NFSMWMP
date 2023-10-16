package com.ea.ironmonkey.devmenu;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.ea.games.nfs13_na.BuildConfig;
import com.ea.games.nfs13_na.R;
import com.ea.ironmonkey.devmenu.dialog.OpenFileDialog;
import com.ea.ironmonkey.devmenu.util.SaveManager;
import com.ea.ironmonkey.devmenu.dialog.SvmwCreatorDialog;
import com.ea.ironmonkey.devmenu.dialog.SvmwInspectorDialog;
import com.ea.ironmonkey.devmenu.util.UtilitiesAndData;
import com.ea.nimble.ApplicationLifecycle;

import java.io.File;

public class SettingsActivity extends PreferenceActivity {

    public static final String LOG_TAG = "SettingActivity";

    private static final int PICKFILE_REQUEST_CODE = 128;
    public static final int PICK_SVMW_REQUEST_CODE = 129;
    public static final int PICK_SVMW_IN_CREATE = 228;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_xml);
        
        ApplicationLifecycle.onActivityCreate(savedInstanceState, this);

        String title = String.format(getString(R.string.dev_menu_title), BuildConfig.DEV_MENU_VERSION);
        getActionBar().setTitle(title);

        Preference chooseSaveFileButton = findPreference(getString(R.string.choose_save_file_title));
        Preference chooseSVMWfileButton = findPreference(getString(R.string.choose_svmw_file_title));
        Preference createSVMWfileButton = findPreference(getString(R.string.create_svmw_file_title));
        Preference turnOffTheDevMenuButton = findPreference(getString(R.string.switch_off_devmenu_title));

        final Context myContext = this;

        turnOffTheDevMenuButton.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.switch_off_devmenu_title);
            builder.setMessage(R.string.msg_devmenu_off);
            builder.setPositiveButton(R.string.ok_title, (dialog, which) -> UtilitiesAndData.getDevMenuSwitcher().delete());
            builder.setNegativeButton(R.string.cancel_title, null);
            builder.show();
            return true;
        });

        chooseSaveFileButton.setOnPreferenceClickListener(preference -> {

            OpenFileDialog fileDialog = new OpenFileDialog(myContext);
            fileDialog
                    .setFilter(".*\\.sb")
                    .setOpenDialogListener(fileName -> {

                        File save = new File(fileName);
                        SaveManager manager = new SaveManager(this);
                        manager.loadSaveFile(save);

                    });

            fileDialog.show();

            return true;
        });

        chooseSVMWfileButton.setOnPreferenceClickListener(preference -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            startActivityForResult(intent, PICK_SVMW_REQUEST_CODE);
            //TODO реализовать выбор svmw
            return true;
        });

        //По нажатии на кнопку создания svmw файла осуществляется переход в диалог создания svmw
        createSVMWfileButton.setOnPreferenceClickListener(preference -> {

            SvmwCreatorDialog dialog = new SvmwCreatorDialog(this);
            dialog.show();

            return true;
        });



        getActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            switch (requestCode) {
                case PICKFILE_REQUEST_CODE: {
                    String s = data.getData().toString();
                    String s1 = s.replaceAll("file://", "");
                    File file = new File(s1);
                }
                    break;
                case PICK_SVMW_REQUEST_CODE: {
                    String s = data.getData().toString();
                    String s1 = s.replaceAll("file://", "");
                    File file = new File(s1);
                    SvmwInspectorDialog inspectorDialog = new SvmwInspectorDialog(this, file);
                    inspectorDialog.show();
                }
                    break;
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
