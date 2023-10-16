package com.ea.ironmonkey.devmenu;

import static com.ea.ironmonkey.devmenu.util.UtilitiesAndData.OPEN_FILE_ON_REPLACE_REQUEST;
import static com.ea.ironmonkey.devmenu.util.UtilitiesAndData.copy;
import static com.ea.ironmonkey.devmenu.util.UtilitiesAndData.generateMD5;
import static com.ea.ironmonkey.devmenu.util.UtilitiesAndData.isFirstRun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.ea.games.nfs13_na.BuildConfig;
import com.ea.games.nfs13_na.R;
import com.ea.ironmonkey.GameActivity;
import com.ea.ironmonkey.devmenu.components.LongPressContextMenu;
import com.ea.ironmonkey.devmenu.util.ResultListener;
import com.ea.ironmonkey.devmenu.util.UtilitiesAndData;
import com.ea.nimble.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

//TODO сделать нормальный файл сохранения
//TODO сделать его нрмальное отображние

//TODO сделать нормальное отслеживние файлов сохранений
//TODO сдлеать настройки отслеживания файла
//TODO сделать отображение текущего пути в проводнике
//TODO добавить иконки к проводику
//TODO сделать динамическое контекстное меню файла

//TODO реализовать сохранение файлов в память телефона из внутреннего хранилища
public class MainActivity extends Activity{

    private final String LOG_TAG = "InjectedActivity";

    private String internalFiles;
    private String externalFiles;
    private ResultListener resultListener;
    private ResultListener openResult = new ResultListener() {};
    private static Thread observerThread;
    private String globalPath = "";
    private ListView fileList;
    private Button backButton;
    private static final int READ_FILE_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilitiesAndData.init(this);
        internalFiles = UtilitiesAndData.getInternalStorage();
        externalFiles = UtilitiesAndData.getExternalStorage();

        File replacements = new File(UtilitiesAndData.getReplacementsStorage());
        if(!replacements.exists()) replacements.mkdir();

        File activityFlag = new File(externalFiles + File.separator + BuildConfig.DEV_MENU_ID);
        // TODO доделать проверку первого запуска
        if(isFirstRun()){
            File data = new File(UtilitiesAndData.getExternalStorage());
            if(!data.exists()){
                
                File data1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getPackageName() + "_");
                if(data1.exists()) {
                    String path = data1.getPath();
                    data1.renameTo(new File(path.substring(0, path.length() - 2)));
                    activityFlag = data1;
                }
            }
            try {
                File temp = new File(UtilitiesAndData.getInternalStorage() + File.separator + "load");
                temp.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!activityFlag.exists()){
            updateLanguage();
            runGame();
            return;
        }

        setContentView(R.layout.custom);


        String title = String.format(getString(R.string.dev_menu_title), /*BuildConfig.DEV_MENU_VERSION*/"");

        getActionBar().setTitle(title);

        fileList = (ListView) findViewById(R.id.FileList);

        fileList.setAdapter(new FileAdapter(this, asList(externalFiles)));
        globalPath = externalFiles;

        RadioGroup group = (RadioGroup) findViewById(R.id.switcherFiles);

        fileList.setOnItemClickListener((parent, view, position, id) -> {
            String chosenElem =
                    (view instanceof TwoLineListItem) ?
                            ((TwoLineListItem) view).getText1().getText().toString() :
                            ((TextView) view).getText().toString(); // получаем текст нажатого элемента

            File intermid =  new File(globalPath + "/" + chosenElem);
            if(intermid.isDirectory()) {
                globalPath += "/" + chosenElem;
                updateListView();
            }
            else{
                openFile(intermid);
            }

        });

        fileList.setOnItemLongClickListener((parent, view, position, id) -> {
            String chosenElem =
                    (view instanceof TwoLineListItem) ?
                            ((TwoLineListItem) view).getText1().getText().toString() :
                            ((TextView) view).getText().toString();


            LongPressContextMenu ninja = new LongPressContextMenu(this, globalPath + "/" + chosenElem);
            return true;
        });

        group.setOnCheckedChangeListener((group1, checkedId) -> {
            globalPath = (checkedId == R.id.externalStoreButton) ? externalFiles : internalFiles;
            updateListView();
        });

