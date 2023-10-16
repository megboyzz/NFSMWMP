package com.ea.ironmonkey.devmenu.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ea.games.nfs13_na.R;
import com.ea.ironmonkey.devmenu.util.SaveManager;

import java.io.File;
import java.util.Date;

/**
 * Диалог выбора и просмотра информации об svmw файле
 */
public class SvmwInspectorDialog extends AlertDialog {

    private SaveManager manager;
    private View mainView;
    private boolean isWork;
    private TextView description;
    private TextView time;

    public SvmwInspectorDialog(Context context, File svmw) {
        super(context);
        manager = new SaveManager(context);
        //Если пришедший файл - svmw иницализируем работу с ним если нет
        // то выходим
        // и ничего интересного не показываем((
        isWork = manager.isSvmwFile(svmw);
        if(isWork){

            setTitle("Файл - " + svmw.getName());

            mainView = LayoutInflater
                    .from(context)
                    .inflate(R.layout.inspector, null, false);

            description = (TextView) mainView.findViewById(R.id.description);
            description.setText(manager.getDescriptionOf(svmw));

            time = (TextView) mainView.findViewById(R.id.date);
            Date dateOfCreate = manager.getDateOfCreateOf(svmw);
            String format = SaveManager.dateFormat.format(dateOfCreate);
            time.setText(time.getText() + ": " + format);

            setButton(context.getString(R.string.title_load_svmw), (dialog, which) -> {
                manager.loadBundleFile(svmw);
            });
            setButton2(context.getString(R.string.cancel_title), (OnClickListener) null);

            setView(mainView);


        }else Toast.makeText(context, "Это не svmw!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void show() {
        if(isWork) super.show();
    }
}
