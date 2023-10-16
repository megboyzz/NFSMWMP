package com.ea.EAIO

import android.app.Activity
import android.content.res.AssetManager
import android.os.Environment

object EAIO {
    external fun Shutdown()
    fun Startup(activity: Activity) {
        StartupNativeImpl(
            activity.assets,
            Environment.getDataDirectory().absolutePath,
            activity.filesDir.absolutePath,
            Environment.getExternalStorageDirectory().absolutePath
        )
    }

    private external fun StartupNativeImpl(
        assetManager: AssetManager,
        dataDirectory: String,
        filesDir: String,
        externalStorage: String
    )
}
