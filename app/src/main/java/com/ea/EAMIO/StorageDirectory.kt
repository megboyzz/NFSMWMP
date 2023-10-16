package com.ea.EAMIO

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Environment
import android.util.Log
import com.ea.games.nfs13_na.BuildConfig


object StorageDirectory {
    @JvmStatic
    fun GetDedicatedDirectory() = ""

    @JvmStatic
    fun GetInternalStorageDirectory() = ""

    @JvmStatic
    fun GetPrimaryExternalStorageDirectory() = ""

    @JvmStatic
    fun GetPrimaryExternalStorageState() = ""

    @JvmStatic
    fun Shutdown() = ShutdownNativeImpl()

    private external fun ShutdownNativeImpl()

    @JvmStatic
    fun Startup() = StartupNativeImpl()

    private external fun StartupNativeImpl()
}
