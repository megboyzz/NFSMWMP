package com.ea.ironmonkey.devmenu.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Менедженер создания и загрузки сохранений в игру<br/>
 * Создает bundle-файлы .svmw и загружает их<br/>
 * Умеет загружать обчные .sb сохранения в игру
 */
public class SaveManager {

    private Context context;
    private static final String LOG_TAG = "SaveManager";
    private static final byte[] svmw_header = "SVMW".getBytes(StandardCharsets.UTF_8);
    private static final byte[] save_header = "SBIN".getBytes(StandardCharsets.UTF_8);
    public static final String dateFormatStr = "dd.MM.yy:hh:mm:ss";
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);

    public SaveManager(Context context) {
        this.context = context;
    }

    public File createBundleFile(String description, File fileToSave, File save) {

        if (fileToSave.exists()) fileToSave.delete();

        try {
            fileToSave.createNewFile();
            FileOutputStream fos = new FileOutputStream(fileToSave);

            Date date = new Date();

            Long time = date.getTime();

            ByteBuffer bb = ByteBuffer.allocate(Long.SIZE);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putLong(time);
            bb.flip();
            //bb.
            String curDate = dateFormat.format(new Date());

            fos.write(svmw_header);
            fos.write(curDate.getBytes(StandardCharsets.UTF_8));
            fos.write(description.getBytes(StandardCharsets.UTF_8));
            fos.write(UtilitiesAndData.fileAsByteArray(save));
        } catch (IOException e) {
            Log.i("lol", fileToSave.getAbsolutePath());
            //fileToSave.getAbsolutePath()
            e.printStackTrace();
        }

        return fileToSave;
    }

    public void loadBundleFile(File svmw) {

        if(isSvmwFile(svmw)){
            byte[] byteFile = UtilitiesAndData.fileAsByteArray(svmw);
            int headerInByteFile = UtilitiesAndData.findHeaderInByteFile(byteFile, save_header);
            byte[] result = new byte[byteFile.length - headerInByteFile];
            System.arraycopy(byteFile, headerInByteFile, result, 0, result.length);
            File dest = new File("/data/data/" + context.getPackageName() + "/files/var/nfstr_save.sb");
            if(!dest.exists()) {
                try {
                    dest.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(dest);
                fos.write(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public String getDescriptionOf(File svmw){
        if(isSvmwFile(svmw)){
            int offset = svmw_header.length + dateFormatStr.length();
            byte[] byteFile = UtilitiesAndData.fileAsByteArray(svmw);
            int headerInByteFile = UtilitiesAndData.findHeaderInByteFile(byteFile, save_header);
            return new String(byteFile, offset, headerInByteFile - offset, StandardCharsets.UTF_8);
        }
        Log.i(LOG_TAG, "getDescription(), this is not a svmw file((( Return empty description(((");
        return "";
    }

    public Date getDateOfCreateOf(File svmw){
        if(isSvmwFile(svmw)){
            int offset = svmw_header.length;
            byte[] byteFile = UtilitiesAndData.fileAsByteArray(svmw);

            String s = new String(byteFile, offset, dateFormatStr.length(), StandardCharsets.UTF_8);

            try {
                return dateFormat.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Log.i(LOG_TAG, "getDateOfCreateOf, this is not a svmw file((( Return null date(((");
        return null;
    }

    public void loadSaveFile(File save) {
        if(isSaveFile(save)){
            copySave(save);
        }
    }

    public boolean isSaveFile(File save) {
        if(!save.exists() | isEmptyFile(save)) return false;
        byte[] arr = UtilitiesAndData.fileAsByteArray(save);
        for (int i = 0; i < save_header.length; i++)
            if (save_header[i] != arr[i]) return false;
        return true;
    }

    public boolean isSvmwFile(File svmw) {
        if(!svmw.exists() | isEmptyFile(svmw)) return false;
        byte[] arr = UtilitiesAndData.fileAsByteArray(svmw);
        for (int i = 0; i < svmw_header.length; i++)
            if (svmw_header[i] != arr[i]) return false;
        return true;
    }


    public boolean isEmptyFile(File file){
        return UtilitiesAndData.fileAsByteArray(file).length == 0;
    }


    public void copySave(File save){

        File source = save;

        File dest = new File("/data/data/" + context.getPackageName() + "/files/var/nfstr_save.sb");


        if(!dest.exists()) {
            try {
                dest.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            is.close();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
