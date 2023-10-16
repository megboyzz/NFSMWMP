package com.ea.ironmonkey.devmenu;

import static com.ea.ironmonkey.devmenu.util.ReplacementDataBaseHelper.MAIN_TABLE_NAME;
import static com.ea.ironmonkey.devmenu.util.ReplacementDataBaseHelper.PATH_TO_REPLACED_ELEMENT;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.ea.ironmonkey.devmenu.util.ReplacementDataBaseHelper;
import com.ea.ironmonkey.devmenu.util.UtilitiesAndData;

import java.io.File;
import java.util.List;

class FileAdapter extends ArrayAdapter<File> {

    private static int count = 0;
    private List files;
    private Context context;
    private ReplacementDataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;

    public FileAdapter(Context context, List files) {
        super(context, android.R.layout.simple_list_item_2, files);
        dataBaseHelper = new ReplacementDataBaseHelper(context);
        database = dataBaseHelper.getDatabase();
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        String name = getItem(position).getName();

        Cursor query = database.query(MAIN_TABLE_NAME, new String[]{PATH_TO_REPLACED_ELEMENT},
                PATH_TO_REPLACED_ELEMENT + " = \"" + getItem(position).getAbsolutePath() + "\""
                , null, null, null, null);

        if (query.getCount() > 0) {
            TwoLineListItem listItem = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null, true);
            listItem.getText1().setText(name);
            listItem.getText2().setText("Заменен");
            view = listItem;
        } else {
            TextView textView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null, true);
            textView.setText(name);
            view = textView;
        }
        query.close();
        return view;
    }

    public List getFiles() {
        return files;
    }
}
