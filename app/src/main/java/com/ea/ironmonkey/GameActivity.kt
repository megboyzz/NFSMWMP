package com.ea.ironmonkey

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.hardware.SensorManager
import android.media.AudioManager
import android.opengl.GLES20
import android.opengl.GLUtils
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import com.ea.EAIO.EAIO
import com.ea.EAMIO.StorageDirectory
import com.ea.easp.EASPHandler
import com.ea.games.nfs13_na.BuildConfig
import com.ea.games.nfs13_na.R
import com.ea.nimble.ApplicationLifecycle
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Locale
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.system.exitProcess


//TODO при заходе в магазин - вылет
class GameActivity : ComponentActivity(), DrawFrameListener {

    init {
        System.loadLibrary("nimble")
        System.loadLibrary("app")
    }

    private var splashDelay: Long = 0
    private var splashTimer: Long = 0

    private lateinit var easpHandler: EASPHandler
    private lateinit var gameRenderer: GameRenderer
    lateinit var runLoop: RunLoop
    lateinit var gameGLSurfaceView: GameGLSurfaceView
      //  get() = field
       // private set
    private lateinit var accelerometer: Accelerometer
    private lateinit var mWakeLock: WakeLock
    private lateinit var mAudioManager: AudioManager

    companion object {

        const val STATE_RESTORE_CONTEXT = 7
        const val STATE_GAME_START = 8
        private var oldState = 0

        @JvmField
        var state = 0

        //В нативном коде эти методы помечны как статические
        @JvmStatic
        fun GetDeviceName() = Build.MODEL

        @JvmStatic
        fun GetApplicationVersion() = BuildConfig.VERSION_NAME

        @JvmStatic
        fun GetDefaultLanguage() = Locale.getDefault().toString().substring(0, 2)

        @JvmStatic
        val osVersion = Build.VERSION.RELEASE
    }

    //Метод вызвывется из нативного кода поэтому нужно их существование
    fun installWallpaper() {}
    fun needInstallWallpaper() = false
    fun openURL(str: String) = Log.d("OpenURL", str)
    fun openURLinBrowser(str: String) = Log.d("OpenURLinBrowser", str)
    fun getDisplayMetrics() = resources.displayMetrics
    fun GetViewRoot() = window.decorView.getRootView().parent
    fun CallGC() {
        Log.d(this.localClassName, "Call garbage collector")
        System.gc()
    }
    //fun getGameGLSurfaceView() = gameGLSurfaceView
    fun getAccelerometer() = accelerometer


