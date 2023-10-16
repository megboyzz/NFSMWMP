package com.ea.ironmonkey.devmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ea.games.nfs13_na.R;
import com.ea.ironmonkey.devmenu.util.ReplacementDataBaseHelper;
import com.ea.ironmonkey.devmenu.util.UtilitiesAndData;

import java.util.ArrayList;

public class RecoverListActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean flag = true;

        getActionBar().setTitle(R.string.recover_file_title);

        ListView view = new ListView(this);

        SQLiteDatabase database = new ReplacementDataBaseHelper(this).getDatabase();

        Cursor cursor = database.rawQuery("SELECT " + ReplacementDataBaseHelper.PATH_TO_REPLACED_ELEMENT + " FROM " + ReplacementDataBaseHelper.MAIN_TABLE_NAME, null);

        ArrayList<String> arrayList = new ArrayList<>();

        ArrayList<String> fullNames = new ArrayList<>();
        ArrayList<String> shortNames = new ArrayList<>();

        while (cursor.moveToNext()) {
            String string = cursor.getString(0);
            fullNames.add(string);
            int from = string.lastIndexOf("/files/");
            shortNames.add(string.substring(from));
        }
        if(fullNames.isEmpty()){
            shortNames.add("Не чего заменять!!");
            flag = false;
        }
        boolean thereIsSmthToRecover = flag;

        ArrayAdapter<String> adapter;

        view.setOnItemClickListener((parent, view1, position, id) -> {
            if(thereIsSmthToRecover){

                TextView textView = (TextView) view1;
                String s = textView.getText().toString();

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                //TODO сделать нормальные строки
                dialog.setTitle("Воостановить?");

                dialog.setPositiveButton(R.string.ok_title, (dialog1, which) -> {
                    shortNames.remove(s);
                    view.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shortNames));
                    String fullName = fullNames.get(position);
                    fullNames.remove(fullName);
                    UtilitiesAndData.recoverFile(fullName);
                    //System.out.println();
                    //TODO Сделать воостановление
                });

                dialog.setNegativeButton(R.string.cancel_title, (dialog1, which) -> {

                });

                dialog.show();
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shortNames);

        view.setAdapter(adapter);

        setContentView(view);
        cursor.close();

    }
}
