package com.ea.ironmonkey.devmenu.components;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** Динамический список для контекстного меню файла LongPressContextMenu */
public class DynamicOptionsListView extends ListView {

    /** Названия позиций в контекстном меню */
    private List<String> names = new ArrayList<>();
    private List<OptionAction> actions = new ArrayList<>();
    private Context context;

    public DynamicOptionsListView(Context context) {
        super(context);
        this.context = context;
        updateList();
        setOnItemClickListener((parent, view, position, id) -> {
            try {
                actions.get(position).action();
            }catch (IndexOutOfBoundsException e){
                Log.i("DynamicListView", "No found action to do( in position " + position);
            }
        });
    }

    private void updateList(){
        setAdapter(
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_list_item_1,
                        names
                ));
    }

    public void addOption(String title, OptionAction action){
        names.add(title);
        actions.add(action);
        updateList();
    }

    public void deleteOption(String title){
        boolean removeInt = names.remove(title);
        actions.remove(removeInt);
        updateList();
    }

    public String deleteOption(int position){
        String result = names.remove(position);
        actions.remove(position);
        updateList();
        return result;
    }
}