    private fun ForceHideVirtualKeyboard() {
        val currentFocus = currentFocus
        if (currentFocus != null) {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                currentFocus.windowToken,
                0
            )
        }
        window.setSoftInputMode(3)
    }


    private fun wakeLockAcquire() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(10, localClassName)
        if (!mWakeLock.isHeld) {
            try {
                mWakeLock.acquire(10*60*1000L /*10 minutes*/)
            } catch (e: SecurityException) {
                Log.w(this.localClassName, "Missing WAKE_LOCK permission.")
            }
        }
    }

    private fun wakeLockRelease() {
        if (mWakeLock != null && mWakeLock!!.isHeld) {
            try {
                mWakeLock!!.release()
            } catch (e: SecurityException) {
                Log.w(this.localClassName, "Missing WAKE_LOCK permission.")
            }
        }
    }

    fun IsSystemKey(key: Int) = false

    fun ShowMessage(message: String, strArr: Array<String?>, finish: Boolean) {
        Log.d(this.localClassName, "ShowMessage msg = $message finish = $finish")

        val dialog = AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setCancelable(false)
        dialog.setPositiveButton(strArr[0]) { _: DialogInterface?, _: Int ->
            if (finish) {
                finish()
            }
        }
        dialog.show()
    }

    fun calcPerformanceScore(
        cpuUsage: Float,
        processorCount: Int,
        screenWidth: Int,
        screenHeight: Int
    ): Float {
        // Log input parameters for debugging
        Log.i(
            this.localClassName,
            "calculatePerformanceScore($cpuUsage, $processorCount, $screenWidth, $screenHeight)"
        )

        // Calculate CPU-related score
        var cpuScore = 0.001f * cpuUsage * 3.0f
        if (processorCount > 1) {
            // If there is more than one processor, adjust the CPU score
            cpuScore += 0.001f * cpuUsage * min(processorCount.toFloat(), 3f) * 0.5f
        }

        // Calculate screen-related score
        val screenScore =
            sqrt((screenWidth * screenHeight).toDouble()) * 0.0010000000474974513 * 0.5

        // Log CPU information
        try {
            val cpuInfo = BufferedReader(FileReader("/proc/cpuinfo")).readLine()
            Log.i(this.localClassName, "CPU Info: $cpuInfo")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Log device model
        val deviceModel = Build.MODEL
        Log.i(this.localClassName, "Device Model: $deviceModel")

        // Calculate the final performance score and return it
        return ((1.0f + cpuScore) / (1.0f + screenScore.toFloat())) / 3
    }

    fun getPerformanceScore(): Float {
        var cpuFrequency = 0.0f
        var processorCount = 0

        // Read CPU information from file
        val cpuInfo = readCpuFile("present")
        if (cpuInfo.isNotEmpty()) {
            val cpuList = parseCpuList(cpuInfo)

            // Iterate through the CPU list and get max frequency
            for (cpuId: Int? in cpuList) {
                val maxFreq =
                    readCpuFile(String.format("cpu%d/cpufreq/cpuinfo_max_freq", cpuId))
                if (maxFreq.isNotEmpty()) {
                    try {
                        cpuFrequency = maxFreq.toInt().toFloat() * 0.001f
                        break
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                }
            }
            processorCount = cpuList.size
        }

        // Get screen metrics
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        // Calculate the performance score and return it
        return calcPerformanceScore(
            cpuFrequency,
            processorCount,
            displayMetrics.widthPixels,
            displayMetrics.heightPixels
        )
    }

    fun getTotalMemory(): Int {
        var totalMemoryInMB = 0
        val memInfoFile = File("/proc/meminfo")
        if (!memInfoFile.exists()) {
            Log.d("mem", "Meminfo file not found")
            return 0
        }
        try {
            val reader = BufferedReader(FileReader(memInfoFile))
            var line: String
            while ((reader.readLine().also { line = it }) != null) {
                val parts =
                    line.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (parts.size != 2) {
                    continue
                }
                val key = parts[0].trim { it <= ' ' }.lowercase(Locale.getDefault())
                val value = parts[1].trim { it <= ' ' }
                if (key != "memtotal") {
                    continue
                }
                val valueParts =
                    value.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (valueParts.isNotEmpty()) {
                    try {
                        totalMemoryInMB = valueParts[0].toInt() / 1024
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                }
                break // We found "MemTotal," so we can exit the loop
            }
            reader.close()
        } catch (e: Exception) {
            Log.e(
                this.localClassName,
                "Error reading system file: ${memInfoFile.absolutePath} (${e.message})"
            )
        }
        Log.d("mem", "Total Memory (MB): $totalMemoryInMB")
        return totalMemoryInMB
    }

    external fun nativeOnCreate()
    external fun nativeOnDestroy()
    external fun nativeOnPause()
    external fun nativeOnPhysicalKeyDown(i: Int, i2: Int)
    external fun nativeOnPhysicalKeyUp(i: Int, i2: Int)
    external fun nativeOnPhysicalKeyboardVisibilityChanged(z: Boolean)
    external fun nativeOnPhysicalNavigationVisibilityChanged(z: Boolean)
    external fun nativeOnRestart()
    external fun nativeOnResume()
    external fun nativeOnStart()
    external fun nativeOnStop()
    external fun nativeRestoreContext(): Boolean
    external fun nativeSurfaceChanged(gl10: GL10?, i: Int, i2: Int)
    external fun nativeSurfaceCreated(gl10: GL10?, eGLConfig: EGLConfig?)

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easpHandler!!.onActivityResult(requestCode, resultCode, data)
        ApplicationLifecycle.onActivityResult(requestCode, resultCode, data, this)
    }

    override fun onBackPressed() {
        //ApplicationLifecycle.onBackPressed()
    }

    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        val className = this.localClassName
        val currentState = lifecycle.currentState

        Log.i(className, "onCreate")
        Log.i(className, "GameActivity.state = $state")

        if (currentState == Lifecycle.State.DESTROYED) {
            Log.w(className, "onCreate called on destroyed app, finishing")
            finish()
            return
        }

        if (currentState >= Lifecycle.State.CREATED) {
            Log.w(className, "onCreate ignored, lifecycle is already $currentState")
            return
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        requestWindowFeature(1)
        Log.d("Model", Build.MODEL)
        mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val defaultSensor = sensorManager.getDefaultSensor(1)
        val rotation = window.windowManager.defaultDisplay.rotation

        accelerometer = Accelerometer(sensorManager, defaultSensor, rotation)

        gameGLSurfaceView = GameGLSurfaceView(this)
        gameRenderer = GameRenderer(this)
        gameRenderer.setDrawFrameListener(this)
        gameGLSurfaceView.setRenderer(gameRenderer)
        runLoop = RunLoop(gameGLSurfaceView)

        val frameLayout = ComposeFrameLayout(this, gameGLSurfaceView = gameGLSurfaceView)
        setContentView(frameLayout)

        Log.d(className, "Init EAIO/EAMIO")
        EAIO.Startup(this)
        StorageDirectory.Startup()

        Log.d(className, "Init EASPHandler")
        easpHandler = EASPHandler(this, frameLayout, gameGLSurfaceView)
        easpHandler.onCreate()

        ApplicationLifecycle.onActivityCreate(bundle, this)
        Log.d(className, "nativeOnCreate")
        nativeOnCreate()
    }


    public override fun onDestroy() {
        Log.i("Debug", "onDestroy")
        Log.i(this.localClassName, "onDestroy")
        super.onDestroy()
        easpHandler.onDestroy()
        if (state == 8) {
            ApplicationLifecycle.onActivityDestroy(this)
            nativeOnDestroy()
        }
        StorageDirectory.Shutdown()
        EAIO.Shutdown()
        exitProcess(0)
    }

    override fun onDrawFrame(gl10: GL10) {
        Log.d(this.localClassName, "onDrawFrame state=$state")
        var isStarted = false
        when (state) {
            STATE_RESTORE_CONTEXT -> {
                run {
                    //Log.i("state_in splash is null", (this.splash == null).toString() + "")
                    /*if (this.splash == null) {
                        this.splash = SplashScreen(this)
                        this.splash!!.init(
                            gl10,
                            this.gameRenderer!!.width,
                            this.gameRenderer!!.height
                        )
                        this.splashDelay = System.currentTimeMillis() + 2000
                    }*/
                    if (this.splashDelay < System.currentTimeMillis() /*&& hasWindowFocus()*/) {
                        if (oldState != 8) {
                            state = oldState
                        } else {
                            this.splashTimer = System.currentTimeMillis() + 300
                            state = 8
                        }
                    }
                }
                run {
                    if (this.splashTimer < System.currentTimeMillis() && nativeRestoreContext()) {
                        nativeOnStart()
                        nativeOnResume()
                        this.gameRenderer!!.setDrawFrameListener(null)
                        isStarted = true
                    }
                }
            }

            STATE_GAME_START -> {
                if (splashTimer < System.currentTimeMillis() && nativeRestoreContext()) {
                    nativeOnStart()
                    nativeOnResume()
                    gameRenderer.setDrawFrameListener(null)
                    isStarted = true
                }
            }
        }
        //GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glClear(16640)
       /* if (splash != null) {
            splash!!.draw(gl10, gameRenderer!!.width, gameRenderer!!.height)
        }
        if (isStarted && splash != null) {
            splash!!.destroy(gl10)
            splash = null
        }*/
    }

    override fun onKeyDown(i: Int, keyEvent: KeyEvent): Boolean {
        super.onKeyDown(i, keyEvent)
        if (state != 8) {
            return true
        }
        if ((i == 4 || i == 108) && keyEvent.repeatCount > 0) {
            return true
        }
        val scanCode = keyEvent.scanCode
        gameGLSurfaceView.queueEvent { nativeOnPhysicalKeyDown(i, scanCode) }
        return IsSystemKey(i)
    }

    override fun onKeyUp(i: Int, keyEvent: KeyEvent): Boolean {
        super.onKeyUp(i, keyEvent)
        if (state != 8) {
            return true
        }
        val scanCode = keyEvent.scanCode
        gameGLSurfaceView.queueEvent { nativeOnPhysicalKeyUp(i, scanCode) }
        return IsSystemKey(i)
    }

    public override fun onPause() {
        super.onPause()
        Log.i("Debug", "onPause")
        Log.i(this.localClassName, "onPause state=$state")
        mAudioManager!!.setStreamMute(3, true)
        gameGLSurfaceView.onPause()
        onWindowFocusChanged(false)
        ApplicationLifecycle.onActivityPause(this)
        nativeOnPause()
    }

    public override fun onRestart() {
        Log.i("Debug", "onRestart")
        Log.i(this.localClassName, "onRestart")
        Log.i(this.localClassName, "TouchEvent, GameActivity.state = $state")
        super.onRestart()
        ApplicationLifecycle.onActivityRestart(this)
        nativeOnRestart()
    }

    public override fun onResume() {
        Log.i("Debug", "onResume")
        Log.i(this.localClassName, "onResume")
        Log.i("check", "GameActivity.state = $state")
        super.onResume()
        if (state != 7) {
            oldState = state
            state = 8
            gameRenderer.setDrawFrameListener(this)
        }
        gameGLSurfaceView.onResume()
        ApplicationLifecycle.onActivityResume(this)
        nativeOnResume()
    }

    public override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        ApplicationLifecycle.onActivitySaveInstanceState(bundle, this)
    }

    public override fun onStart() {
        System.gc()
        Log.i("Debug", "onStart")
        Log.i(this.localClassName, "onStart")
        super.onStart()
        Log.i(this.localClassName, "onStart 1")
        wakeLockAcquire()
        Log.i(this.localClassName, "onStart 2")
        if (lifecycle.currentState < Lifecycle.State.STARTED || lifecycle.currentState == Lifecycle.State.DESTROYED) {
            Log.i(this.localClassName, "onStart 3")
            Log.i(this.localClassName, "nativeOnStart")
            nativeOnStart()
            Log.i(this.localClassName, "nativeOnStart - end")
            Log.i(this.localClassName, "ApplicationLifecycle")
            ApplicationLifecycle.onActivityStart(this)
        }
        Log.w(
            this.localClassName,
            "onStart ignored, lifecycle is already ${lifecycle.currentState}"
        )
    }

    public override fun onStop() {
        super.onStop()
        Log.i("Debug", "onStop")
        Log.i(this.localClassName, "onStop")
        if (lifecycle.currentState >= Lifecycle.State.RESUMED) {
            Log.w(
                this.localClassName,
                "onStop ignored, lifecycle is already ${lifecycle.currentState}"
            )
            return
        }
        wakeLockRelease()
        nativeOnStop()
        mAudioManager!!.setStreamMute(3, false)
        mAudioManager!!.setStreamSolo(3, false)
        if (mAudioManager!!.abandonAudioFocus(null) == 0) {
            Log.e(this.localClassName, "abandonAudioFocus failed")
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        Log.i("Debug", "onWindowFocusChanged")
        super.onWindowFocusChanged(hasFocus)
        Log.i(this.localClassName, "onWindowsFocusChanged($hasFocus) state=$state")
        if (hasFocus) {
            mAudioManager!!.setStreamMute(3, false)
        } else {
            ForceHideVirtualKeyboard()
            nativeOnPhysicalKeyDown(131, 0)
            nativeOnPhysicalKeyUp(131, 0)
            if (state == 8) {
                mAudioManager!!.setStreamMute(3, true)
                oldState = state
                state = 7
                gameRenderer.setDrawFrameListener(this)
            }
        }
        ApplicationLifecycle.onActivityWindowFocusChanged(hasFocus, this)
    }

    fun parseCpuList(cpuInfo: String): List<Int> {
        val arrayList = mutableListOf<Int>()
        val split = cpuInfo.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (str2: String in split) {
            if (str2.contains("-")) {
                val split2 = str2.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (split2.size >= 2) {
                    try {
                        val parseInt = split2[0].toInt()
                        val parseInt2 = split2[1].toInt()
                        for (i in parseInt..parseInt2) {
                            arrayList.add(i)
                        }
                    } catch (_: NumberFormatException) {
                    }
                }
            } else {
                try {
                    arrayList.add(str2.toInt())
                } catch (_: NumberFormatException) {
                }
            }
        }
        return arrayList
    }

    fun readCpuFile(str: String): String {
        val file = File("/sys/devices/system/cpu/$str")
        runCatching {
            return file.readLines()[0]
        }.onFailure {
            Log.e(
                this.localClassName,
                "Error reading system file: ${file.absoluteFile} (${it.message})"
            )
            return ""
        }
        return ""
    }

}
