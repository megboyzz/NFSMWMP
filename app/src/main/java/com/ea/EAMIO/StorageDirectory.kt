package com.ea.EAMIO

//На методы есть ссылка из нативного кода но они не вызываются и не несут полезной нагрузки
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