        backButton = (Button)findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> {
            if(!( globalPath.equals(internalFiles) | globalPath.equals(externalFiles) )
                    & !globalPath.isEmpty()
                    & (new File(globalPath).exists())) {
                globalPath = globalPath.substring(0, globalPath.lastIndexOf("/"));
                updateListView();
            }
        });


        //Настройка языка игры
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    public void readAndSortNumbersFromFile(String fileName) {
        List<Integer> numbersList = new ArrayList<>();

        try {
            File file = new File(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    // Попытайтесь преобразовать строку в целое число и добавить его в список
                    int number = Integer.parseInt(line);
                    numbersList.add(number);
                } catch (NumberFormatException e) {
                    // Если строка не является числом, проигнорируйте ее
                    Log.e("FileOperations", "Ошибка при чтении числа: " + line);
                }
            }

            bufferedReader.close();

            // Отсортируйте числа в списке
            Collections.sort(numbersList);

        } catch (IOException e) {
            Log.e("Time", "Ошибка при чтении файла: " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        backButton.callOnClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case OPEN_FILE_ON_REPLACE_REQUEST:{
                resultListener.onResult(data);
            }break;

            case READ_FILE_REQUEST_CODE:{
                openResult.onResult(data);
            }
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.optionRunTheGame) {
            updateLanguage();
            runGame();
        } else if (itemId == R.id.optionSettings) {
            Intent goToSettings = new Intent(this, SettingsActivity.class);
            startActivity(goToSettings);
        } else if (itemId == R.id.optionDeleteData) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.remove_action_title));
            builder.setMessage(getString(R.string.sure_remove_all_data_title));
            builder.setPositiveButton(R.string.ok_title, (dialogInterface, i) -> {
                File[] internals = new File(UtilitiesAndData.getInternalStorage()).listFiles();
                for (File internal : internals) {
                    if (!UtilitiesAndData.isExclusionName(internal.getName())) {
                        internal.delete();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel_title, null);
            builder.show();
        } else if (itemId == R.id.optionCheckRecovers) {
            Intent goToRecovers = new Intent(this, RecoverListActivity.class);
            startActivity(goToRecovers);
        }

        return super.onOptionsItemSelected(item);
    }

    public void openFile(File url) {
        File tempFile = null;
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if(url.getAbsolutePath().contains(UtilitiesAndData.getInternalStorage())){
            Log.wtf(LOG_TAG, "WTF, man, you cant read my files!!!");

            //Создаем временный файл, там где можем его прочитать
            Random random = new Random();
            tempFile = new File(UtilitiesAndData.getExternalStorage() + File.separator + "temp_" + random.nextInt());

            //Копируем тот файл который хотим посмотреть
            copy(url.getAbsolutePath(), tempFile.getAbsolutePath());

            //Сохраняем ссылку на окрытый файл, в случае его изменения
            final File openedFile = url;
            url = tempFile;
            File finalTempFile = tempFile;

            //Создаем хеш файла для того чтобы его потом сравнить
            final byte[] compTemp = generateMD5(finalTempFile);

            openResult = new ResultListener(){
                @Override
                public void onResult(Object data) {
                    byte[] bytes = generateMD5(finalTempFile);
                    //Если хеши не одинаковы то заменяем одно на другое
                    if(!Arrays.equals(bytes, compTemp))
                        copy(finalTempFile.getAbsolutePath(), openedFile.getAbsolutePath());
                    finalTempFile.delete();
                }
            };
            intent.putExtra("pathToTemp", tempFile.getAbsolutePath());
        }
        // Create URI
        Uri uri = Uri.fromFile(url);

        if (url.toString().contains(".doc") || url.toString().contains(".docx"))
            intent.setDataAndType(uri, "application/msword");
        else if(url.toString().contains(".pdf")) {
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            intent.setDataAndType(uri, "application/x-wav");
        } else if(url.toString().contains(".rtf")) {
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            intent.setDataAndType(uri, "video/*");
        } else {
            intent.setDataAndType(uri, "*/*");
        }

        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, READ_FILE_REQUEST_CODE);

    }

    private void runGame() {

        Intent GoToGame = new Intent(this, GameActivity.class);
        startActivity(GoToGame);

    }

    // TODO Сделать номальную систему учета измения файлов
    public static void observ(){
        File save = new File(UtilitiesAndData.getInternalStorage() + File.separator + "files/var/nfstr_save.sb");
        File fileOut = new File(UtilitiesAndData.getExternalStorage() + File.separator + "Log.txt");
        File pathToSave = new File(UtilitiesAndData.getExternalStorage() + File.separator + "saves");
        pathToSave.mkdir();
        if(!fileOut.exists()) {
            try {
                fileOut.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
        UtilitiesAndData.setLogger(fileOut);
        observerThread = new Thread(() -> {
            int count = 1;
            byte[] lastMD5 = new byte[10];
            while (true){
                byte[] md5 = generateMD5(save);
                if(!Arrays.equals(md5, lastMD5)) {
                    UtilitiesAndData.printLog(format.format(new Date()) + " | " + Utility.bytesToHexString(md5) + "\n");
                    File change = new File(pathToSave.getAbsolutePath() + File.separator + "nfs_save_change_"+ count +".sb");
                    try {
                        change.createNewFile();
                        copy(save, change);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                lastMD5 = md5;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
            }
        });
        observerThread.start();
    }

    private <T> List<T> asList(T[] a){
        return Arrays.asList(a);
    }

    // TODO реализовать сокрытие лишних папок
    private List<File> asList(String path){
        return asList(new File(path).listFiles());
    }

    private void updateLanguage(){
        //Получаем текущий язык
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String current_lang = preferences.getString(getString(R.string.current_lang), "00");
        if(current_lang.equals("00")) {
            Log.e(LOG_TAG, "Not found currentLang preference(");
            return;
        }
        if(current_lang.equals("sys"))
            current_lang = Locale.getDefault().getLanguage();

        byte[] current_lang_bytes = current_lang.getBytes();

        //Открываем языковой файл и создаем поток чтения
        File locale = new File(internalFiles + "/files/var/locale");
        FileInputStream inputStream;

        //Байтовое представление файла
        byte[] bytes_locale = new byte[4];
        try {

            inputStream = new FileInputStream(locale);
            inputStream.read(bytes_locale);

        } catch (FileNotFoundException e) {
            Log.wtf(LOG_TAG, "No found locale(((((");
            return;
        }catch (IOException e){
            Log.wtf(LOG_TAG, "Couldn't read the locale file((((((");
            return;
        }
        try {
            if (
                    bytes_locale[2] != current_lang_bytes[0] &
                            bytes_locale[3] != current_lang_bytes[1]
            ) {
                bytes_locale[2] = current_lang_bytes[0];
                bytes_locale[3] = current_lang_bytes[1];
            } else return;
        }catch (Exception e){
            return;
        }

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(locale, false);
            outputStream.write(bytes_locale, 0, 4);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void setResultListener(ResultListener resultListener) {
        this.resultListener = resultListener;
    }

    public void updateListView(){
        fileList.setAdapter(new FileAdapter(getApplicationContext(), asList(globalPath)));
    }

}