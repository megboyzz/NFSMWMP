package com.ea.easp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.os.Handler
import android.view.KeyEvent
import android.view.ViewGroup

class EASPHandler(
    activity: Activity,
    viewGroup: ViewGroup,
    mGLSurfaceView: GLSurfaceView
) {

    @SuppressLint("StaticFieldLeak")
    companion object{
        @JvmStatic
        lateinit var mActivity: Activity
        @JvmStatic
        lateinit var mViewGroup: ViewGroup
    }

    init {
        mActivity = activity
        mViewGroup = viewGroup
    }
    private var mPhysicalKeyboard: PhysicalKeyboardAndroid? = null
    private val mTaskLauncher: TaskLauncher = TaskLauncher(Handler(), mGLSurfaceView)

    external fun initJNI()

    fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        Debug.Log.i(this.javaClass.name, "onActivityResult requestCode=$i, resultCode=$i2")
        //this.mFacebookAgent.authorizeCallback(i, i2, intent);
    }

    fun onConfigurationChanged(configuration: Configuration?) {
        mPhysicalKeyboard!!.onConfigurationChanged(configuration)
    }

    fun onCreate() {
        Debug.Log.d(this.javaClass.name, "onCreate()...")
        initJNI()
        DeviceInfoUtil.init()
        PackageUtil.init()
        //this.mFacebookAgent = new FacebookAgentJNI(mActivity, this.mTaskLauncher);
        //this.mFacebookAgent.init();
        //BrowserAndroid.Startup(mActivity, mViewGroup);
        //this.mAndroidMarketJNI = new MarketJNI(mActivity, this.mTaskLauncher, MarketJNI.StoreType.UNKNOWN);
        //this.mAndroidMarketJNI.init();
        mPhysicalKeyboard = PhysicalKeyboardAndroid(mTaskLauncher)
        Debug.Log.d(this.javaClass.name, "...onCreate()")
    }

    fun onDestroy() {
        Debug.Log.d(this.javaClass.name, "onDestroy()...")
        mPhysicalKeyboard = null
        //this.mAndroidMarketJNI.shutdown();
        //BrowserAndroid.Shutdown();
        //this.mFacebookAgent.shutdown();
        PackageUtil.shutdown()
        DeviceInfoUtil.shutdown()
        shutdownJNI()
        Debug.Log.d(this.javaClass.name, "...onDestroy()")
    }

    fun onKeyDown(i: Int, keyEvent: KeyEvent?): Boolean {
        return mPhysicalKeyboard!!.OnKeyDown(i, keyEvent)
    }

    fun onKeyUp(i: Int, keyEvent: KeyEvent?): Boolean {
        return mPhysicalKeyboard!!.OnKeyUp(i, keyEvent)
    }

    fun setLogEnabled(enabled: Boolean) {
        Debug.LogEnabled = enabled
    }

    external fun shutdownJNI()

}
