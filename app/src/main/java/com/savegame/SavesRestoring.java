package com.savegame;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class SavesRestoring extends Activity {
    private static int PdsjdolaSd = 0;
    private static int daDakdsIID = 0;

    public static void DoSmth(Context context) {
        try {
            SmartDataRestoreForYou(context, context.getAssets(), context.getPackageName());
        } catch (Exception e) {
            Log.e(context.getPackageName() + ":savemessages", "Message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean ExistsInArray(String[] strArr, String str) {
        for (int i = 0; i < strArr.length; i++) {
            if (strArr[i].contains(str)) {
                return true;
            }
        }
        return false;
    }

    private static String IcvaeLIJAFL() {
        daDakdsIID++;
        return Character.toString('b');
    }

    private static String JaEBhFLsj() {
        daDakdsIID++;
        return Character.toString('s');
    }

    private static String MNTRGWO() {
        daDakdsIID++;
        return Character.toString('o');
    }

    private static String SWoFpOGUOR() {
        daDakdsIID++;
        return Character.toString('m');
    }

    private static void SmartDataRestoreForYou(Context context, AssetManager assetManager, String str) throws Exception {
        if (!context.getSharedPreferences("savegame", 0).getBoolean("notfirst", false)) {
            context.getSharedPreferences("savegame", 0).edit().putBoolean("notfirst", true).commit();
            String str2 = str + ":savemessages";
            Log.i(str2, "SmDR: Starting...");
            context.getSharedPreferences("savegame", 0).edit().putBoolean("notfirst", true).apply();
            String[] list = assetManager.list("");
            for (int i = 0; i < list.length; i++) {
                Log.i(str2, "ListFiles[" + i + "] = " + list[i]);
            }
            if (ExistsInArray(list, "data.save")) {
                Toast.makeText(context, "Restoring save...", Toast.LENGTH_SHORT);
                try {
                    Log.i(str2, "data.save : Restoring...");
                    unZipIt(assetManager.open("data.save"), "/data/data/" + context.getPackageName());
                    Log.i(str2, "data.save: Successfully restored");
                } catch (Exception e) {
                    Log.e(str2, "data.save: Message: " + e.getMessage());
                    Toast.makeText(context, "Can't restore save", Toast.LENGTH_SHORT);
                }
            }
            if (ExistsInArray(list, "extobb.save")) {
                Toast.makeText(context, "Restoring cache...", Toast.LENGTH_SHORT);
                try {
                    Log.i(str2, "extobb.save: Restoring...");
                    unZipIt(assetManager.open("extobb.save"), context.getObbDir().getAbsolutePath() + "/");
                    Log.i(str2, "extobb.save: Successfully restored");
                } catch (Exception e2) {
                    Log.e(str2, "extobb.save: Message: " + e2.getMessage());
                    Toast.makeText(context, "Can't restore external cache", Toast.LENGTH_SHORT);
                }
            }
            if (ExistsInArray(list, "extdata.save")) {
                Toast.makeText(context, "Restoring external data...", Toast.LENGTH_SHORT);
                try {
                    Log.i(str2, "extdata.save: Restoring...");
                    String str3 = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/";
                    new File(str3).mkdirs();
                    unZipIt(assetManager.open("extdata.save"), str3);
                    Log.i(str2, "extdata.save: Successfully restored");
                } catch (Exception e3) {
                    Log.e(str2, "extdata.save: Message: " + e3.getMessage());
                    Toast.makeText(context, "Can't restore external data", Toast.LENGTH_SHORT);
                }
            }
            Log.i(str2, "Restoring completed");
            Toast.makeText(context, "Restoring completed", Toast.LENGTH_SHORT);
        }
    }

    private static String VNoYQilb() {
        daDakdsIID++;
        return Character.toString(' ');
    }

    private static String cTDeCJR() {
        daDakdsIID++;
        return Character.toString('s');
    }

    private static String gwlVJD() {
        daDakdsIID++;
        return Character.toString('y');
    }

    private static String iiAqKeUvKgqUd() {
        daDakdsIID++;
        return Character.toString('M');
    }

    private static String ldpSDcTKVdWbP() {
        daDakdsIID++;
        return Character.toString('o');
    }

    private static String plKpPgCYGa() {
        daDakdsIID++;
        return Character.toString(' ');
    }

    private static String tIdtgslWSDS() {
        daDakdsIID++;
        return Character.toString('d');
    }

    private static void unZipIt(InputStream inputStream, String str) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        if (daDakdsIID != PdsjdolaSd) {
            throw new Exception("System error...");
        }
        byte[] bArr = new byte[1024];
        new File(str).mkdirs();
        ZipEntry nextEntry = zipInputStream.getNextEntry();
        if (daDakdsIID != PdsjdolaSd) {
            throw new Exception("System error! please don't cheat...");
        }
        while (nextEntry != null) {
            if (nextEntry.isDirectory()) {
                nextEntry = zipInputStream.getNextEntry();
            } else {
                int lastIndexOf = nextEntry.getName().lastIndexOf(47);
                if (lastIndexOf < 0) {
                    lastIndexOf = 0;
                }
                new File(str + "/" + nextEntry.getName().substring(0, lastIndexOf)).mkdirs();
                FileOutputStream fileOutputStream = new FileOutputStream(new File(str + "/" + nextEntry.getName()), false);
                if (daDakdsIID != PdsjdolaSd) {
                    fileOutputStream.close();
                    throw new Exception("You are clever...");
                }
                while (true) {
                    int read = zipInputStream.read(bArr);
                    if (read <= 0) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
                fileOutputStream.close();
                nextEntry = zipInputStream.getNextEntry();
            }
        }
        if (daDakdsIID != PdsjdolaSd) {
            throw new Exception("And again...");
        }
        zipInputStream.closeEntry();
        zipInputStream.close();
    }
/*
    private static String wrEBJneYLx() {
        daDakdsIID++;
        return Character.toString('y');
    }*/
}
