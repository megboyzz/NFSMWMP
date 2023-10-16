package com.ea.ironmonkey.devmenu.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ea.games.nfs13_na.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OpenFileDialog extends AlertDialog.Builder {

    private String currentPath = Environment.getExternalStorageDirectory().getPath();
    private FilenameFilter filenameFilter;
    private List<File> files = new ArrayList<File>();
    private TextView title;
    private ListView listView;
    private int selectedIndex = -1;

    public OpenFileDialog(Context context) {
        super(context);
        title = createTitle(context);
        changeTitle();
        LinearLayout linearLayout = createMainLayout(context);
        linearLayout.addView(createBackItem(context));
        files.addAll(getFiles(currentPath));
        listView = createListView(context);
        listView.setAdapter(new FileAdapter(context, files));
        linearLayout.addView(listView);
        setCustomTitle(title)
                .setView(linearLayout)
                .setPositiveButton(R.string.ok_title, (dialog, which) -> {
                    if (selectedIndex > -1 && listener != null) {
                        listener.OnSelectedFile(listView.getItemAtPosition(selectedIndex).toString());
                    }
                })
                .setNegativeButton(R.string.cancel_title, null);
    }

    @Override
    public AlertDialog show() {
        files.addAll(getFiles(currentPath));
        listView.setAdapter(new FileAdapter(getContext(), files));
        return super.show();
    }

    private <T> List<T> asList(T[] a){
        return Arrays.asList(a);
    }

    private List<File> getFiles(String directoryPath){
        File directory = new File(directoryPath);
        List<File> fileList = asList(directory.listFiles(filenameFilter));
        Collections.sort(fileList, (file, file2) -> {
            if (file.isDirectory() && file2.isFile())
                return -1;
            else if (file.isFile() && file2.isDirectory())
                return 1;
            else
                return file.getPath().compareTo(file2.getPath());
        });
        return fileList;
    }

    private TextView createTextView(Context context, int style) {
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, style);
        int itemHeight = getItemHeight(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        textView.setMinHeight(itemHeight);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(15, 0, 0, 0);
        return textView;
    }

    private  int getItemHeight(Context context) {
        TypedValue value = new TypedValue();
        DisplayMetrics metrics = new DisplayMetrics();
        context.getTheme().resolveAttribute(android.R.attr.rowHeight, value, true);
        getDefaultDisplay(context).getMetrics(metrics);
        return (int)TypedValue.complexToDimension(value.data, metrics);
    }

    public int getTextWidth(String text, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.left + bounds.width() + 80;
    }

    private void changeTitle() {
        String titleText = currentPath;
        int screenWidth = getScreenSize(getContext()).x;
        int maxWidth = (int) (screenWidth * 0.99);
        if (getTextWidth(titleText, title.getPaint()) > maxWidth) {
            while (getTextWidth("..." + titleText, title.getPaint()) > maxWidth)
            {
                int start = titleText.indexOf("/", 2);
                if (start > 0)
                    titleText = titleText.substring(start);
                else
                    titleText = titleText.substring(2);
            }
            title.setText("..." + titleText);
        } else {
            title.setText(titleText);
        }
    }

    private TextView createTitle(Context context) {
        TextView textView = new TextView(context);
        textView.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_DialogWindowTitle);
        int itemHeight = getItemHeight(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));
        textView.setMinHeight(itemHeight);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(15, 0, 0, 0);
        textView.setText(currentPath);
        return textView;
    }

    private void RebuildFiles(ArrayAdapter<File> adapter) {
        try{
            List<File> fileList = getFiles(currentPath);
            files.clear();
            selectedIndex = -1;
            files.addAll(fileList);
            adapter.notifyDataSetChanged();
            changeTitle();
        } catch (NullPointerException e){
            Toast.makeText(getContext(), android.R.string.unknownName, Toast.LENGTH_SHORT).show();
        }
    }

    public OpenFileDialog setFilter(final String filter) {
        filenameFilter = (file, fileName) -> {
            File tempFile = new File(String.format("%s/%s", file.getPath(), fileName));
            if (tempFile.isFile())
                return tempFile.getName().matches(filter);
            return true;
        };
        return this;
    }

    private ListView createListView(Context context) {
        ListView listView = new ListView(context);
        listView.setOnItemClickListener((adapterView, view, index, l) -> {
            FileAdapter adapter = (FileAdapter) adapterView.getAdapter();
            File file = adapter.getItem(index);
            if (file.isDirectory()) {
                currentPath = file.getPath();
                RebuildFiles(adapter);
            } else {
                if (index != selectedIndex)
                    selectedIndex = index;
                else
                    selectedIndex = -1;
                adapter.notifyDataSetChanged();
            }
        });
        return listView;
    }

    private static Display getDefaultDisplay(Context context) {
        return ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    private static Point getScreenSize(Context context) {
        Point screeSize = new Point();
        getDefaultDisplay(context).getSize(screeSize);
        return screeSize;
    }

    private LinearLayout createMainLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(750);
        return linearLayout;
    }

    private TextView createBackItem(Context context) {
        TextView textView = createTextView(context, android.R.style.TextAppearance_DeviceDefault_Small);
        Drawable drawable = getContext().getResources().getDrawable(android.R.drawable.ic_menu_directions);
        drawable.setBounds(0, 0, 60, 60);
        textView.setCompoundDrawables(drawable, null, null, null);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setOnClickListener(view -> {
            File file = new File(currentPath);
            File parentDirectory = file.getParentFile();
            if (parentDirectory != null) {
                currentPath = parentDirectory.getPath();
                RebuildFiles(((FileAdapter) listView.getAdapter()));
            }
        });
        return textView;
    }

    class FileAdapter extends ArrayAdapter<File> {

        public FileAdapter(Context context, List files) {
            super(context, android.R.layout.simple_list_item_1, files);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            view.setText(file.getName());
            if (selectedIndex == position)
                view.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_light));
            else
                view.setBackgroundColor(getContext().getResources().getColor(android.R.color.background_dark));
            return view;
        }
    }

    public interface OpenDialogListener{
        void OnSelectedFile(String fileName);
    }
    private OpenDialogListener listener;

    public OpenFileDialog setOpenDialogListener(OpenDialogListener listener) {
        this.listener = listener;
        return this;
    }
}
